package com.lightning.northstar.contraption.rocket;

import com.lightning.northstar.block.tech.rocket_station.RocketStationActor;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.*;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.contraption.rocket.packet.RocketSeatsPacket;
import com.lightning.northstar.contraption.rocket.packet.RocketSyncPacket;
import com.lightning.northstar.network.packet.ForceContraptionControlPacket;
import com.lightning.northstar.planet.Planet;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.lightning.northstar.util.NorthstarLang;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RocketContraptionEntity extends AbstractContraptionEntity implements IEntityWithComplexSpawn {

    /** The launch countdown, in ticks. */
    public final static int LAUNCH_COUNTDOWN_TICKS = 10 * 20;
    /** The delay during which the rocket stands still before and after changing dimensions, in ticks. */
    public final static int TRANSPORT_DELAY_TICKS = 2 * 20;
    /** Maximum velocity, in blocks per tick */
    public static final float MAX_SPEED = 5f; // 5 m/t = 100 m/s
    /** The speed at which the air sound reaches full volume, in blocks per tick */
    public static final float AIR_SOUND_SPEED = 1f; // 1 m/t = 20 m/s
    /** Minimum descent velocity, in blocks per tick */
    public static final float MIN_DESCEND_SPEED = 0.1f; // 0.1 bpt = 2 bps
    /** Minimum artificial gravity, used to prevent the rocket from being stuck in zero gravity dimensions */
    public static final float MINIMUM_GRAVITY = 0.05f;
    /** The maximum acceleration in m/s^2 */
    public static final float MAX_ACCELERATION = 0.5f;

    // Common-side
    private LaunchStatus status = LaunchStatus.WAITING;
    private int countdown = -1;
    /** Delay during dimension change to allow players to load, rocket stops moving when non-zero. Measure in ticks. */
    private int transportDelay;
    /** If the jump is key is being pressed during the landing phase */
    private boolean manualBreaking;
    /** If the thrusters should be turned on visually */
    private boolean thrustersEnabled;

    // Server-side
    private final Map<UUID, Vec3> virtualSeats = new HashMap<>();
    /** The rocket's vertical velocity, in blocks per tick. */
    private float velocity;
    private boolean shouldSync;
    private boolean hadSpaceDown;
    private int movementCheckCooldown;
    private BlockPos lastControlPos;
    private float landingHeight = Float.NaN;
    private UUID launchingPlayer;

    // Client-side
    @OnlyIn(Dist.CLIENT)
    private RocketAirSound flyingSound;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private int lerpSteps;

    public RocketContraptionEntity(EntityType<?> type, Level level) {
        super(type, level);

        noCulling = true;
    }

    public static RocketContraptionEntity create(Level world, Contraption contraption) {
        RocketContraptionEntity rce = new RocketContraptionEntity(NorthstarEntityTypes.ROCKET_CONTRAPTION.get(), world);
        rce.setContraption(contraption);
        return rce;
    }

    @Override
    protected void tickContraption() {
        RocketContraption contraption = getContraption();
        Level level = level();

        tickActors();

        if (lerpSteps > 0) {
            double nx = getX() + (lerpX - getX()) / lerpSteps;
            double ny = getY() + (lerpY - getY()) / lerpSteps;
            double nz = getZ() + (lerpZ - getZ()) / lerpSteps;
            setPos(nx, ny, nz);
            lerpSteps--;
        }

        if (!level.isClientSide()) {
            switch (getStatus()) {
                case WAITING -> {
                    if (movementCheckCooldown <= 0) {
                        float acceleration = level.northstar$gravity();

                        if (Mth.equal(acceleration, 0f) && isOutOfWorld()) {
                            acceleration = getY() >= (level.getMinBuildHeight() + level.getMaxBuildHeight()) * 0.5f ? MINIMUM_GRAVITY : -MINIMUM_GRAVITY;
                        }

                        velocity = (float) checkCollisionDistance(Mth.clamp(velocity - acceleration * 0.05f, -MAX_SPEED, MAX_SPEED));
                        if (Mth.equal(velocity, 0)) {
                            velocity = 0;
                            movementCheckCooldown = 2 * 20;
                        } else {
                            move(0, velocity, 0);
                        }
                    } else {
                        movementCheckCooldown--;
                    }
                }
                case COUNTDOWN -> {
                    countdown--;
                    if (countdown == 0) {
                        countdown = -1;

                        ServerLevel destination = ((ServerLevel) level).getServer().getLevel(contraption.destination.dimKey());
                        if (destination == null) {
                            setStatus(LaunchStatus.WAITING);
                            break;
                        }

                        if (!contraption.infiniteFuel) {
                            float fuel = contraption.calculateRequiredFuel(
                                    level.northstar$planet(), level.northstar$dimension(),
                                    destination.northstar$planet(), destination.northstar$dimension()
                            ).total();
                            RocketContraption.consumeFuel(contraption.getStorage().getFluids(), fuel);
                        }

                        ServerPlayer player = level.getServer().getPlayerList().getPlayer(launchingPlayer);
                        if (player != null) {
                            player.awardStat(NorthstarStats.ROCKET_LAUNCHES);
                        }

                        setStatus(LaunchStatus.ASCENDING);
                    }
                }
                case ASCENDING -> {
                    ResourceKey<Level> dimensionBelow = level.northstar$dimension().dimensionBelow();
                    boolean dropDown = dimensionBelow != null && dimensionBelow.location().equals(contraption.destination.dim());

                    // we can't go down too much or else entities will start dying to the void
                    if (dropDown ? getY() >= level.getMinBuildHeight() - 20 : getY() <= level.getMaxBuildHeight() + NorthstarConfigs.server().getCombinedTeleportHeight()) {
                        float acceleration;
                        if (dropDown) {
                            acceleration = -Math.max(level.northstar$gravityScale(), MINIMUM_GRAVITY);
                        } else {
                            acceleration = -level.northstar$gravity() + contraption.thrusterCount * NorthstarConfigs.server().thrusterPower.getF() / contraption.weight;
                        }

                        acceleration = Mth.clamp(acceleration, -MAX_ACCELERATION, MAX_ACCELERATION) * 0.05f; // m/s^2 -> m/s/t
                        velocity = Mth.clamp(velocity + acceleration, -MAX_SPEED, MAX_SPEED);
                        thrustersEnabled = !dropDown;

                        // TODO: This should probably have some kind of collision handling
                        move(0, velocity, 0);
                    } else {
                        velocity = 0;
                        thrustersEnabled = false;
                        landingHeight = Float.NaN;
                        transportDelay = Math.min(TRANSPORT_DELAY_TICKS, transportDelay + 1);
                        if (transportDelay == TRANSPORT_DELAY_TICKS) {
                            setStatus(LaunchStatus.DESCENDING);

                            for (Entity passenger : getPassengers()) {
                                if (passenger instanceof Player player) {
                                    player.awardStat(NorthstarStats.ROCKET_TRAVELS);
                                }
                            }

                            ServerLevel targetLevel = ((ServerLevel) level).getServer().getLevel(contraption.destination.dimKey());
                            if (targetLevel != null) {
                                dimensionBelow = targetLevel.northstar$dimension().dimensionBelow();
                                boolean fromBelow = dimensionBelow != null && dimensionBelow.location().equals(contraption.origin.dim());

                                double height = fromBelow ?
                                        targetLevel.getMinBuildHeight() - 20 :
                                        targetLevel.getMaxBuildHeight() + NorthstarConfigs.server().getCombinedTeleportHeight() - 100;
                                RocketDestination dest = contraption.destination;
                                BlockPos anchor = dest.pos() != null && dest.dir() != null ?
                                        getAnchorPosition(dest.pos(), dest.dir()) :
                                        BlockPos.containing(position());
                                Vec3 pos = Vec3.atLowerCornerOf(anchor).with(Direction.Axis.Y, height);

                                if (!targetLevel.equals(level)) {
                                    changeDimension(new DimensionTransition(targetLevel, pos, Vec3.ZERO, getYRot(), getXRot(), DimensionTransition.DO_NOTHING));
                                } else {
                                    teleportTo(pos.x(), pos.y(), pos.z());
                                }
                            }
                        }
                    }
                }
                case DESCENDING -> {
                    transportDelay = Math.max(0, transportDelay - 1);
                    if (transportDelay > 0) {
                        break;
                    }

                    PlanetDimension dimension = level.northstar$dimension();
                    ResourceKey<Level> dimensionBelow = dimension.dimensionBelow();
                    boolean fromBelow = dimensionBelow != null && dimensionBelow.location().equals(contraption.origin.dim());
                    boolean doStop = false;
                    boolean hasCollided = false;

                    float targetPoint = Float.POSITIVE_INFINITY;
                    if (contraption.destination != null && contraption.destination.pos() != null && dimension.isOrbit()) {
                        targetPoint = contraption.destination.pos().getY();
                    }
                    if (targetPoint == Float.POSITIVE_INFINITY) {
                        if (Float.isNaN(landingHeight) || level.getGameTime() % 20 == 0) {
                            landingHeight = calculateLandingHeight(fromBelow);
                        }
                        targetPoint = landingHeight;
                    }
                    if (targetPoint == Float.POSITIVE_INFINITY && dimension.isOrbit()) {
                        targetPoint = (level.getMinBuildHeight() + level.getMaxBuildHeight()) / 2f;
                    }

                    if (fromBelow) {
                        // TODO: The player should probably have some way of controlling this like regular landings

                        float delta = targetPoint - (float) getY();

                        if (delta <= 1) {
                            thrustersEnabled = false;
                            velocity = 0;
                            doStop = true;
                        } else if (delta <= 100) {
                            thrustersEnabled = false;
                            velocity = Mth.map(delta, 100, 0, MAX_SPEED, 0);
                        } else {
                            thrustersEnabled = !level.northstar$isZeroGravity();
                            velocity = 2;
                        }

                        move(0, velocity, 0);
                    } else {
                        float acceleration = -Math.max(level.northstar$gravity(), MINIMUM_GRAVITY);

                        float deceleration = contraption.thrusterCount * NorthstarConfigs.server().thrusterPower.getF() / contraption.weight;
                        float safeDistance = velocity * velocity / (2f * Mth.clamp(acceleration + deceleration, -MAX_ACCELERATION, MAX_ACCELERATION)) * 20;

                        if (!contraption.hasAutoLander) {
                            ServerPlayer controlling = (ServerPlayer) getControllingPlayerInstance();
                            ChatFormatting red = level.getGameTime() / 10 % 2 == 0 ? ChatFormatting.RED : ChatFormatting.DARK_RED;
                            if (controlling != null) {
                                // 20% warning margin
                                if (getY() <= targetPoint + safeDistance * 1.2 && !manualBreaking) {
                                    controlling.northstar$showTitle(
                                            Component.empty(),
                                            Component.translatable("northstar.contraption.rocket.hold_to_slow_down", Component.keybind("key.jump"))
                                                    .withStyle(red),
                                            0,
                                            20,
                                            20
                                    );
                                }

                                float safeSpeed = NorthstarConfigs.server().landingMaxSafeSpeed.getF();
                                Component information = Component.translatable(
                                        "northstar.contraption.rocket.speed_information",
                                        Component.literal("%2.1f m/s".formatted(velocity * 20))
                                                .withStyle(-velocity * 20 <= safeSpeed ? ChatFormatting.GREEN : ChatFormatting.RED),
                                        NorthstarLang.numberDirect(getY() - targetPoint)
                                                .append(" m")
                                                .withStyle(ChatFormatting.AQUA)
                                );
                                controlling.sendSystemMessage(information, true);
                            } else {
                                for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, getBoundingBox())) {
                                    player.sendSystemMessage(Component.translatable("northstar.contraption.rocket.slow_no_pilot")
                                            .withStyle(red), true);
                                }
                            }
                        }

                        boolean shouldSlow = manualBreaking || (contraption.hasAutoLander && getY() <= targetPoint + safeDistance + 5);
                        thrustersEnabled = shouldSlow;
                        if (shouldSlow) {
                            acceleration += deceleration;
                        }

                        acceleration = Mth.clamp(acceleration, -MAX_ACCELERATION, MAX_ACCELERATION) * 0.05f; // m/s^2 -> m/s/t
                        velocity = Mth.clamp(velocity + acceleration, -MAX_SPEED, -MIN_DESCEND_SPEED);

                        double movement = checkCollisionDistance(velocity);
                        move(0, movement, 0);

                        hasCollided = (dimension.isOrbit() && getY() <= targetPoint) || velocity != movement;
                        doStop = hasCollided;
                    }

                    if (doStop) {
                        MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor = contraption.getActorAt(BlockPos.ZERO);
                        if (actor != null) {
                            RocketStationActor station = RocketStationActor.get(actor.right);
                            if (NorthstarItems.RETURN_TICKET.isIn(station.container.getItem(0))) {
                                station.container.setItem(0, ItemStack.EMPTY);
                            } else {
                                createReturnTicket();
                            }
                        }

                        setStatus(LaunchStatus.WAITING);
                        sendSyncPacket();
                        dismountPassengers();

                        if (hasCollided && Math.abs(velocity) * 20 > NorthstarConfigs.server().landingMaxSafeSpeed.get()) {
                            ServerPlayer player = level.getServer().getPlayerList().getPlayer(launchingPlayer);
                            if (player != null) {
                                player.awardStat(NorthstarStats.ROCKET_CRASHES);
                            }

                            explode();
                        }

                        velocity = 0;
                        launchingPlayer = null;
                        thrustersEnabled = false;
                        landingHeight = Float.NaN;
                        contraption.origin = null;
                    }
                }
            }

            if (status != LaunchStatus.WAITING || this.shouldSync || tickCount % 100 == 0) {
                this.shouldSync = false;
                sendSyncPacket();
            }
        } else {
            RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::tickAirSound);
            if (thrustersEnabled && level.getGameTime() % 20 == 0) {
                // FIXME: ROCKET_LANDING sound should be played when descending but loop is too short
                level.playLocalSound(getX(), getY(), getZ(), NorthstarSounds.ROCKET_BLAST.get(), SoundSource.BLOCKS, 1f, 0, false);
            }
        }
    }

    @Override
    public boolean control(BlockPos controlsLocalPos, Collection<Integer> heldControls, Player player) {
        if (level().isClientSide())
            return true;
        if (player.isSpectator() || !toGlobalVector(VecHelper.getCenterOf(controlsLocalPos), 1).closerThan(player.position(), 8) || heldControls.contains(5))
            return false;

        boolean spaceDown = heldControls.contains(4);
        RocketContraption contraption = getContraption();

        switch (getStatus()) {
            case WAITING -> {
                if (spaceDown && !hadSpaceDown) {
                    RocketDestination destination = contraption.destination;

                    if (destination == null) {
                        player.sendSystemMessage(Component.translatable("northstar.contraption.rocket.launch.no_destination").withStyle(ChatFormatting.RED));
                        break;
                    }
                    ServerLevel destinationLevel = ((ServerLevel) level()).getServer().getLevel(destination.dimKey());
                    if (destinationLevel == null) {
                        player.sendSystemMessage(Component.translatable("northstar.contraption.rocket.launch.level_unloaded").withStyle(ChatFormatting.RED));
                        break;
                    }

                    if (destination.dimKey() == level().dimension() &&
                        (destination.pos() == null ||
                         getAnchorPosition(destination.pos(), destination.dir()).atY(0).equals(new BlockPos(getBlockX(), 0, getBlockZ())))) {
                        player.sendSystemMessage(Component.translatable("northstar.contraption.rocket.launch.same_destination").withStyle(ChatFormatting.RED));
                        break;
                    }

                    // if the position is null it's a direct flight (keep same coordinates), if direction is null it's an absolute position from a return ticket (so no marker)
                    if (destination.pos() != null && destination.dir() != null && !destinationLevel.getPoiManager().exists(destination.pos(), holder -> holder.is(NorthstarPois.ROCKET_WAYPOINT.getId()))) {
                        player.sendSystemMessage(Component.translatable("northstar.contraption.rocket.launch.missing_waypoint").withStyle(ChatFormatting.RED));
                        break;
                    }

                    if (!contraption.isClearForLaunch(level(), destinationLevel)) {
                        player.sendSystemMessage(Component.translatable("northstar.contraption.rocket.launch.requirements").withStyle(ChatFormatting.RED));
                        break;
                    }

                    mountPassengers();

                    contraption.origin = new RocketDestination(level().dimension().location(), blockPosition(), null);
                    launchingPlayer = player.getUUID();
                    countdown = LAUNCH_COUNTDOWN_TICKS + 1;
                    setStatus(LaunchStatus.COUNTDOWN);
                }
            }
            case COUNTDOWN -> {
                if (spaceDown && !hadSpaceDown) {
                    countdown = -1;
                    launchingPlayer = null;
                    setStatus(LaunchStatus.WAITING);
                    sendSyncPacket();
                    dismountPassengers();

                    for (ServerPlayer p : level().getEntitiesOfClass(ServerPlayer.class, getBoundingBox().inflate(NorthstarConfigs.server().launchCountdownRadius.get()))) {
                        p.sendSystemMessage(Component.translatable("northstar.contraption.rocket.launch_aborted"), true);
                        p.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 1, 1);
                    }
                }
            }
            case DESCENDING -> manualBreaking = spaceDown;
        }

        hadSpaceDown = spaceDown;

        return true;
    }

    @Override
    public boolean startControlling(BlockPos controlsLocalPos, @Nullable Player player) {
        return player != null && !player.isSpectator() && getControllingPlayer().isEmpty();
    }

    @Override
    public void stopControlling(BlockPos controlsLocalPos) {
        super.stopControlling(controlsLocalPos);
        manualBreaking = false;
    }

    @Override
    protected boolean isActorActive(MovementContext context, MovementBehaviour actor) {
        // Most actors need to be disabled to prevent the rocket from stalling or possible exploits
        // (eg: extracting fuel with a portable fluid interface after initiating countdown would cause both).
        if (status == LaunchStatus.WAITING ||
            NorthstarBlockTags.ROCKET_ALWAYS_ACTIVE_ACTORS.matches(context.state) ||
            (actor instanceof RocketMovementBehaviour a && a.isActive(status))) {
            return super.isActorActive(context, actor);
        }
        return false;
    }

    private void explode() {
        Level level = level();
        boolean fire = level.northstar$oxygen().hasOxygen();
        List<BlockPos> blocks = new ArrayList<>(contraption.getBlocks().keySet());
        int count = Mth.ceil(blocks.size() * NorthstarConfigs.server().rocketExplosionFraction.get());
        int size = NorthstarConfigs.server().rocketExplosionSize.get();

        disassemble();

        for (int i = 0; i < count; i++) {
            BlockPos pos = blocks.get(level.random.nextInt(blocks.size()));
            level.explode(this, getX() + pos.getX(), getY() + pos.getY(), getZ() + pos.getZ(), size, fire, Level.ExplosionInteraction.MOB);
        }
    }

    @ApiStatus.Internal
    @OnlyIn(Dist.CLIENT)
    public void onSync(RocketSyncPacket packet) {
        status = packet.status();
        velocity = packet.velocity();
        countdown = packet.countdown();
        thrustersEnabled = packet.thrustersEnabled();

        Minecraft minecraft = Minecraft.getInstance();
        if (countdown % 20 == 0 &&
            minecraft.player != null &&
            minecraft.player.getBoundingBox().intersects(getBoundingBox().inflate(NorthstarConfigs.server().launchCountdownRadius.get()))) {
            var color = switch (countdown / 20) {
                case 0 -> ChatFormatting.DARK_RED;
                case 1 -> ChatFormatting.RED;
                case 2 -> ChatFormatting.GOLD;
                case 3 -> ChatFormatting.YELLOW;
                case 4 -> ChatFormatting.GREEN;
                default -> ChatFormatting.WHITE;
            };
            Component message = Component.translatable("northstar.contraption.rocket.launching_countdown", countdown / 20)
                    .withStyle(color);

            minecraft.gui.setOverlayMessage(message, false);
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_PLING, 1f));
        }
    }

    @Override
    protected void writeAdditional(CompoundTag compound, HolderLookup.Provider registries, boolean spawnPacket) {
        super.writeAdditional(compound, registries, spawnPacket);

        compound.putString("Status", status.getSerializedName());
        compound.putInt("Countdown", countdown);
        compound.putInt("TransportDelay", transportDelay);
        compound.putFloat("Velocity", velocity);
        if (lastControlPos != null) compound.put("LastControlPos", NbtUtils.writeBlockPos(lastControlPos));
        if (launchingPlayer != null) compound.putUUID("launchingPlayer", launchingPlayer);

        compound.put("VirtualSeats", NBTHelper.writeCompoundList(virtualSeats.entrySet(), entry -> {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("Entity", entry.getKey());
            tag.putDouble("X", entry.getValue().x());
            tag.putDouble("Y", entry.getValue().y());
            tag.putDouble("Z", entry.getValue().z());
            return tag;
        }));
    }

    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnData) {
        super.readAdditional(compound, spawnData);

        status = Objects.requireNonNullElse(LaunchStatus.byName(compound.getString("Status")), LaunchStatus.WAITING);
        countdown = compound.getInt("Countdown");
        transportDelay = compound.getInt("TransportDelay");
        velocity = compound.getFloat("Velocity");
        lastControlPos = NbtUtils.readBlockPos(compound, "LastControlPos").orElse(null);
        launchingPlayer = compound.hasUUID("LaunchingPlayer") ? compound.getUUID("LaunchingPlayer") : null;
        manualBreaking = false;

        virtualSeats.clear();
        NBTHelper.iterateCompoundList(compound.getList("VirtualSeats", Tag.TAG_COMPOUND), tag -> virtualSeats.put(tag.getUUID("Entity"), new Vec3(tag.getDouble("X"), tag.getDouble("Y"), tag.getDouble("Z"))));
    }

    @Override
    public @Nullable Entity changeDimension(DimensionTransition transition) {
        record PassengerData(Entity entity, @Nullable Integer seat, @Nullable Vec3 offset) {
        }
        List<PassengerData> passengers = new ArrayList<>();

        for (Entity passenger : getPassengers()) {
            Integer seat = contraption.getSeatMapping().get(passenger.getUUID());
            Vec3 offset = virtualSeats.get(passenger.getUUID());
            passengers.add(new PassengerData(passenger, seat, offset));
        }

        RocketContraptionEntity newRocket = (RocketContraptionEntity) super.changeDimension(transition);
        if (newRocket == null) {
            return null; // huh?
        }
        for (PassengerData data : passengers) {
            Entity newPassenger = data.entity.changeDimension(new DimensionTransition(transition.newLevel(), transition.pos(), transition.speed(), data.entity.getYRot(), data.entity.getXRot(), DimensionTransition.DO_NOTHING));
            if (newPassenger == null) {
                continue; // shouldn't happen unless this method is misused by another mod
            }

            if (data.seat != null) {
                newRocket.addSittingPassenger(newPassenger, data.seat);
            } else if (data.offset != null) {
                newPassenger.startRiding(newRocket, true);
                newRocket.virtualSeats.put(newPassenger.getUUID(), data.offset);
            }
        }

        if (newRocket.getControllingPlayerInstance() instanceof ServerPlayer player && lastControlPos != null) {
            CatnipServices.NETWORK.sendToClient(player, new ForceContraptionControlPacket(getId(), lastControlPos));
        }
        return newRocket;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity entityLiving) {
        return super.getDismountLocationForPassenger(entityLiving);
    }

    public BlockPos getAnchorPosition(BlockPos waypointPos, @Nullable Direction waypointDir) {
        if (waypointDir == null) {
            return waypointPos;
        }

        AABB bounds = contraption.bounds;
        if (bounds == null) return waypointPos;

        int offsetY = (int) -bounds.minY;
        int offsetX;
        int offsetZ;

        if (waypointDir.getAxis() == Direction.Axis.X) {
            offsetX = Mth.floor(waypointDir == Direction.WEST ? bounds.minX - 1 : bounds.maxX);
            offsetZ = Mth.floor((bounds.minZ + bounds.maxZ) * 0.5);
        } else {
            offsetX = Mth.floor((bounds.minX + bounds.maxX) * 0.5);
            offsetZ = Mth.floor(waypointDir == Direction.NORTH ? bounds.minZ - 1 : bounds.maxZ);
        }

        return waypointPos.offset(-offsetX, offsetY, -offsetZ);
    }

    private void createReturnTicket() {
        if (!NorthstarConfigs.server().enableReturnTicketCreation.get()) {
            return;
        }

        RocketDestination origin = getContraption().origin;
        Planet planet;
        PlanetDimension dimension;
        if (origin == null ||
            (planet = level().northstar$getPlanetTracker().getPlanetByLevel(origin.dimKey())) == null ||
            (dimension = level().northstar$getPlanetTracker().getDimensionByLevel(origin.dim())) == null) {
            return;
        }

        ItemStack ticket = NorthstarItems.RETURN_TICKET.asStack(1);
        ticket.set(DataComponents.ITEM_NAME, Component.translatable("item.northstar.return_ticket.planet", planet.getDimensionName(dimension)));
        ticket.set(NorthstarDataComponents.RETURN_DESTINATION, origin);

        // Put the item in the rocket station if possible
        MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor = contraption.getActorAt(BlockPos.ZERO);
        if (actor != null) {
            RocketStationActor rocketStation = RocketStationActor.get(actor.right);
            if (rocketStation.container.getItem(1).isEmpty()) {
                rocketStation.container.setItem(1, ticket);
                return;
            }
        }

        // Try to find a non-obstructed side to drop the reading on
        Direction[] directions = new Direction[] { Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.DOWN };
        for (Direction direction : directions) {
            if (contraption.getBlocks().containsKey(BlockPos.ZERO.relative(direction))) {
                continue;
            }
            level().addFreshEntity(new ItemEntity(level(), getX() + direction.getStepX(), getY() + direction.getStepY(), getZ() + direction.getStepZ(), ticket));
            return;
        }

        // Otherwise just spawn it inside the rocket station, this should usually never happen
        level().addFreshEntity(new ItemEntity(level(), getX(), getY(), getZ(), ticket));
    }

    private float calculateLandingHeight(boolean upwards) {
        Level level = level();
        BlockPos anchor = BlockPos.containing(position());
        Object2IntMap<BlockPos> columns = new Object2IntOpenHashMap<>();
        columns.defaultReturnValue(upwards ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        for (BlockPos pos : getContraption().getOrCreateColliders(level, upwards ? Direction.UP : Direction.DOWN)) {
            BlockPos key = pos.atY(0);
            if (upwards) {
                if (pos.getY() > columns.getInt(key)) {
                    columns.put(key, pos.getY());
                }
            } else if (pos.getY() < columns.getInt(key)) {
                columns.put(key, pos.getY());
            }
        }

        if (upwards) {
            MutableBlockPos pos = new MutableBlockPos();
            int lowest = Integer.MAX_VALUE;

            for (Object2IntMap.Entry<BlockPos> entry : columns.object2IntEntrySet()) {
                BlockPos offset = entry.getKey();
                pos.set(anchor.getX() + offset.getX(), level.getMinBuildHeight(), anchor.getZ() + offset.getZ());
                while (pos.getY() < level.getMaxBuildHeight() && level.getBlockState(pos).isAir()) {
                    pos.setY(pos.getY() + 1);
                }
                if (pos.getY() != level.getMaxBuildHeight() && pos.getY() - entry.getIntValue() < lowest) {
                    lowest = pos.getY() - entry.getIntValue();
                }
            }

            return lowest == Integer.MAX_VALUE ? Float.POSITIVE_INFINITY : lowest;
        }

        int highest = Integer.MIN_VALUE;
        for (Object2IntMap.Entry<BlockPos> entry : columns.object2IntEntrySet()) {
            BlockPos offset = entry.getKey();
            int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING, anchor.getX() + offset.getX(), anchor.getZ() + offset.getZ()) - entry.getIntValue();
            if (height != level.getMinBuildHeight() - entry.getIntValue() && height > highest) {
                highest = height;
            }
        }
        return highest == Integer.MIN_VALUE ? Float.POSITIVE_INFINITY : highest;
    }

    private void sendSyncPacket() {
        CatnipServices.NETWORK.sendToClientsTrackingEntity(this, new RocketSyncPacket(getId(), countdown, velocity, status, thrustersEnabled));
    }

    @OnlyIn(Dist.CLIENT)
    private void tickAirSound() {
        if ((flyingSound == null || flyingSound.isStopped()) && RocketAirSound.shouldPlayFor(this)) {
            flyingSound = new RocketAirSound(SoundEvents.ELYTRA_FLYING, this);
            Minecraft.getInstance().getSoundManager().play(flyingSound);
        }
    }

    public boolean isOutOfWorld() {
        BlockPos pos = BlockPos.containing(position());
        return isOutOfWorld(pos, Direction.DOWN) || isOutOfWorld(pos.relative(Direction.UP), Direction.UP);
    }

    private boolean isOutOfWorld(BlockPos pos, Direction direction) {
        Level level = level();
        for (BlockPos collider : getContraption().getOrCreateColliders(level, direction)) {
            if (level.isOutsideBuildHeight(pos.getY() + collider.getY())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the maximum distance the rocket can travel vertically before hitting a block. <br>
     * Note: for simplicity and performance reasons, this assumes that the rocket is aligned to the grid.
     * If this isn't the case (eg: rocket was teleported manually) this function will skip blocks.
     *
     * @param delta the distance to travel along the Y axis
     * @return the distance after which the collision happens or {@code delta} if no collision happens
     */
    private double checkCollisionDistance(double delta) {
        if (Mth.equal(delta, 0)) {
            return 0;
        }

        int blocksToCheck = Mth.ceil(Math.abs(delta));
        Direction direction = delta < 0 ? Direction.DOWN : Direction.UP;

        Level level = level();
        Set<BlockPos> colliders = getContraption().getOrCreateColliders(level, direction);
        BlockPos worldAnchor = BlockPos.containing(position());

        for (int i = 0; i <= blocksToCheck; i++) {
            double distance = delta;

            for (BlockPos collider : colliders) {
                BlockPos worldPos = worldAnchor.offset(collider.getX(), collider.getY() + direction.getStepY() * i, collider.getZ());

                BlockState collidedState = level.getBlockState(worldPos);
                BlockState collidingState = contraption.getBlocks().get(collider).state();

                VoxelShape collidedShape = collidedState.getCollisionShape(level, worldPos);
                VoxelShape collidingShape = collidingState.getCollisionShape(level, collider);
                if (collidedShape.isEmpty() || collidingShape.isEmpty()) {
                    continue;
                }

                if (delta < 0) {
                    collidedShape = collidedShape.move(0, -(i + Mth.frac(position().y())), 0);
                    for (AABB box : collidingShape.toAabbs()) {
                        double result = collidedShape.collide(Direction.Axis.Y, box, -(i + 1));
                        if (result >= distance) {
                            distance = result;
                        }
                    }
                } else {
                    collidedShape = collidedShape.move(0, i - Mth.frac(position().y()), 0);
                    for (AABB box : collidingShape.toAabbs()) {
                        double result = collidedShape.collide(Direction.Axis.Y, box, (i + 1));
                        if (result <= distance) {
                            distance = result;
                        }
                    }
                }

            }

            if (distance != delta) {
                return distance;
            }
        }

        return delta;
    }

    @Override
    public RocketContraption getContraption() {
        return (RocketContraption) contraption;
    }

    @Override
    protected void setContraption(Contraption contraption) {
        if (!(contraption instanceof RocketContraption))
            throw new IllegalArgumentException("RocketContraptionEntity requires a RocketContraption contraption");
        super.setContraption(contraption);
    }

    @Override
    public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
        return localPos;
    }

    @Override
    public Vec3 reverseRotation(Vec3 localPos, float partialTicks) {
        return localPos;
    }

    @Override
    protected StructureTransform makeStructureTransform() {
        return new StructureTransform(BlockPos.containing(getAnchorVec().add(.5, .5, .5)), 0, 0, 0);
    }

    @Override
    protected float getStalledAngle() {
        return 0;
    }

    @Override
    protected void handleStallInformation(double x, double y, double z, float angle) {
        setPosRaw(x, y, z);
    }

    @Override
    public ContraptionRotationState getRotationState() {
        return ContraptionRotationState.NONE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
        TransformStack.of(matrixStack).nudge(getId());
    }

    @Override
    protected void lerpPositionAndRotationStep(int steps, double targetX, double targetY, double targetZ, double targetYRot, double targetXRot) {
        super.lerpPositionAndRotationStep(steps, targetX, targetY, targetZ, targetYRot, targetXRot);
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpSteps = steps;
    }

    private void mountPassengers() {
        for (Entity passenger : level().getEntities(this, getBoundingBox())) {
            if (passenger.startRiding(this, true)) {
                virtualSeats.put(passenger.getUUID(), passenger.position().subtract(position()).add(0, -passenger.getVehicleAttachmentPoint(this).y, 0));
            }
        }

        CatnipServices.NETWORK.sendToClientsTrackingEntity(this, new RocketSeatsPacket(getId(), virtualSeats));
    }

    private void dismountPassengers() {
        for (UUID uuid : List.copyOf(virtualSeats.keySet())) {
            Entity entity = ((ServerLevel) level()).getEntity(uuid);
            if (entity != null) {
                entity.stopRiding();
            }
        }
        virtualSeats.clear();

        CatnipServices.NETWORK.sendToClientsTrackingEntity(this, new RocketSeatsPacket(getId(), Map.of()));
    }

    @Override
    public Vec3 getPassengerPosition(Entity passenger, float partialTick) {
        Vec3 offset = virtualSeats.get(passenger.getUUID());
        if (offset != null) {
            return toGlobalVector(offset, partialTick);
        }
        return super.getPassengerPosition(passenger, partialTick);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        virtualSeats.remove(passenger.getUUID());
    }

    @Nullable
    public Player getControllingPlayerInstance() {
        Optional<UUID> controlling = getControllingPlayer();
        return controlling.isPresent() ? level().getPlayerByUUID(controlling.get()) : null;
    }

    public LaunchStatus getStatus() {
        return status;
    }

    public void setStatus(LaunchStatus status) {
        this.status = status;
        this.shouldSync = true;
    }

    public int getCountdown() {
        return countdown;
    }

    public float getVelocity() {
        return velocity;
    }

    public boolean areThrustersEnabled() {
        return thrustersEnabled;
    }

    public Map<UUID, Vec3> getVirtualSeats() {
        return virtualSeats;
    }

}
