package com.lightning.northstar.contraptions;

import com.lightning.northstar.content.NorthstarEntityTypes;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.contraptions.packets.RocketContraptionQuickSyncPacket;
import com.lightning.northstar.contraptions.packets.RocketContraptionSyncPacket;
import com.lightning.northstar.world.TemperatureStuff;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPackets;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionBlockChangedPacket;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.kinetics.base.BlockBreakingMovementBehaviour;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.*;

import static com.lightning.northstar.Northstar.LOGGER;

public class RocketContraptionEntity extends AbstractContraptionEntity implements IEntityAdditionalSpawnData {

    public boolean auto_land_mode;
    double clientOffsetDiff;
    double axisMotion;
    public boolean launchingMode;
    public boolean landingMode;
    boolean fuelBurned = false;
    boolean printed = false;
    public boolean blasting = false;
    public boolean slowing = false;
    public boolean hasExploded = false;
    public boolean isUsingTicket = false;
    int i = 90;
    int soundTime = 0;
    int cooldown = 0;
    int cooldownLength = 100;
    private int maxSpeed = 5;
    /**
     * In ticks
     */
    private int launchtime = 0;
    public int visualEngineCount = 0;
    private boolean activeLaunch = false;
    public Player owner;
    public UUID ownerID;
    public double sequencedOffsetLimit;
    public float lift_vel = 0.5f;
    public float final_lift_vel = lift_vel - 0.5f;
    public ResourceKey<Level> home;
    public ResourceKey<Level> destination;
    CompoundTag serialisedEntity;
    Map<Integer, CompoundTag> serialisedPassengers;
    public WeakReference<RocketContraptionEntity> entity;
    List<UUID> clientSide_entitiesThatMustBeReseated = null;
    public byte dissasemblyTicks = 0;

    public int getLaunchTime() {
        return launchtime;
    }

    @SuppressWarnings("unused")
    private Vec3 serverPrevPos;
    public BlockPos localControlsPos;
    private List<Entity> entitiesWithinContraption = new ArrayList<>();

    public Map<UUID, Vec3> getPassengerOffsets() {
        return this.getRocketPassengerOffsets();
    }

    public void setPassengerOffsets(Map<UUID, Vec3> set) {
        this.setRocketPassengerOffsets(set);
    }

    public List<Entity> getEntitiesWithinContraption() {
        return entitiesWithinContraption;
    }

    public RocketContraptionEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        noCulling = true;

        sequencedOffsetLimit = -1;
        lift_vel = 0.5f;
        launchingMode = true;
        landingMode = false;
    }

    public static RocketContraptionEntity create(Level world, Contraption contraption) {
        RocketContraptionEntity entity = new RocketContraptionEntity(NorthstarEntityTypes.ROCKET_CONTRAPTION.get(), world);
        entity.setContraption(contraption);
        return entity;
    }

    public static double getSlowdownHeightThreshold(RocketContraptionEntity rocketEntity) {
        return rocketEntity.level().getMaxBuildHeight() + 150;
    }

    public void limitMovement(double maxOffset) {
        sequencedOffsetLimit = maxOffset;
    }


    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        CompoundTag nbt = additionalData.readAnySizeNbt();
        if (nbt != null) {
            readAdditional(nbt, true);
        }
    }

    @Override
    protected void tickContraption() {
        if (contraption instanceof RocketContraption contrap) {
            //Check for entities within the contraption
            //Ideally, we shouldnt have to do this every tick, but there are mixins that rely on this (gravityStuffMixin)
            entitiesWithinContraption = this.level().getEntities(this, this.getBoundingBox());


            if (launchtime > 0 && activeLaunch) {
//                System.out.println("Launchtime: " + launchtime);
                launchtime--;
            }

            if (dissasemblyTicks > 0) {
                setDeltaMovement(0, 0, 0);
                dissasemblyTicks--;
                System.out.println("Dissasembling in " + dissasemblyTicks + " ticks");
                if (!level().isClientSide && dissasemblyTicks == 0) {
                    disassemble();
                }
                return;
            }

            //TODO: Other entities that arent in seats (animals that I have tested) always get removed or dissapear whent the rocket lands
//            LOGGER.info(getPassengers().size() + " {} Passengers: {}", level().isClientSide ? "Client" : "Server", getPassengers());

            if (level().isClientSide) {
                clientOffsetDiff *= .75f;
                updateClientMotion();
            }
            tickActors();

            if (launchingMode && launchtime == 0 && activeLaunch) {//Start blasting off
                if (!blasting) {//Only do this once
                    //When we make all entities passengers on client and server side
                    makeEntitiesPassengers(getEntitiesWithinContraption());
                    blasting = true;
                }
                if (!fuelBurned) { //We only burn the fuel once
                    System.out.println("BURNING FUEL");
                    if (contrap.fuelAmount() < contrap.fuelCost) {  //If we dont have enough fuel, disassemble
                        this.disassemble();
                    } else {
                        contrap.burnFuel(level());
                        fuelBurned = true;
                    }
                }
            }


            if (visualEngineCount == 0) {
                visualEngineCount = contrap.getVisualJetEngines();
            }
            if (this.owner == null) {
                if (contrap.owner != null) {
                    this.owner = ((RocketContraption) this.contraption).owner;
                }
                if (this.ownerID != null) {
                    this.owner = level().getPlayerByUUID(ownerID);
                }
            }

            if (contrap.isUsingTicket) {
                this.isUsingTicket = true;
            }
            if (contrap.localControlsPos != null) {
                this.localControlsPos = contrap.localControlsPos;
            }


            if (this.level().isClientSide) {
                // this code feels really stupid but I don't care enough to clean it up
                if (Math.abs(final_lift_vel) > 0.5f) {
                    int volume = NorthstarPlanets.getPlanetAtmosphereCost(level().dimension()) / 400;
                    int final_vol = volume < 1 ? 1 : volume;
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> tickAirSound(final_vol));
                }

                //Reseat the entities on the client
                synchronized (clientReseatLock) {
                    if (clientSide_entitiesThatMustBeReseated != null && !clientSide_entitiesThatMustBeReseated.isEmpty()) {
                        for (int i = getEntitiesWithinContraption().size() - 1; i >= 0; i--) {
                            Entity passenger = getEntitiesWithinContraption().get(i);
                            if (clientSide_entitiesThatMustBeReseated.contains(passenger.getUUID())) {
                                System.out.println("RESEATING " + passenger);
                                makeEntityPassenger(passenger);
                                clientSide_entitiesThatMustBeReseated.remove(passenger.getUUID());
                            }
                        }
                    }
                }


            } else {
                if (this.tickCount % 40 == 0) { //Send a packet containing data from server to client every 40 ticks
                    writeSyncPacket();
                }
                if (this.landingMode) {
                    NorthstarPackets.getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
                            new RocketContraptionQuickSyncPacket(slowing, this.getId()));
                }
            }

            if (contrap.owner != null && printed == false) {
                double heatCost = (TemperatureStuff.getHeatRating(destination) * ((RocketContraption) contraption).blockCount) + TemperatureStuff.getHeatConstant(destination);
                double heatCostHome = (TemperatureStuff.getHeatRating(level().dimension()) * ((RocketContraption) contraption).blockCount) + TemperatureStuff.getHeatConstant(level().dimension());
                if (heatCostHome > heatCost) heatCost = heatCostHome;
                int requiredJets = contrap.fuelCost / 800;
                int fuelCost = (int) (contrap.weightCost + (contrap.fuelCost - (contrap.fuelCost * contrap.computingPower)));

                contrap.owner.displayClientMessage(Component.literal
                        ("Fuel: " + (int)contrap.fuelAmount() + "; Required: " + fuelCost).withStyle(ChatFormatting.GOLD), false);
                contrap.owner.displayClientMessage(Component.literal
                        ("Return Fuel Cost: ~" + contrap.fuelReturnCost).withStyle(ChatFormatting.GOLD), false);
                contrap.owner.displayClientMessage(Component.literal
                        ("Heat Shielding: " + contrap.heatShielding() + "; Required: " + (int) Math.ceil(heatCost)).withStyle(ChatFormatting.YELLOW), false);
                contrap.owner.displayClientMessage(Component.literal
                        ("Engine Count: " + contrap.hasJetEngine() + "; Required: " + requiredJets).withStyle(ChatFormatting.BLUE), false);

                if (auto_land_mode) {
                    contrap.owner.displayClientMessage(Component.literal
                            ("Auto Landing Mode Enabled!").withStyle(ChatFormatting.GREEN), false);
                }

                contrap.owner.displayClientMessage(Component.literal
                        ("All entities should remain seated for the duration of the flight!").withStyle(ChatFormatting.AQUA), false);
                printed = true;
            }

            if (destination == null) {//bruh :(
                System.out.println("Destination is null. Setting destination to overworld");
                destination = Level.OVERWORLD;
            }


            if (launchingMode) {//If we are in launch mode
                if (blasting) {
                    lift_vel += lift_vel / 200;
                    lift_vel = Mth.clamp(lift_vel, 0.5f, maxSpeed);
                    final_lift_vel = lift_vel - 0.5f;
                }
                if (this.getY() > RocketHandler.DIMENSION_CHANGE_HEIGHT) { //Start landing
                    if (this.level().isClientSide) flyingSound.stopSound();
                    startLanding();
                    this.cooldown = 0;
                    this.final_lift_vel = 0;
                }

                if (soundTime % 40 == 0 && launchtime == 0 && blasting) {
                    this.level().playLocalSound(this.getX(), this.getY() - 20, this.getZ(), NorthstarSounds.ROCKET_BLAST.get(), SoundSource.BLOCKS, 5, 0, false);
                    i = 0;
                    soundTime = 0;
                } else {
                    soundTime++;
                }

            } else if (landingMode) { //If we are in landing mode
                if (auto_land_mode && this.getY() < getSlowdownHeightThreshold()) {
                    slowing = true;
                }

                if (slowing) {
                    this.level().playLocalSound(this.getX(), this.getY() - 8, this.getZ(), NorthstarSounds.ROCKET_LANDING.get(), SoundSource.BLOCKS, 4, 0, false);
                    i = 0;
                    soundTime = 0;
                }
                if (cooldown <= cooldownLength) {
                    cooldown++;
                }
                if (cooldown >= cooldownLength) {
                    if (!slowing) {
                        lift_vel -= 0.02;
                    } else {
                        lift_vel -= lift_vel / 10;
                    }
                    lift_vel = Mth.clamp(lift_vel, -maxSpeed, -0.5f);
                    final_lift_vel = lift_vel;
                }
            }

            double prevAxisMotion = axisMotion;
            if (level().isClientSide) {
                clientOffsetDiff *= .75f;
                updateClientMotion();
            }
            alignEntity();
            tickActors();
            Vec3 movementVec = getDeltaMovement();

            if (isLaunchingOrLanding() && //No point in checking for collisions if we're not moving
                    customCollision(landingMode ? Direction.DOWN : Direction.UP)) { //If we collide with the world
                if (!level().isClientSide) {
                    level().playLocalSound(getX(), getY(), getZ(), AllSoundEvents.STEAM.getMainEvent(), SoundSource.BLOCKS, 3, 0, true);
                    if ((Math.abs(final_lift_vel) < 3 || hasExploded)) {
                        if (this.landingMode && !isUsingTicket) {//Give the player a return ticket
                            ItemStack returnTicket = this.createReturnTicket(this);
                            if (owner != null) {
                                Player player = owner;
                                level().addFreshEntity(new ItemEntity(level(), player.getX(), player.getY(), player.getZ(), returnTicket));
                            }
                        }
                        //If we're landing, move the rocket up so it doesn't clip into the ground
                        if (this.landingMode) setPos(getX(), getY() + 1, getZ());
                        stopAndDissasembleInTicks(20);
                        if (this.landingMode && isUsingTicket) {//Consume the ticket
                            RocketHandler.deleteTicket(level(), this.blockPosition());
                        }
                    }

                    //If auto-landing is disabled, explode the rocket if it hits the ground
                    if (landingMode && !auto_land_mode && Math.abs(final_lift_vel) > 3 && !hasExploded) {
                        level().explode(this, getX(), getY() - 1, getZ(), 30, NorthstarPlanets.getPlanetOxy(destination), Level.ExplosionInteraction.MOB);
                        hasExploded = true;
                    }
                } else {
                    flyingSound.stopSound();
                }
            }

            if (!isStalled() && tickCount > 2) {
                if (sequencedOffsetLimit >= 0)
                    movementVec = VecHelper.clampComponentWise(movementVec, (float) sequencedOffsetLimit);
                move(movementVec.x, movementVec.y + final_lift_vel, movementVec.z);
                if (sequencedOffsetLimit > 0)
                    sequencedOffsetLimit = Math.max(0, sequencedOffsetLimit - movementVec.length());
            }
            if (Math.signum(prevAxisMotion) != Math.signum(axisMotion) && prevAxisMotion != 0)
                contraption.stop(level());
            slowing = false;
        }
    }

    private void writeSyncPacket() {
        NorthstarPackets.getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
                new RocketContraptionSyncPacket(this.position(), lift_vel, this.getId(), launchtime,
                        launchingMode, landingMode, blasting, slowing, activeLaunch, dissasemblyTicks));
    }


    @OnlyIn(Dist.CLIENT)
    private RocketAirSound flyingSound;

    @OnlyIn(Dist.CLIENT)
    private void tickAirSound(float maxVolume) {
        if (level().isClientSide) {
            float pitch = (float) Mth.clamp(getDeltaMovement().length(), .2f, 3f);
            if (flyingSound == null || flyingSound.isStopped()) {
                flyingSound = new RocketAirSound(SoundEvents.ELYTRA_FLYING, pitch, this);
                Minecraft.getInstance().getSoundManager().play(flyingSound);
            }
            flyingSound.setPitch(pitch);
            flyingSound.fadeIn(maxVolume);
        }
    }

    protected final Object clientReseatLock = new Object();

    @OnlyIn(Dist.CLIENT)
    public static void handleSyncPacket(RocketContraptionSyncPacket packet) {
        Entity entity = Minecraft.getInstance().level.getEntity(packet.contraptionEntityId);
        if (!(entity instanceof RocketContraptionEntity rce)) return;

        rce.setPos(packet.pos.x, packet.pos.y, packet.pos.z);
        rce.lift_vel = packet.lift_vel;
        rce.launchtime = packet.launchtime;
        rce.launchingMode = packet.launched;
        rce.landingMode = packet.landing;
        rce.blasting = packet.blasting;
        rce.slowing = packet.slowing;
        rce.activeLaunch = packet.activeLaunch;
        rce.dissasemblyTicks = packet.dissasemblyTicks;
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleQuickSyncPacket(RocketContraptionQuickSyncPacket packet) {
        Entity entity = Minecraft.getInstance().level.getEntity(packet.contraptionEntityId);
        if (!(entity instanceof RocketContraptionEntity rce)) return;

        rce.slowing = packet.slowing;
    }

    public ItemStack createReturnTicket(RocketContraptionEntity entity) {
        ItemStack result = new ItemStack(NorthstarItems.RETURN_TICKET.get());
        result.setHoverName(Component.translatable("item.northstar.return_ticket" + "_" + NorthstarPlanets.getPlanetName(entity.home)).setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA).withItalic(false)));
        CompoundTag tag = result.getOrCreateTagElement("Planet");
        tag.putString("name", NorthstarPlanets.getPlanetName(entity.home));
        return result;
    }


    public void startLanding() {
        this.launchingMode = false;
        this.landingMode = true;
        this.lift_vel = 0;
    }

    @Override
    public Component getContraptionName() {
        if (this.contraption instanceof RocketContraption rc)
            return Component.literal(rc.name);
        else
            return getName();
    }

    @Override
    public boolean startControlling(BlockPos controlsLocalPos, Player player) {
        if (player == null || player.isSpectator())
            return false;
        return true;
    }

    public void updateClientMotion() {
        Direction dir = Direction.UP;
        float modifier = dir.getAxisDirection()
                .getStep();
//        System.out.println("axisMotion " + axisMotion);
//        System.out.println("clientOffsetDiff " + clientOffsetDiff);
        Vec3 motion = Vec3.atLowerCornerOf(Direction.UP.getNormal())
                .scale((axisMotion + clientOffsetDiff * modifier / 2f) * ServerSpeedProvider.get());
        if (sequencedOffsetLimit >= 0)
            motion = VecHelper.clampComponentWise(motion, (float) sequencedOffsetLimit);
        setDeltaMovement(motion);
    }

    public boolean customCollision(Direction dir) {
        Level world = this.getCommandSenderWorld();
        AABB bounds = this.getBoundingBox();
        Vec3 position = this.position();
        BlockPos gridPos = BlockPos.containing(position);

        if (contraption == null)
            return false;
        if (!(contraption instanceof RocketContraption))
            return false;
        if (bounds == null)
            return false;

        // Blocks in the world
        if (dir.getAxisDirection() == AxisDirection.POSITIVE)
            gridPos = gridPos.relative(dir);
        if (isCollidingWithWorld(world, (RocketContraption) this.getContraption(), gridPos, dir))
            return true;

        return false;
    }

    @Override
    public boolean control(BlockPos controlsLocalPos, Collection<Integer> heldControls, Player player) {
        if (player.isSpectator() ||
                !toGlobalVector(VecHelper.getCenterOf(controlsLocalPos), 1).closerThan(player.position(), 8) || //If we are too far from the controls
                heldControls.contains(5)) { //If we press sneak key to dismount
            return false;
        }

        boolean spaceDown = heldControls.contains(4);
        if (spaceDown && launchingMode && launchtime == 0 && !blasting) {
            startLaunchSequence();
        }
        if (spaceDown && landingMode) {
            slowing = true;
        }
        return true;
    }

    /**
     * In ticks
     */
    public final static int LAUNCH_COUNTDOWN_TIME = 220;

    public void startLaunchSequence() {
        launchtime = LAUNCH_COUNTDOWN_TIME;
        activeLaunch = true;
    }

    public void cancelLaunch() {
        launchtime = 0;
        activeLaunch = false;
    }

    public boolean clientControl(BlockPos controlsLocalPos, Collection<Integer> heldControls, Player player) {
        if (player == null
                || player.isSpectator()
                || controlsLocalPos == null ||
                !toGlobalVector(VecHelper.getCenterOf(controlsLocalPos), 1).closerThan(player.position(), 8) || //If we are too far from the controls
                heldControls.contains(5)) { //If we press sneak key to dismount
            return false;
        }

        boolean spaceDown = heldControls.contains(4);
        if (spaceDown && launchingMode && launchtime == 0 && !blasting) {
            startLaunchSequence();
        }
        if (spaceDown && landingMode) {
            slowing = true;
        }
        return true;
    }

    public static boolean isCollidingWithWorld(Level world, RocketContraption contraption, BlockPos anchor,
                                               Direction movementDirection) {
        for (BlockPos pos : contraption.getOrCreateColliders(world, movementDirection)) {
            BlockPos colliderPos = pos.offset(anchor);

            BlockState collidedState = world.getBlockState(colliderPos);
            StructureBlockInfo blockInfo = contraption.getBlocks()
                    .get(pos);
            boolean emptyCollider = collidedState.getCollisionShape(world, pos)
                    .isEmpty();

            if (collidedState.getBlock() instanceof CocoaBlock)
                continue;

            MovementBehaviour movementBehaviour = MovementBehaviour.REGISTRY.get(blockInfo.state());
            if (movementBehaviour != null) {
                if (movementBehaviour instanceof BlockBreakingMovementBehaviour behaviour) {
                    if (!behaviour.canBreak(world, colliderPos, collidedState) && !emptyCollider)
                        return true;
                    continue;
                }
                if (movementBehaviour instanceof HarvesterMovementBehaviour harvesterMovementBehaviour) {
                    if (!harvesterMovementBehaviour.isValidCrop(world, colliderPos, collidedState)
                            && !harvesterMovementBehaviour.isValidOther(world, colliderPos, collidedState)
                            && !emptyCollider)
                        return true;
                    continue;
                }
            }

            if (!collidedState.canBeReplaced() && !emptyCollider) {
                return true;
            }

        }
        return false;
    }


    public void alignEntity() {
        if (!this.level().isClientSide()) {

            for (Entity e : this.getPassengers()) {
                if (!(e instanceof Player))
                    continue;
                if (e.distanceToSqr(this) > 32 * 32)
                    continue;
            }

            if (this.getPassengers()
                    .stream()
                    .anyMatch(p -> p instanceof Player)
            ) {
            }

            this.setServerSidePrevPosition();
        }

        this.setPos(this.position());

        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

    }

    public void setServerSidePrevPosition() {
        serverPrevPos = position();
    }

    @Override
    public RocketContraption getContraption() {
        return (RocketContraption) this.contraption;
    }

    public void makeEntityPassenger(Entity passenger) {
        passenger.pinToVehicle(this); //When we pin the entity, we force them to be a passenger and create a position offset to lock them down
    }

    public List<Entity> makeEntitiesPassengers(List<Entity> entitiesInContraption) {
        System.out.println("\n");
        List<Entity> madePassengers = new ArrayList<>();
        for (Entity passenger : entitiesInContraption) {
            if (passenger instanceof SuperGlueEntity) continue;

            if (!isEntitySeated(passenger)) {
                makeEntityPassenger(passenger);
                madePassengers.add(passenger);
            }
        }
        System.out.println("Force-seated " + madePassengers.size() + " passengers (" + (level().isClientSide ? "client" : "server") + ")\n");
        return madePassengers;
    }

    public boolean isLaunchingOrLanding() {
        return blasting || landingMode;
    }


    public boolean isAllPlayersSeated() {
        for (Entity passenger : getEntitiesWithinContraption()) {
            if (passenger instanceof Player && !isEntitySeated(passenger)) {
                return false;
            }
        }
        return true;
    }

    public boolean isEntitySeated(Entity entity) {
        return entity.getVehicle() == this;
    }


    public double getSlowdownHeightThreshold() {
        return this.level().getMaxBuildHeight() + 150;
    }

    @SuppressWarnings("unused")
    private void removeAndSaveEntity(RocketContraptionEntity entity, boolean portal) {
        Contraption contraption = entity.getContraption();
        if (contraption != null) {
            Map<UUID, Integer> mapping = contraption.getSeatMapping();
            for (Entity passenger : entity.getPassengers()) {
                if (!mapping.containsKey(passenger.getUUID()))
                    continue;

                Integer seat = mapping.get(passenger.getUUID());

                if (passenger instanceof ServerPlayer sp) {
                    continue;
                }

                serialisedPassengers.put(seat, passenger.serializeNBT());
            }
        }

        for (Entity passenger : entity.getPassengers())
            if (!(passenger instanceof Player))
                passenger.discard();

        serialize(entity);
        entity.discard();
        this.entity.clear();
    }

    private void serialize(Entity entity) {
        serialisedEntity = entity.serializeNBT();
        serialisedEntity.remove("Passengers");
        serialisedEntity.getCompound("Contraption")
                .remove("Passengers");
    }


    @Override
    public void setBlock(BlockPos localPos, StructureBlockInfo newInfo) {
        AllPackets.getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
                new ContraptionBlockChangedPacket(this.getId(), localPos, newInfo.state()));
    }

    /**
     * Disassembles the rocket in nTicks (max 128)
     * @param nTicks
     */
    public void stopAndDissasembleInTicks(int nTicks) {
        dissasemblyTicks = (byte) nTicks;
        lift_vel = 0;
        blasting = false;
        if (!level().isClientSide()) {
            writeSyncPacket();
        }
    }

    @Override
    public void disassemble() {
        //TODO: Entities are thrust downward when rocket hits the ground
        //A solution may be to freeze all entities in place for 0.5s after the rocket disassembles
        for (Entity passenger : getEntitiesWithinContraption()) {
            System.out.println("Dismounting " + passenger);
            passenger.stopRiding();//Safely dismount
        }
        for (Entity passenger : getPassengers()) {
            System.out.println("Dismounting " + passenger);
            passenger.stopRiding();//Safely dismount
        }
        RocketHandler.ROCKETS.remove(this);
        sequencedOffsetLimit = -1;
        super.disassemble();
    }

    @Override
    protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
        System.out.println("WRITE ADDITIONAL");
        super.writeAdditional(compound, spawnPacket);
        if (sequencedOffsetLimit >= 0)
            compound.putDouble("SequencedOffsetLimit", sequencedOffsetLimit);

        compound.putBoolean("blasting", this.blasting);
        compound.putBoolean("slowing", this.slowing);
        compound.putBoolean("isUsingTicket", this.isUsingTicket);
        compound.putBoolean("launched", this.launchingMode);

        compound.putBoolean("landing", this.landingMode);
        compound.putBoolean("fuelBurned", this.fuelBurned);
        compound.putBoolean("printed", this.printed);
        compound.putBoolean("activeLaunch", this.activeLaunch);

        compound.putBoolean("isUsingTicket", this.isUsingTicket);

        compound.putString("home", NorthstarPlanets.getPlanetName(home));
        compound.putString("destination", NorthstarPlanets.getPlanetName(destination));
        if (this.owner != null) {
            compound.putUUID("player", this.owner.getUUID());
        }

        compound.putInt("visualEngineCount", this.visualEngineCount);

        compound.putFloat("lift_vel", lift_vel);
        compound.putFloat("final_lift_vel", final_lift_vel);
        compound.putBoolean("auto_land", auto_land_mode);

        if (getPassengerOffsets() != null) {
            // Save passengerOffsets map
            ListTag offsetsList = new ListTag();

            for (var entry : getPassengerOffsets().entrySet()) {
                CompoundTag offsetTag = new CompoundTag();

                // Store UUID
                offsetTag.putUUID("uuid", entry.getKey());

                // Store Vec3
                Vec3 vec = entry.getValue();
                offsetTag.putDouble("x", vec.x);
                offsetTag.putDouble("y", vec.y);
                offsetTag.putDouble("z", vec.z);

                offsetsList.add(offsetTag);
            }
            // Attach list to main tag
            compound.put("passengerOffsets", offsetsList);
        }

    }

    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnData) {
        System.out.println("READ ADDITIONAL (is spawn packet: " + spawnData + ")  is client: " + level().isClientSide);
        super.readAdditional(compound, spawnData);
        sequencedOffsetLimit =
                compound.contains("SequencedOffsetLimit") ? compound.getDouble("SequencedOffsetLimit") : -1;

        this.blasting = compound.contains("blasting") ? compound.getBoolean("SequencedOffsetLimit") : false;
        this.slowing = compound.contains("slowing") ? compound.getBoolean("slowing") : false;
        this.isUsingTicket = compound.contains("isUsingTicket") ? compound.getBoolean("isUsingTicket") : false;
        this.launchingMode = compound.contains("launched") ? compound.getBoolean("launched") : false;

        this.landingMode = compound.contains("landing") ? compound.getBoolean("landing") : false;
        this.fuelBurned = compound.contains("fuelBurned") ? compound.getBoolean("fuelBurned") : false;
        this.printed = compound.contains("printed") ? compound.getBoolean("printed") : false;
        this.activeLaunch = compound.contains("activeLaunch") ? compound.getBoolean("activeLaunch") : false;

        this.isUsingTicket = compound.contains("isUsingTicket") ? compound.getBoolean("isUsingTicket") : false;

        if (compound.contains("home")) {
            home = NorthstarPlanets.getPlanetDimension(compound.getString("home"));
        }
        if (compound.contains("destination")) {
            destination = NorthstarPlanets.getPlanetDimension(compound.getString("destination"));
        }
        if (compound.contains("player")) {
            this.ownerID = compound.getUUID("player");
        }
        if (compound.contains("visualEngineCount")) {
            this.visualEngineCount = compound.getInt("visualEngineCount");
        }
        if (compound.contains("lift_vel")) {
            this.lift_vel = compound.getFloat("lift_vel");
        }
        if (compound.contains("final_lift_vel")) {
            this.final_lift_vel = compound.getFloat("final_lift_vel");
        }

        if (compound.contains("auto_land")) {
            this.auto_land_mode = compound.getBoolean("auto_land");
        }

        if (compound.contains("passengerOffsets", 9)) { // 9 = ListTag
            Map<UUID, Vec3> passengerOffsets = new HashMap<>(); // Create map>
            ListTag offsetsList = compound.getList("passengerOffsets", 10); // 10 = CompoundTag

            for (int i = 0; i < offsetsList.size(); i++) {
                CompoundTag offsetTag = offsetsList.getCompound(i);

                UUID uuid = offsetTag.getUUID("uuid");
                double x = offsetTag.getDouble("x");
                double y = offsetTag.getDouble("y");
                double z = offsetTag.getDouble("z");

                passengerOffsets.put(uuid, new Vec3(x, y, z));
            }
            setPassengerOffsets(passengerOffsets);
            //Make sure all passengers are reseated
            if (level().isClientSide) {
                synchronized (clientReseatLock) {
                    clientSide_entitiesThatMustBeReseated = new ArrayList<>(passengerOffsets.keySet());
                }
            } else makeEntitiesPassengers(getEntitiesWithinContraption());
        }
    }


    @Override
    protected boolean isActorActive(MovementContext context, MovementBehaviour actor) {
        if (!(contraption instanceof RocketContraption rc))
            return false;
        if (!super.isActorActive(context, actor))
            return false;
        return level().isClientSide();
    }

    @Override
    public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
        return localPos;
    }

    @Override
    public Vec3 reverseRotation(Vec3 localPos, float partialTicks) {
        return localPos;
    }


    public double getAxisCoord() {
        Vec3 anchorVec = getAnchorVec();
        return anchorVec.y;
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
    public void teleportTo(double p_70634_1_, double p_70634_3_, double p_70634_5_) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double x, double y, double z, float yw, float pt, int inc, boolean t) {
    }

    @Override
    protected void handleStallInformation(double x, double y, double z, float angle) {
        setPosRaw(x, y, z);
        clientOffsetDiff = 0;
    }

    @Override
    public ContraptionRotationState getRotationState() {
        return ContraptionRotationState.NONE;
    }

    @Override
    public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
        float angleInitialYaw = 0;
        float angleYaw = getViewYRot(partialTicks);
        float anglePitch = getViewXRot(partialTicks);

        matrixStack.translate(0, 0, 0);

        TransformStack.of(matrixStack)
                .nudge(getId())
                .center()
                .rotateY(angleYaw)
                .rotateZ(anglePitch)
                .rotateY(angleInitialYaw)
                .uncenter();
    }

    public boolean isActiveLaunch() {
        return activeLaunch;
    }
}
