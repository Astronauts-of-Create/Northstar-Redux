package com.lightning.northstar.contraption.rocket;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarEntityTypes;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.contraption.rocket.packet.EntityLockPacket;
import com.lightning.northstar.contraption.rocket.packet.RocketContraptionQuickSyncPacket;
import com.lightning.northstar.contraption.rocket.packet.RocketContraptionSyncPacket;
import com.lightning.northstar.contraption.rocket.packet.RocketControlPacket;
import com.lightning.northstar.util.mixinInterfaces.EntityMixin_I;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.kinetics.base.BlockBreakingMovementBehaviour;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;


/**
 * TODO: Rocket launch / seating bugs
 * 1. players sitting in rideable entities, no clip through the rocket?
 *  - currently solved this by not allowing a player to sit in a rideable entity when in a rocket
 *
 * 2. players sitting down can no clip through the rocket when they dismount
 *  - currently solved by soft-locking player in place for 10 ticks but could be improved (might not be foolproof)
 *      - the issue was that when the player dismounts, their dismount location could be different from the rocket's position, when the rocket is moving fast, updating dismount location to account for this isn't enough.
 *
 * 3. when a boat/card is on the ship, if the player gets too close, they will noclip through the ship
 *  - possible solutions
 *      - break boat/minecart?
 *      - disable collisions of said entities?
 *      - understand why this happens in the first place, and fix it at that level instead of trying to patch it?
 *
 * 4. non-seated entities still bug out visually
 *   - This is a client side issue where entities arent being rendered as if they are riding the rocket (the client likely interpolates the position of the entity from the server, but doesn't keep it synchronized with the rocket's position)
 */
public class RocketContraptionEntity extends AbstractContraptionEntity implements IEntityAdditionalSpawnData {

    /**
     * In ticks
     */
    public final static int LAUNCH_COUNTDOWN_TICKS = 10 * 20;
    private final static int TRANSPORT_DELAY_TICKS = 40;
    private final static int DISMOUNT_SOFT_LOCK_TICKS = 10;
    /**
     * maximum velocity, in blocks per tick
     */
    private static final float MAX_SPEED = 5;

    private List<Entity> entitiesWithinContraption = List.of();

    public boolean auto_land_mode;
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
    /**
     * In ticks
     */
    private int launchTime = 0;
    private boolean activeLaunch = false;
    public Player owner;
    public UUID ownerID;
    public float lift_vel;
    public float final_lift_vel = lift_vel;
    public ResourceKey<Level> home;
    public ResourceKey<Level> destination;

    private int transportDelay;

    public RocketContraptionEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        noCulling = true;
        lift_vel = 0.5f;
        launchingMode = true;
        landingMode = false;
    }

    private void fixEntityMounting(boolean inflatedAABB) {
        for (Entity entity :
                inflatedAABB ?
                        entitiesWithinContraption :
                        level().getEntities(this, getBoundingBox())
        ) {
            ((EntityMixin_I) entity).setRidingRocket(this);
            if (entity.getVehicle() != this) {
                if (entity.getVehicle() != null) {
                    Northstar.LOGGER.warn("Unmounting entity because they are in a vehicle that is not this rocket");
                    entity.stopRiding();
                }
                if (!level().isClientSide//Temporary solution to prevent large entities like boats from jostling the player and shoving them out of the rocket
                        && !(entity instanceof LivingEntity)
                        && !(entity instanceof SuperGlueEntity)
                        && !(entity instanceof ItemEntity)) {
                    lockEntity(entity, EntityLockPacket.LockInfo.FOREVER);
                    entity.startRiding(this, true);
                }
            }
        }
    }

    public static RocketContraptionEntity create(Level world, Contraption contraption) {
        RocketContraptionEntity entity = new RocketContraptionEntity(NorthstarEntityTypes.ROCKET_CONTRAPTION.get(), world);
        entity.setContraption(contraption);
        return entity;
    }

    @Override
    public void disassemble() {
        super.disassemble();

        for (Entity entity : getEntitiesWithinContraption()) {
            EntityMixin_I entity1 = (EntityMixin_I) entity;
            entity1.setRidingRocket(null);
        }

        RocketHandler.ROCKETS.remove(this);
    }

    @Override
    protected void tickContraption() {
        Level level = level();
        if (!level.dimension().equals(destination)) {
            if (getY() >= RocketHandler.DIMENSION_CHANGE_HEIGHT) {
                transportDelay = Math.min(TRANSPORT_DELAY_TICKS, transportDelay + 1);
                if (transportDelay == TRANSPORT_DELAY_TICKS && level instanceof ServerLevel sl) {
                    changeDimension(sl.getServer().getLevel(destination));
                }
            }
        } else {
            transportDelay = Math.max(0, transportDelay - 1);
        }

        var contraption = getContraption();

        tickActors();

        entitiesWithinContraption = level.getEntities(this, getBoundingBox().inflate(1, MAX_SPEED * 4, 1));

        if (launchTime > 0 && activeLaunch) {
            launchTime--;
        }

        if (launchingMode && launchTime == 0 && activeLaunch) {//Start blasting off
            if (!blasting) {//Only do this once
                blasting = true;
                fixEntityMounting(false);
            }
            if (!fuelBurned) { //We only burn the fuel once
                Northstar.LOGGER.debug("BURNING FUEL");
                if (contraption.fuelAmount() < contraption.fuelCost) {  //If we dont have enough fuel, disassemble
                    this.disassemble();
                } else {
                    contraption.burnFuel();
                    fuelBurned = true;
                }
            }
        }

        if (this.owner == null) {
            if (contraption.owner != null) {
                this.owner = ((RocketContraption) this.contraption).owner;
            }
            if (this.ownerID != null) {
                this.owner = level.getPlayerByUUID(ownerID);
            }
        }

        if (contraption.isUsingTicket) {
            this.isUsingTicket = true;
        }


        if (level.isClientSide) {
            // this code feels really stupid but I don't care enough to clean it up
            if (Math.abs(final_lift_vel) > 0.5f) {
                int volume = NorthstarPlanets.getPlanetAtmosphereCost(level.dimension()) / 400;
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> tickAirSound(Math.max(volume, 1)));
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

        if (contraption.owner != null && !printed) {
            displayInfo();
            fixEntityMounting(false);
            printed = true;
        }

        if (destination == null) {//bruh :(
            Northstar.LOGGER.error("Rocket destination is null. Setting destination to overworld");
            destination = Level.OVERWORLD;
        }


        if (launchingMode) {//If we are in launch mode
            if (blasting) {
                lift_vel += lift_vel * 0.005f;
                lift_vel = Mth.clamp(lift_vel, 0.5f, MAX_SPEED);
                final_lift_vel = lift_vel - 0.5f;
            }
            if (this.getY() > RocketHandler.DIMENSION_CHANGE_HEIGHT) { //Start landing
                if (level.isClientSide && flyingSound != null) flyingSound.stopSound();
                startLanding();
                this.cooldown = 0;
                this.final_lift_vel = 0;
            }

            if (soundTime % 40 == 0 && launchTime == 0 && blasting) {
                level.playLocalSound(this.getX(), this.getY() - 20, this.getZ(), NorthstarSounds.ROCKET_BLAST.get(), SoundSource.BLOCKS, 5, 0, false);
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
                level.playLocalSound(this.getX(), this.getY() - 8, this.getZ(), NorthstarSounds.ROCKET_LANDING.get(), SoundSource.BLOCKS, 4, 0, false);
                i = 0;
                soundTime = 0;
            }
            if (cooldown <= cooldownLength) {
                cooldown++;
            }
            if (cooldown >= cooldownLength) {
                if (!slowing) {
                    lift_vel -= 0.02f;
                } else {
                    lift_vel -= lift_vel / 10;
                }
                lift_vel = Mth.clamp(lift_vel, -MAX_SPEED, -0.5f);
                final_lift_vel = lift_vel;
            }
        }

        tickActors();

        if (isLaunchingOrLanding() && collidesWithBlocks(landingMode ? Direction.DOWN : Direction.UP)) { //If we collide with the world
            if (!level.isClientSide) {
                level.playLocalSound(getX(), getY(), getZ(), AllSoundEvents.STEAM.getMainEvent(), SoundSource.BLOCKS, 3, 0, true);
                if ((Math.abs(final_lift_vel) < 3 || hasExploded)) {
                    if (this.landingMode && !isUsingTicket) {//Give the player a return ticket
                        ItemStack returnTicket = createReturnTicket();
                        if (owner != null) {
                            Player player = owner;
                            level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), returnTicket));
                        }
                    }
                    move(0, -1, 0); // temporary fix for the rocket disassembling one block above the ground
                    disassemble();
                    final_lift_vel = 0; // don't move entities when disassembling
                    if (this.landingMode && isUsingTicket) {//Consume the ticket
                        RocketHandler.deleteTicket(level, this.blockPosition());
                    }
                    landingMode = false;
                }

                //If auto-landing is disabled, explode the rocket if it hits the ground
                if (landingMode && !auto_land_mode && Math.abs(final_lift_vel) > 3 && !hasExploded) {
                    level.explode(this, getX(), getY() - 1, getZ(), 30, NorthstarPlanets.getPlanetOxy(destination), Level.ExplosionInteraction.MOB);
                    hasExploded = true;
                }
            } else {
                if (flyingSound != null)
                    flyingSound.stopSound();
            }
        }
        if (!isStalled() && tickCount > 2 && transportDelay == 0) {
            move(0, final_lift_vel, 0);
            // TODO: non-seated entities still bug out visually
            for (Entity entity : getEntitiesWithinContraption()) {
                if (entity.getVehicle() != this) { //If the entity is not a passenger of this rocket (contraption.getSeatOf(entity.getUUID()) == null)
                    EntityLockPacket.LockInfo lockInfo = entityLockMap.get(entity.getUUID());
                    if (lockInfo == null) { //Offset the player position by the rocket velocity
                        entity.setPos(entity.getX(), entity.getY() + final_lift_vel, entity.getZ());
                    } else { //We need to hold the player in their seat for a short time before letting them go, this is to prevent players from clipping through the ship
                        entity.setPos(
                                position().x + lockInfo.offset().x,
                                position().y + lockInfo.offset().y,
                                position().z + lockInfo.offset().z);
                        if (lockInfo.ticks().get() != EntityLockPacket.LockInfo.FOREVER) {//remove soft-lock after a few ticks
                            lockInfo.ticks().getAndAdd(-1);
                            if (lockInfo.ticks().get() < 0) entityLockMap.remove(entity.getUUID());
                        }
                    }
                }
            }
        }

        slowing = false;
    }

    public HashMap<UUID, EntityLockPacket.LockInfo> entityLockMap = new HashMap<>();

    /**
     * Add a soft-release entry, to lock the player in for a few ticks (This should happen on the server side)
     *
     * @param passenger
     */
    public void lockEntity(Entity passenger, int ticks) {
        EntityLockPacket.LockInfo entry = entityLockMap.get(passenger.getUUID());
        if (entry != null) { //If there is already an entry, don't let the tick duration be less than what it already is
            ticks = Math.max(entry.ticks().get(), ticks);
        }

        BlockPos seatPos = contraption.getSeatOf(passenger.getUUID());
        if (seatPos != null) {
            Northstar.LOGGER.info("Locking " + passenger + " to seat for " + ticks + " ticks");
            Vec3 offset = VecHelper.getCenterOf(seatPos);//.add(0, passenger.getBoundingBox().getYsize(), 0);
            entry = new EntityLockPacket.LockInfo(offset, new AtomicInteger(ticks));
        } else {//If the player dismounts from the rocket without a seat or dismounts from a boat for instance
            Northstar.LOGGER.info("Locking " + passenger + " to position for " + ticks + " ticks");
            Vec3 offset = passenger.position().subtract(position()).add(0, final_lift_vel, 0);
            entry = new EntityLockPacket.LockInfo(offset, new AtomicInteger(ticks));
        }


        if (entry != null) {
            entityLockMap.put(passenger.getUUID(), entry);
            NorthstarPackets.getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new EntityLockPacket(passenger.getUUID(), getId(), entry));
        }
    }

    @Override
    public void positionRider(Entity passenger, MoveFunction callback) {
        if (!hasPassenger(passenger))
            return;

        Vec3 transformedVector = getPassengerPosition(passenger, 1);
        if (transformedVector == null) {
            //Added
            EntityLockPacket.LockInfo lockInfo = entityLockMap.get(passenger.getUUID());
            if (lockInfo != null) {
                callback.accept(passenger,
                        position().x + lockInfo.offset().x,
                        position().y + lockInfo.offset().y,
                        position().z + lockInfo.offset().z);
                if (lockInfo.ticks().get() != EntityLockPacket.LockInfo.FOREVER) {
                    lockInfo.ticks().getAndAdd(-1);
                    if (lockInfo.ticks().get() < 0) entityLockMap.remove(passenger.getUUID());
                }
            }//--------------
            return;
        }

        float offset = -1 / 8f;
        if (passenger instanceof AbstractContraptionEntity)
            offset = 0.0f;
        callback.accept(passenger, transformedVector.x,
                transformedVector.y + SeatEntity.getCustomEntitySeatOffset(passenger) + offset, transformedVector.z);
    }

    @Override
    public void removePassenger(Entity passenger) {
        //If we dismount
        if (!level().isClientSide) lockEntity(passenger, DISMOUNT_SOFT_LOCK_TICKS);
        super.removePassenger(passenger);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity entityLiving) {
        return super.getDismountLocationForPassenger(entityLiving).add(0, final_lift_vel, 0);
    }

    private void writeSyncPacket() {
        RocketContraptionSyncPacket packet = new RocketContraptionSyncPacket(getId(), position(), lift_vel, launchTime,
                launchingMode, landingMode, blasting, slowing, activeLaunch);
        NorthstarPackets.getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> this), packet);
    }

    private void displayInfo() {
        RocketContraption contraption = getContraption();
        double heatCost = (NorthstarTemperature.getHeatRating(destination) * ((RocketContraption) this.contraption).blockCount) + NorthstarTemperature.getHeatConstant(destination);
        double heatCostHome = (NorthstarTemperature.getHeatRating(level().dimension()) * ((RocketContraption) this.contraption).blockCount) + NorthstarTemperature.getHeatConstant(level().dimension());
        if (heatCostHome > heatCost) heatCost = heatCostHome;
        int requiredJets = contraption.fuelCost / 800;
        int fuelCost = (int) (contraption.weightCost + (contraption.fuelCost - (contraption.fuelCost * contraption.computingPower)));

        contraption.owner.displayClientMessage(Component.literal("Fuel: " + (int) contraption.fuelAmount() + "; Required: " + fuelCost).withStyle(ChatFormatting.GOLD), false);
        contraption.owner.displayClientMessage(Component.literal("Return Fuel Cost: ~" + contraption.fuelReturnCost).withStyle(ChatFormatting.GOLD), false);
        contraption.owner.displayClientMessage(Component.literal("Heat Shielding: " + contraption.heatShielding() + "; Required: " + (int) Math.ceil(heatCost)).withStyle(ChatFormatting.YELLOW), false);
        contraption.owner.displayClientMessage(Component.literal("Engine Count: " + contraption.hasJetEngine() + "; Required: " + requiredJets).withStyle(ChatFormatting.BLUE), false);

        if (auto_land_mode) {
            contraption.owner.displayClientMessage(Component.literal("Auto Landing Mode Enabled!").withStyle(ChatFormatting.GREEN), false);
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
        Northstar.LOGGER.info("Changing ship " + this + " to dimension " + destination.toString());
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
        newRocket.transportDelay = TRANSPORT_DELAY_TICKS;

        for (PassengerData data : passengers) {
            Entity newPassenger = data.entity.changeDimension(destination, teleporter);
            if (newPassenger == null) continue; // shouldn't happen unless this method is misused by another mod

            newPassenger.setPos(newRocket.position().add(data.offset));
            ((EntityMixin_I) newPassenger).setRidingRocket(newRocket);

            if (data.seat != -1)
                newRocket.addSittingPassenger(newPassenger, data.seat);
        }

        if (controllingPlayer != null)
            NorthstarPackets.getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new RocketControlPacket(controllingPlayer, getId(), getContraption().localControlsPos));

        fixEntityMounting(true);
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

    @OnlyIn(Dist.CLIENT)
    public static void handleSyncPacket(RocketContraptionSyncPacket packet) {
        Entity entity = Minecraft.getInstance().level.getEntity(packet.contraptionEntityId);
        if (!(entity instanceof RocketContraptionEntity rce)) return;

        // abruptly changing the position is what causes the player to fall out of the rocket
        //  this is disabled temporarily as a fix until the rocket system will be rewritten.
        //rce.setPos(packet.pos.x, packet.pos.y, packet.pos.z);
        rce.lift_vel = packet.lift_vel;
        rce.launchTime = packet.launchTime;
        rce.launchingMode = packet.launched;
        rce.landingMode = packet.landing;
        rce.blasting = packet.blasting;
        rce.slowing = packet.slowing;
        rce.activeLaunch = packet.activeLaunch;
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleQuickSyncPacket(RocketContraptionQuickSyncPacket packet) {
        Entity entity = Minecraft.getInstance().level.getEntity(packet.contraptionEntityId);
        if (!(entity instanceof RocketContraptionEntity rce)) return;

        rce.slowing = packet.slowing;
    }

    public ItemStack createReturnTicket() {
        ItemStack result = new ItemStack(NorthstarItems.RETURN_TICKET.get());
        result.setHoverName(Component.translatable("item.northstar.return_ticket" + "_" + NorthstarPlanets.getPlanetName(home)).setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA).withItalic(false)));
        CompoundTag tag = result.getOrCreateTagElement("Planet");
        tag.putString("name", NorthstarPlanets.getPlanetName(home));
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
        return getName();
    }

    @Override
    public boolean startControlling(BlockPos controlsLocalPos, Player player) {
        return player != null && !player.isSpectator();
    }

    public boolean collidesWithBlocks(Direction dir) {
        if (!(contraption instanceof RocketContraption))
            return false;

        return isCollidingWithWorld(level(), getContraption(), BlockPos.containing(position()).relative(dir), dir);
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

    @Override
    public boolean control(BlockPos controlsLocalPos, Collection<Integer> heldControls, Player player) {
        if (player.isSpectator() ||
                !toGlobalVector(VecHelper.getCenterOf(controlsLocalPos), 1).closerThan(player.position(), 8) || //If we are too far from the controls
                heldControls.contains(5)) { //If we press sneak key to dismount
            return false;
        }

        boolean spaceDown = heldControls.contains(4);
        if (spaceDown && launchingMode && launchTime == 0 && !blasting) {
            startLaunchSequence();
        }
        if (spaceDown && landingMode) {
            slowing = true;
        }
        return true;
    }

    public void startLaunchSequence() {
        launchTime = LAUNCH_COUNTDOWN_TICKS;
        activeLaunch = true;
    }

    public void cancelLaunch() {
        launchTime = 0;
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
        if (spaceDown && launchingMode && launchTime == 0 && !blasting) {
            startLaunchSequence();
        }
        if (spaceDown && landingMode) {
            slowing = true;
        }
        return true;
    }

    public boolean isLaunchingOrLanding() {
        return blasting || landingMode;
    }

    public boolean isActiveLaunch() {
        return activeLaunch;
    }

    public int getLaunchTime() {
        return launchTime;
    }

    public double getSlowdownHeightThreshold() {
        return level().getMaxBuildHeight() + 150;
    }

    public List<Entity> getEntitiesWithinContraption() {
        return entitiesWithinContraption;
    }

    @Override
    public RocketContraption getContraption() {
        return (RocketContraption) contraption;
    }

    @Override
    protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
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

        compound.putFloat("lift_vel", lift_vel);
        compound.putFloat("final_lift_vel", final_lift_vel);
        compound.putBoolean("auto_land", auto_land_mode);
    }

    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnData) {
        super.readAdditional(compound, spawnData);

        blasting = compound.contains("blasting") && compound.getBoolean("SequencedOffsetLimit");
        slowing = compound.contains("slowing") && compound.getBoolean("slowing");
        isUsingTicket = compound.contains("isUsingTicket") && compound.getBoolean("isUsingTicket");
        launchingMode = compound.contains("launched") && compound.getBoolean("launched");

        landingMode = compound.contains("landing") && compound.getBoolean("landing");
        fuelBurned = compound.contains("fuelBurned") && compound.getBoolean("fuelBurned");
        printed = compound.contains("printed") && compound.getBoolean("printed");
        activeLaunch = compound.contains("activeLaunch") && compound.getBoolean("activeLaunch");

        isUsingTicket = compound.contains("isUsingTicket") && compound.getBoolean("isUsingTicket");

        if (compound.contains("home"))
            home = NorthstarPlanets.getPlanetDimension(compound.getString("home"));
        if (compound.contains("destination"))
            destination = NorthstarPlanets.getPlanetDimension(compound.getString("destination"));
        if (compound.contains("player"))
            ownerID = compound.getUUID("player");
        if (compound.contains("lift_vel"))
            lift_vel = compound.getFloat("lift_vel");
        if (compound.contains("final_lift_vel"))
            final_lift_vel = compound.getFloat("final_lift_vel");

        auto_land_mode = compound.contains("auto_land") && compound.getBoolean("auto_land");
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

}
