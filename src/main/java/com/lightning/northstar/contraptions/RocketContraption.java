package com.lightning.northstar.contraptions;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.computer_rack.TargetingComputerRackBlockEntity;
import com.lightning.northstar.block.tech.jet_engine.JetEngineBlock;
import com.lightning.northstar.block.tech.rocket_station.RocketStationBlockEntity;
import com.lightning.northstar.content.*;
import com.lightning.northstar.data.FuelType;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import com.simibubi.create.content.contraptions.minecart.TrainCargoManager;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.portal.DimensionTransition;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class RocketContraption extends TranslatingContraption {

    public int fuelCost = 0;
    public int fuelReturnCost = 0;
    public int weightCost = 0;
    public int heatShielding = 0;
    public int blockCount = 0;
    private boolean rocket_station = false;
    public boolean hasControls = false;
    public boolean hasInterplanetaryNavigation = false;
    public boolean hasAutoLander = false;
    public boolean isUsingTicket;
    private float fuelAmount = 0;
    private int jet_engines = 0;
    private int visual_jet_engines = 0;
    public float computingPower = 0;
    private List<BlockPos> assembledJets;
    public int fuelTicks;
    public String name = "Rocket";
    public Player owner;
    public BlockPos localControlsPos;

    protected MountedStorageManager storageProxy;

    public ResourceKey<Level> dest = null;

    static final IItemHandlerModifiable fallbackItems = new ItemStackHandler();
    static final IFluidHandler fallbackFluids = new FluidTank(0);


    public float prevYaw;
    public float yaw;
    public float targetYaw;

    public RocketContraption() {
        assembledJets = new ArrayList<>();
        storage = new TrainCargoManager();
    }

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        if (!searchMovedStructure(world, pos, null))
            return false;
        System.out.print(blocks.size());
        startMoving(world);

        return true;
    }

    public void burnFuel(Level level) {
        IFluidHandler tanks = storage.getFluids();

        float fuelToBurn = weightCost + fuelCost * (1 - computingPower);
        for (int slot = 0; slot < tanks.getTanks(); slot++) {
            FluidStack stack = tanks.getFluidInTank(slot);
            FuelType fuel = FuelType.getFuelType(level.registryAccess(), stack.getFluid());
            if (fuel != null && stack.getAmount() >= fuel.rocketMultiplier()) {
                float toBurn = Math.min(fuelToBurn / fuel.rocketMultiplier(), stack.getAmount());

                stack.shrink((int) toBurn);
                fuelToBurn -= toBurn;
                if (fuelToBurn < 1)
                    return;
            }
        }
    }

    @Override
    protected Pair<StructureBlockInfo, BlockEntity> capture(Level world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);

        if (NorthstarTechBlocks.ROCKET_STATION.has(blockState)) {
            rocket_station = true;
            BlockEntity ent = world.getBlockEntity(pos);
            if (ent instanceof RocketStationBlockEntity rsbe) {
                name = rsbe.name;

                // this is a bit sketchy in game, it should delete the ticket after it's
                // actually been used, not when assembling the rocket
                // though I can't figure that out so this may have to do

                if (rsbe.container.getItem(0).is(NorthstarItems.RETURN_TICKET.get())) {
                    if (rsbe.container.getItem(0).has(NorthstarDataComponents.PLANET)) {
                        if (NorthstarPlanets.getPlanetDimension(rsbe.container.getItem(0).get(NorthstarDataComponents.PLANET)) == dest)
                            this.isUsingTicket = true;
                        this.isUsingTicket = true;
                    }
                }
            }
        }
        if (blockState.is(NorthstarBlocks.AUTO_LANDER.get())) {
            Northstar.LOGGER.debug("AUTO LANDER");
            this.hasAutoLander = true;
        }
        if (blockState.is(NorthstarBlocks.INTERPLANETARY_NAVIGATOR.get())) {
            this.hasInterplanetaryNavigation = true;
        }
        if (NorthstarTechBlocks.COMPUTER_RACK.has(blockState)) {
            BlockEntity ent = world.getBlockEntity(pos);
            if (ent instanceof TargetingComputerRackBlockEntity crbe) {
                for (int b = 0; b < crbe.container.getContainerSize(); b++) {
                    if (crbe.container.getItem(b).is(NorthstarItems.TARGETING_COMPUTER.get())) {
                        if (computingPower < 0.4)
                            computingPower += 0.0025;
                    }
                }
            }
        }

        if (NorthstarTechBlocks.ROCKET_CONTROLS.has(blockState)) {
            hasControls = true;
            if (this.localControlsPos == null) {
                this.localControlsPos = this.toLocalPos(pos);
            }
        }
        if (blockState.getBlock() instanceof JetEngineBlock) {
            jet_engines += 1;
            if (!blockState.getValue(JetEngineBlock.BOTTOM)) {
                visual_jet_engines++;
            }
            assembledJets.add(toLocalPos(pos));
        }
        if (world.getBlockEntity(pos) instanceof FluidTankBlockEntity tank && Float.isFinite(fuelAmount)) {
            FluidTank tankInventory = tank.getTankInventory();
            for (int i = 0; i < tankInventory.getTanks(); i++) {
                FuelType fuel = FuelType.getFuelType(world.registryAccess(), tankInventory.getFluidInTank(i).getFluid());
                if (fuel != null) {
                    fuelAmount += tankInventory.getFluidAmount() * fuel.rocketMultiplier();
                }
            }

            if (blockState.is(AllBlocks.CREATIVE_FLUID_TANK.get())) {
                fuelAmount = Float.POSITIVE_INFINITY;
            }
        }
        if (!blockState.is(Blocks.AIR) && !blockState.is(Blocks.CAVE_AIR)) {
            blockCount++;
        }
        if (blockState.is(NorthstarTags.NorthstarBlockTags.HEAVY_BLOCKS.tag) && !blockState.is(Blocks.AIR)) {
            weightCost += 5;
        } else if (blockState.is(NorthstarTags.NorthstarBlockTags.SUPER_HEAVY_BLOCKS.tag) && !blockState.is(Blocks.AIR)) {
            weightCost += 10;
        } else if (!blockState.is(Blocks.AIR)) {
            weightCost += 1;
        }
        if (blockState.is(NorthstarTags.NorthstarBlockTags.TIER_1_HEAT_RESISTANCE.tag) && !blockState.is(Blocks.AIR)) {
            heatShielding += 3;
        }
        if (blockState.is(NorthstarTags.NorthstarBlockTags.TIER_2_HEAT_RESISTANCE.tag) && !blockState.is(Blocks.AIR)) {
            heatShielding += 8;
        }
        if (blockState.is(NorthstarTags.NorthstarBlockTags.TIER_3_HEAT_RESISTANCE.tag) && !blockState.is(Blocks.AIR)) {
            heatShielding += 20;
        }
        return super.capture(world, pos);
    }

    @Override
    public boolean canBeStabilized(Direction facing, BlockPos localPos) {
        return false;
    }

    @Override
    public ContraptionType getType() {
        return NorthstarContraptionTypes.ROCKET;
    }

    public boolean hasRocketStation() {
        return rocket_station;
    }

    public int hasJetEngine() {
        return jet_engines;
    }

    public int getVisualJetEngines() {
        return visual_jet_engines;
    }

    public float fuelAmount() {
        return fuelAmount;
    }

    public int heatShielding() {
        return heatShielding;
    }


    @Override
    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return super.isAnchoringBlockAt(pos.relative(Direction.DOWN));
    }

}
