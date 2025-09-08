package com.lightning.northstar.contraptions;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarEntityTypes;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.contraptions.packets.RocketContraptionQuickSyncPacket;
import com.lightning.northstar.contraptions.packets.RocketContraptionSyncPacket;
import com.lightning.northstar.contraptions.packets.RocketControlPacket;
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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Function;

public class RocketContraptionEntity extends AbstractContraptionEntity implements IEntityAdditionalSpawnData {

    private final Map<Integer, Vec3> passengerOffsets = new HashMap<>();

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
    public float lift_vel = 0.5f;
    public float final_lift_vel = lift_vel - 0.5f;
    public ResourceKey<Level> home;
    public ResourceKey<Level> destination;
    public WeakReference<RocketContraptionEntity> entity;
    private byte dissasemblyTicks;

    public int getLaunchTime() {
        return launchtime;
    }

    public BlockPos localControlsPos;
    private List<Entity> entitiesWithinContraption = new ArrayList<>();

    public List<Entity> getEntitiesWithinContraption() {
        return entitiesWithinContraption;
    }

    public RocketContraptionEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        noCulling = true;

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
        if (!level().dimension().equals(destination) && getY() >= RocketHandler.DIMENSION_CHANGE_HEIGHT && level() instanceof ServerLevel) {
            changeDimension(level().getServer().getLevel(destination));
        }

        //If we wait 1 second before disassembling, it helps preventing entities clipping into the ground after disassembling
        if (dissasemblyTicks > 0) {
            setDeltaMovement(0, 0, 0);
            if (!level().isClientSide) {
//                System.out.println("Dissasembling in " + dissasemblyTicks + " ticks");
                dissasemblyTicks--;
                if (dissasemblyTicks == 0) disassemble();
            }
            return;
        }

        var contraption = getContraption();

        securePassengers();

        //Check for entities within the contraption
        //Ideally, we shouldnt have to do this every tick, but there are mixins that rely on this (gravityStuffMixin)
        entitiesWithinContraption = this.level().getEntities(this, this.getBoundingBox());


        if (launchtime > 0 && activeLaunch) {
//                System.out.println("Launchtime: " + launchtime);
            launchtime--;
        }
        if (level().isClientSide) {
            clientOffsetDiff *= .75f;
            updateClientMotion();
        }
        tickActors();

        if (launchingMode && launchtime == 0 && activeLaunch) {//Start blasting off
            if (!blasting) {//Only do this once
                blasting = true;
            }
            if (!fuelBurned) { //We only burn the fuel once
                System.out.println("BURNING FUEL");
                if (contraption.fuelAmount() < contraption.fuelCost) {  //If we dont have enough fuel, disassemble
                    this.disassemble();
                } else {
                    contraption.burnFuel(level());
                    fuelBurned = true;
                }
            }
        }


        if (visualEngineCount == 0) {
            visualEngineCount = contraption.getVisualJetEngines();
        }
        if (this.owner == null) {
            if (contraption.owner != null) {
                this.owner = ((RocketContraption) this.contraption).owner;
            }
            if (this.ownerID != null) {
                this.owner = level().getPlayerByUUID(ownerID);
            }
        }

        if (contraption.isUsingTicket) {
            this.isUsingTicket = true;
        }
        if (contraption.localControlsPos != null) {
            this.localControlsPos = contraption.localControlsPos;
        }


        if (this.level().isClientSide) {
            // this code feels really stupid but I don't care enough to clean it up
            if (Math.abs(final_lift_vel) > 0.5f) {
                int volume = NorthstarPlanets.getPlanetAtmosphereCost(level().dimension()) / 400;
                int final_vol = volume < 1 ? 1 : volume;
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> tickAirSound(final_vol));
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

        if (contraption.owner != null && printed == false) {
            double heatCost = (TemperatureStuff.getHeatRating(destination) * ((RocketContraption) this.contraption).blockCount) + TemperatureStuff.getHeatConstant(destination);
            double heatCostHome = (TemperatureStuff.getHeatRating(level().dimension()) * ((RocketContraption) this.contraption).blockCount) + TemperatureStuff.getHeatConstant(level().dimension());
            if (heatCostHome > heatCost) heatCost = heatCostHome;
            int requiredJets = contraption.fuelCost / 800;
            int fuelCost = (int) (contraption.weightCost + (contraption.fuelCost - (contraption.fuelCost * contraption.computingPower)));

            contraption.owner.displayClientMessage(Component.literal
                    ("Fuel: " + (int) contraption.fuelAmount() + "; Required: " + fuelCost).withStyle(ChatFormatting.GOLD), false);
            contraption.owner.displayClientMessage(Component.literal
                    ("Return Fuel Cost: ~" + contraption.fuelReturnCost).withStyle(ChatFormatting.GOLD), false);
            contraption.owner.displayClientMessage(Component.literal
                    ("Heat Shielding: " + contraption.heatShielding() + "; Required: " + (int) Math.ceil(heatCost)).withStyle(ChatFormatting.YELLOW), false);
            contraption.owner.displayClientMessage(Component.literal
                    ("Engine Count: " + contraption.hasJetEngine() + "; Required: " + requiredJets).withStyle(ChatFormatting.BLUE), false);

            if (auto_land_mode) {
                contraption.owner.displayClientMessage(Component.literal
                        ("Auto Landing Mode Enabled!").withStyle(ChatFormatting.GREEN), false);
            }

            contraption.owner.displayClientMessage(Component.literal
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
                    setPos(getX(), getY() + 1, getZ());   //To prevent the rocket from colliding with the world, we move it up
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
                if (flyingSound != null)
                    flyingSound.stopSound();
            }
        }

        if (!isStalled() && tickCount > 2) {
            move(movementVec.x, movementVec.y + final_lift_vel, movementVec.z);
        }
        if (Math.signum(prevAxisMotion) != Math.signum(axisMotion) && prevAxisMotion != 0)
            this.contraption.stop(level());
        slowing = false;
    }

    private void writeSyncPacket() {
        NorthstarPackets.getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
                new RocketContraptionSyncPacket(this.position(), lift_vel, this.getId(), launchtime,
                        launchingMode, landingMode, blasting, slowing, activeLaunch, dissasemblyTicks));
    }

    private void securePassengers() {
        // Non-seated passengers tend to phase out of the contraption at high speeds
        // This is a temporary fix until Create fixes it for good (also happens on trains)

        AABB bounds = getBoundingBox();
        AABB verticalBounds = bounds.inflate(0, 1000, 0);
        List<Entity> entities = level().getEntities(this, bounds);

        Iterator<Map.Entry<Integer, Vec3>> iterator = passengerOffsets.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Vec3> entry = iterator.next();
            Entity entity = level().getEntity(entry.getKey());
            if (entity == null) {
                iterator.remove();
                continue;
            }
            if (entity.getBoundingBox().intersects(bounds))
                continue; // entity is still in the rocket, nothing to do

            if (entity.getBoundingBox().intersects(verticalBounds)) {
                // entity is likely under the rocket after phasing trough, teleport it back on board
                entity.setPos(position().add(entry.getValue()));
            } else {
                // entity likely jumped out on purpose, stop tracking it
                iterator.remove();
            }
        }

        for (Entity entity : entities) {
            passengerOffsets.put(entity.getId(), entity.position().subtract(position()));
        }
    }

    @Override
    public @Nullable Entity changeDimension(ServerLevel destination) {
        // default teleporter doesn't create a portal info
        return changeDimension(destination, new ITeleporter() {
            @Override
            public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
                return new PortalInfo(entity.position(), Vec3.ZERO, entity.getXRot(), entity.getYRot());
            }
        });
    }

    @Override
    public @Nullable Entity changeDimension(ServerLevel destination, ITeleporter teleporter) {
        record PassengerData(Entity entity, Vec3 offset, int seat) {
        }
        List<PassengerData> passengers = new ArrayList<>();
        UUID controllingPlayer = getControllingPlayer().orElse(null);

        for (Entity passenger : level().getEntities(this, getBoundingBox().inflate(2, 50, 2))) {
            Vec3 offset = passenger.position().subtract(position());
            int seat = contraption.getSeats().indexOf(contraption.getSeatOf(passenger.getUUID()));
            passengers.add(new PassengerData(passenger, offset, seat));
        }

        RocketContraptionEntity newRocket = (RocketContraptionEntity) super.changeDimension(destination, teleporter);
        if (newRocket == null) {
            return null; // huh?
        }

        for (PassengerData data : passengers) {
            Entity newPassenger = data.entity.changeDimension(destination, teleporter);
            if (newPassenger == null) {
                continue; // TODO: should we do something about it?
            }

            newPassenger.setPos(newRocket.position().add(data.offset));
            newRocket.passengerOffsets.put(newPassenger.getId(), data.offset);

            if (data.seat != -1)
                addSittingPassenger(newPassenger, data.seat);
        }

        if (controllingPlayer != null)
            NorthstarPackets.getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new RocketControlPacket(controllingPlayer, getId(), localControlsPos));

        return newRocket;
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


    public void stopAndDissasembleInTicks(int nTicks) {
        dissasemblyTicks = (byte) nTicks;
        lift_vel = 0;
        blasting = false;
        if (!level().isClientSide()) {
            writeSyncPacket();
        }
    }


//    public void addPassenger(Entity passenger, Integer seatIndex, Vec3 offset) {
//        Northstar.LOGGER.info("ROCKET: Adding passenger " + passenger + " Seat ID: " + seatIndex + " Offset " + offset);
//        //If we want to set an offset (and we dont have one already), do it
//        if (offset != null && !passengerOffsets.containsKey(passenger.getId())) {
//            System.out.println("ROCKET: Setting offset for passenger " + passenger + " Seat ID: " + seatIndex + " Offset " + offset);
//            passengerOffsets.put(passenger.getId(), offset);
//        }
//
//        if (seatIndex == null) {//When we want to force seat an entity but they aren't in a seat
//
//            if (offset == null) {//If the offset is null
//                offset = passengerOffsets.get(passenger.getId());
//                if (offset == null) { //If we dont have an offset for this passenger, make one
//                    offset = new Vec3(passenger.getX() - getX(), passenger.getY() - getY(), passenger.getZ() - getZ());
//                    Northstar.LOGGER.info("ROCKET: Recording Passenger Offset. Passenger: {}; Offset: {}", passenger, offset);
//                    passengerOffsets.put(passenger.getId(), offset);
//                }
//            }
//
//            passenger.startRiding(this, true);
//            if (passenger instanceof TamableAnimal ta) ta.setInSittingPose(true);
//
//            //TODO: Should the passenger be seated using create seats?
//            //Make a new seat and add sitting passenger based on it (The only issue is when 2 entities share the same block position)
//            //We CANNOT assign a fake seat id (the seat ID must be corresponding to an actual seat in contraption.getSeats())
//            //see com.simibubi.create.content.contraptions.Contraption.addPassengersToWorld(Contraption.java:1297)
//            BlockPos newSeat = new BlockPos((int) offset.x, (int) offset.y, (int) offset.z);
//            contraption.getSeats().add(newSeat); //Add the new seat and get the index
//            seatIndex = contraption.getSeats().indexOf(newSeat);
//        }
//
//        passenger.setInvulnerable(true);
//        passenger.noPhysics = true;
//        super.addSittingPassenger(passenger, seatIndex);
//    }


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
//        rce.auto_land_mode = packet.auto_land_mode;
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
        Vec3 motion = Vec3.atLowerCornerOf(Direction.UP.getNormal())
                .scale((axisMotion + clientOffsetDiff * modifier / 2f) * ServerSpeedProvider.get());
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
        return isCollidingWithWorld(world, getContraption(), gridPos, dir);
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
        }

        this.setPos(this.position());

        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

    }

    @Override
    public RocketContraption getContraption() {
        return (RocketContraption) contraption;
    }

    public boolean isLaunchingOrLanding() {
        return blasting || landingMode;
    }

    public double getSlowdownHeightThreshold() {
        return level().getMaxBuildHeight() + 150;
    }

    @Override
    public void setBlock(BlockPos localPos, StructureBlockInfo newInfo) {
        AllPackets.getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
                new ContraptionBlockChangedPacket(this.getId(), localPos, newInfo.state()));
    }

    @Override
    public void disassemble() {
        super.disassemble();

        RocketHandler.ROCKETS.remove(this);
    }

    @Override
    protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
        System.out.println("WRITE ADDITIONAL");
        super.writeAdditional(compound, spawnPacket);

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
    }

    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnData) {
        System.out.println("READ ADDITIONAL (is spawn packet: " + spawnData + ")  is client: " + level().isClientSide);
        super.readAdditional(compound, spawnData);

        this.blasting = compound.contains("blasting") && compound.getBoolean("SequencedOffsetLimit");
        this.slowing = compound.contains("slowing") && compound.getBoolean("slowing");
        this.isUsingTicket = compound.contains("isUsingTicket") && compound.getBoolean("isUsingTicket");
        this.launchingMode = compound.contains("launched") && compound.getBoolean("launched");

        this.landingMode = compound.contains("landing") && compound.getBoolean("landing");
        this.fuelBurned = compound.contains("fuelBurned") && compound.getBoolean("fuelBurned");
        this.printed = compound.contains("printed") && compound.getBoolean("printed");
        this.activeLaunch = compound.contains("activeLaunch") && compound.getBoolean("activeLaunch");

        this.isUsingTicket = compound.contains("isUsingTicket") && compound.getBoolean("isUsingTicket");

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

    @Override
    protected StructureTransform makeStructureTransform() {
        return new StructureTransform(BlockPos.containing(getAnchorVec().add(.5, .5, .5)), 0, 0, 0);
    }

    @Override
    protected float getStalledAngle() {
        return 0;
    }

    @Override
    public void teleportTo(double x, double y, double z) {
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