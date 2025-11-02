package com.lightning.northstar.block.tech.rocket_station;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.contraption.rocket.RocketContraption;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.lightning.northstar.contraption.rocket.RocketHandler;
import com.lightning.northstar.world.sealer.ProgressiveBlockSealer;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.lightning.northstar.world.sealer.SealingMode;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.util.*;

public class RocketStationBlockEntity extends SmartBlockEntity implements IDisplayAssemblyExceptions, IControlContraption, MenuProvider {

    boolean assembleNextTick;
    public Player owner;
    protected AssemblyException lastException;
    public TrackTargetingBehaviour<GlobalStation> edgePoint;
    protected ItemStackHandler inventory;
    protected LazyOptional<IItemHandlerModifiable> itemCapability;
    public String name = "Bing Bong's Big Bonanza";

    protected int failedCarriageIndex;
    Direction assemblyDirection;
    int assemblyLength;

    public float offset;
    public int fuelCost;
    public int fuelReturnCost;
    public boolean running;
    public boolean needsContraption;
    public AbstractContraptionEntity movedContraption;
    protected boolean forceMove;
    protected ScrollOptionBehaviour<MovementMode> movementMode;
    protected boolean waitingForSpeedChange;
    protected double sequencedOffsetLimit;


    int i = 0;

    // Custom position sync
    protected float clientOffsetDiff;
    public ResourceKey<Level> target;


    public final Container container = new SimpleContainer(1);


    public static WorldAttached<Map<BlockPos, BoundingBox>> assemblyAreas = new WorldAttached<>(w -> new HashMap<>());

    public RocketStationBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        inventory = new ItemStackHandler();
        itemCapability = LazyOptional.of(() -> new CombinedInvWrapper(inventory));
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        registerAwardables(behaviours, AllAdvancements.CONTRAPTION_ACTORS);
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inventory);
    }

    public void queueAssembly(Player player) {
        owner = player;
        assembleNextTick = true;
    }


    public void enterAssembly() {
        assembleNextTick = true;
    }

    public void exitAssembly() {
        assembleNextTick = false;
    }

    @Override
    public void tick() {
        super.tick();
        i++;
        ItemStack item = container.getItem(0);
        if (item.getItem() == NorthstarItems.STAR_MAP.get() || item.getItem() == NorthstarItems.RETURN_TICKET.get()) {
            if (item.getTagElement("Planet") != null)
                target = NorthstarPlanets.getPlanetDimension(item.getTagElement("Planet").getString("name"));
        }
        fuelCost = fuelCalc();
        fuelReturnCost = fuelReturnCalc();

        if (assembleNextTick) {
            tryAssemble();
            assembleNextTick = false;
        }
    }


    private void tryAssemble() {
        BlockState blockState = getBlockState();
        if (!(blockState.getBlock() instanceof RocketStationBlock)) {
            return;
        }

        RocketContraption contraption = new RocketContraption();

        BlockEntity blockEntity = level.getBlockEntity(worldPosition);
        if (!(blockEntity instanceof RocketStationBlockEntity)) {
            return;
        }
        Direction movementDirection = Direction.UP;

        int engines = 0;
        boolean hasStation = false;
        float fuelAmount = 0;
        int requiredJets = 0;
        int heatShielding = 0;
        double heatCost = 0;
        double heatCostHome = 0;
        try {
            lastException = null;
            contraption.owner = owner;
            if (!contraption.assemble(level, worldPosition)) {
                return;
            }
            engines = contraption.hasJetEngine();
            fuelAmount = contraption.fuelAmount();
            heatShielding = contraption.heatShielding();
            hasStation |= contraption.hasRocketStation();
            contraption.fuelCost = fuelCost;
            contraption.fuelReturnCost = fuelReturnCost;
            contraption.dest = target;
            heatCost = (NorthstarTemperature.getHeatRating(target) * (contraption.blockCount)) + NorthstarTemperature.getHeatConstant(target);
            heatCostHome = (NorthstarTemperature.getHeatRating(level.dimension()) * (contraption.blockCount)) + NorthstarTemperature.getHeatConstant(level.dimension());
            if (heatCostHome > heatCost) {
                heatCost = heatCostHome;
            }
            requiredJets = engineCalc();

            sendData();
        } catch (AssemblyException e) {
            owner.displayClientMessage(Component.translatable("northstar.gui.rocket_too_big").withStyle(ChatFormatting.RED), false);
            owner.displayClientMessage(Component.translatable("northstar.gui.current_config_size").withStyle(ChatFormatting.RED), false);
            owner.displayClientMessage(Component.literal(AllConfigs.server().kinetics.maxBlocksMoved.get().toString()).withStyle(ChatFormatting.RED), false);
            lastException = e;
            sendData();
            return;
        }
        if (ContraptionCollider.isCollidingWithWorld(level, contraption, worldPosition.relative(movementDirection),
                movementDirection)) {

            if (!this.level.getBlockState(worldPosition.above()).isAir()) {
                contraption.owner.displayClientMessage(Component.literal
                        ("Rocket Stations require at least 1 block of space above them").withStyle(ChatFormatting.RED), false);
            } else {
                contraption.owner.displayClientMessage(Component.literal
                        ("Something's blocking your rocket! Make sure everything is glued together!").withStyle(ChatFormatting.RED), false);
            }
            return;
        } else {
            Northstar.LOGGER.debug("Obamna");
        }

        ProgressiveBlockSealer sealer = new ProgressiveBlockSealer(SealingMode.OXYGEN);
        // cannot rely on getContraptionWorld() yet as it depends on the entity to get the level
        Level contraptionWorld = new ContraptionWorld(level, contraption);
        int maximumSealedBlocks = NorthstarConfigs.server().oxygenSealerMaxContraptionSealed.get();
        boolean oxygenSealed = sealer.beginSeal(contraptionWorld, BlockPos.ZERO, Direction.UP) &&
                sealer.updateSeal(contraptionWorld, maximumSealedBlocks, maximumSealedBlocks) &&
                !sealer.hasLeak();

        boolean interplanetaryFlag = NorthstarPlanets.isInterplanetary(level.dimension(), target);
        if (interplanetaryFlag) {
            if (contraption.hasInterplanetaryNavigation) {
                interplanetaryFlag = false;
            }
        }
        if (interplanetaryFlag) {
            contraption.owner.displayClientMessage(Component.literal
                    ("Interplanetary travel requires a Interplanetary Navigator!").withStyle(ChatFormatting.RED), false);
        }
        //Assuming we have everything we need to assemble, let's do it
        if (engines >= requiredJets && hasStation && fuelAmount > (fuelCost + contraption.weightCost) && heatShielding >= heatCost && oxygenSealed && !interplanetaryFlag && contraption.hasControls && contraption.dest != null && contraption.dest != this.level.dimension()) {
            //Create the new contraption entity
            Northstar.LOGGER.debug("{}", engines);
            contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
            RocketContraptionEntity movedContraption = RocketContraptionEntity.create(level, contraption);
            BlockPos anchor = worldPosition;
            movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
            AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition);
            movedContraption.destination = target;
            //Assign auto lander
            movedContraption.auto_land_mode = contraption.hasAutoLander;
            movedContraption.home = level.dimension();
            RocketHandler.ROCKETS.add(movedContraption);
            level.addFreshEntity(movedContraption);
        } else {
            contraption.owner.displayClientMessage(Component.literal
                    ("Full Fuel Cost: " + (contraption.weightCost + contraption.fuelCost)).withStyle(ChatFormatting.GOLD), false);
            contraption.owner.displayClientMessage(Component.literal
                    ("Current Fuel Supply: " + (int) contraption.fuelAmount()).withStyle(ChatFormatting.GOLD), false);
            contraption.owner.displayClientMessage(Component.literal
                    ("Estimated Return Cost: " + (contraption.weightCost + fuelReturnCost)).withStyle(ChatFormatting.GOLD), false);
            contraption.owner.displayClientMessage(Component.literal
                    ("Required Heat Shielding: " + heatCost).withStyle(ChatFormatting.YELLOW), false);
            contraption.owner.displayClientMessage(Component.literal
                    ("Current Heat Shielding: " + contraption.heatShielding()).withStyle(ChatFormatting.YELLOW), false);
            contraption.owner.displayClientMessage(Component.literal
                    ("Required Engines: " + requiredJets).withStyle(ChatFormatting.BLUE), false);
            contraption.owner.displayClientMessage(Component.literal
                    ("Current Engine Count: " + contraption.hasJetEngine()).withStyle(ChatFormatting.BLUE), false);
            //contraption.owner.displayClientMessage(Component.literal("Oxygen Size: " + oxyCheck.size()).withStyle(ChatFormatting.AQUA), false);
            if (!oxygenSealed) {
                contraption.owner.displayClientMessage(Component.literal
                        ("Cockpit is not sealed, or too large!").withStyle(ChatFormatting.DARK_RED), false);
            }
            if (contraption.fuelAmount() < contraption.fuelCost) {
                contraption.owner.displayClientMessage(Component.literal
                        ("Insufficient fuel!").withStyle(ChatFormatting.DARK_RED), false);
            }
            if (contraption.heatShielding() < heatCost) {
                contraption.owner.displayClientMessage(Component.literal
                        ("Insufficient heat shielding!").withStyle(ChatFormatting.DARK_RED), false);
            }
            if (contraption.hasJetEngine() < requiredJets) {
                contraption.owner.displayClientMessage(Component.literal
                        ("Not enough Jet Engines!").withStyle(ChatFormatting.DARK_RED), false);
            }
            if (!contraption.hasControls) {
                contraption.owner.displayClientMessage(Component.literal
                        ("No controls present!").withStyle(ChatFormatting.DARK_RED), false);
            }
            if (container.getItem(0).isEmpty()) {
                contraption.owner.displayClientMessage(Component.literal
                        ("No star map or ticket present!").withStyle(ChatFormatting.DARK_RED), false);
            } else if (contraption.dest == null || contraption.dest == this.level.dimension()) {
                contraption.owner.displayClientMessage(Component.literal
                        ("Invalid Target!").withStyle(ChatFormatting.DARK_RED), false);
            }
            contraption.owner.displayClientMessage(Component.literal
                    ("Rocket failed to assemble!").withStyle(ChatFormatting.RED), false);
            Northstar.LOGGER.debug("No station or jet engine, Bruh!");
            Northstar.LOGGER.debug("Heat Cost: {}     Heat Shielding: {}", heatCost, heatShielding);
            Northstar.LOGGER.debug("Weight Cost: {}      Fuel Cost: {}", contraption.weightCost, fuelCost);
            exception(new AssemblyException(CreateLang.translateDirect("train_assembly.no_controls")), -1);
        }
    }

    public boolean isAssembling() {
        BlockState state = getBlockState();
        return state.hasProperty(StationBlock.ASSEMBLING) && state.getValue(StationBlock.ASSEMBLING);
    }

    public int fuelCalc() {
        String home = NorthstarPlanets.getPlanetName(this.level.dimension());
        String targ = NorthstarPlanets.getPlanetName(target);

        int home_x = (int) NorthstarPlanets.getPlanetX(home);
        int home_y = (int) NorthstarPlanets.getPlanetY(home);

        int targ_x = (int) NorthstarPlanets.getPlanetX(targ);
        int targ_y = (int) NorthstarPlanets.getPlanetY(targ);

        int dif = (int) (Math.pow(home_x - targ_x, 2) + Math.pow(home_y - targ_y, 2));
        dif = Mth.roundToward(dif, 100) / 20;
        int cost = dif + NorthstarPlanets.getPlanetAtmosphereCost(this.level.dimension()) + 1000;

        if (dif != 0) {
            Northstar.LOGGER.debug("{}", dif);
        }
        return cost * 8;
    }

    public int fuelReturnCalc() {
        String home = NorthstarPlanets.getPlanetName(this.level.dimension());
        String targ = NorthstarPlanets.getPlanetName(target);

        int home_x = (int) NorthstarPlanets.getPlanetX(home);
        int home_y = (int) NorthstarPlanets.getPlanetY(home);

        int targ_x = (int) NorthstarPlanets.getPlanetX(targ);
        int targ_y = (int) NorthstarPlanets.getPlanetY(targ);

        int dif = (int) (Math.pow(home_x - targ_x, 2) + Math.pow(home_y - targ_y, 2));
        dif = Mth.roundToward(dif, 100) / 20;
        int cost = dif + NorthstarPlanets.getPlanetAtmosphereCost(target) + 1000;

        if (dif != 0) {
            Northstar.LOGGER.debug("{}", dif);
        }
        return cost * 8;
    }

    public int engineCalc() {
        int homeAtmos = NorthstarPlanets.getPlanetAtmosphereCost(level.dimension()) / 100;
        int targetAtmos = NorthstarPlanets.getPlanetAtmosphereCost(target) / 100;

        double grav = NorthstarPlanets.getGravMultiplier(target);
        double homeGrav = NorthstarPlanets.getGravMultiplier(level.dimension());
        if (grav < homeGrav) {
            grav = homeGrav;
        }
        double constant = NorthstarPlanets.getEngineConstant(target);
        double homeConstant = NorthstarPlanets.getEngineConstant(level.dimension());
        if (constant < homeConstant) {
            constant = homeConstant;
        }

        return (int) (Mth.clamp(((targetAtmos + homeAtmos) * grav), 6, 64) + constant);
    }

    // this is extremely buggy for some reason, this NEEDS to be fixed before release
    //not sure what's making it so buggy but it saves really inconsistently despite sharing the code of the oxygen filler which works fine
    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.put("item", container.getItem(0).serializeNBT());
    }

    @Override
    public void writeSafe(CompoundTag compound) {
        super.writeSafe(compound);
        compound.put("item", container.getItem(0).serializeNBT());
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if (compound.contains("item", 10)) {
            container.setItem(0, ItemStack.of(compound.getCompound("item")));
        }
    }

    private void exception(AssemblyException exception, int carriage) {
        failedCarriageIndex = carriage;
        lastException = exception;
        sendData();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return super.getCapability(cap, side);
    }


    @SuppressWarnings("unused")
    private boolean shouldAssemble() {
        BlockState blockState = getBlockState();
        if (!(blockState.getBlock() instanceof RocketStationBlock))
            return false;
        return true;
    }

    @Override
    public AssemblyException getLastAssemblyException() {
        return lastException;
    }

    public Direction getAssemblyDirection() {
        if (assemblyDirection != null)
            return assemblyDirection;
        if (!edgePoint.hasValidTrack())
            return null;
        BlockPos targetPosition = edgePoint.getGlobalPosition();
        BlockState trackState = edgePoint.getTrackBlockState();
        ITrackBlock track = edgePoint.getTrack();
        AxisDirection axisDirection = edgePoint.getTargetDirection();
        Vec3 axis = track.getTrackAxes(level, targetPosition, trackState)
                .get(0)
                .normalize()
                .scale(axisDirection.getStep());
        return assemblyDirection = Direction.getNearest(axis.x, axis.y, axis.z);
    }

    @Override
    public boolean isValid() {
        return !isRemoved();
    }

    @Override
    public void attach(ControlledContraptionEntity contraption) {
        this.movedContraption = contraption;
        if (!level.isClientSide) {
            this.running = true;
            sendData();
        }
    }

    @Override
    public boolean isAttachedTo(AbstractContraptionEntity contraption) {
        return movedContraption == contraption;
    }

    @Override
    public BlockPos getBlockPosition() {
        return worldPosition;
    }

    @Override
    public void onStall() {
        if (!level.isClientSide) {
            forceMove = true;
            sendData();
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return RocketStationMenu.create(id, inv, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Rocket Station");
    }

}
