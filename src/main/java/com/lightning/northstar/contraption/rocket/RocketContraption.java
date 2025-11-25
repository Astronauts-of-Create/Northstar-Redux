package com.lightning.northstar.contraption.rocket;

import com.lightning.northstar.block.tech.computer_rack.TargetingComputerRackBlockEntity;
import com.lightning.northstar.block.tech.jet_engine.JetEngineBlock;
import com.lightning.northstar.block.tech.rocket_station.RocketStationBlockEntity;
import com.lightning.northstar.compat.copycats.CopycatsPlusHelper;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarContraptionTypes;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.contraption.FuelType;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import com.simibubi.create.content.contraptions.minecart.TrainCargoManager;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
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
    public String name = "Rocket";
    public Player owner;
    public BlockPos localControlsPos;

    public ResourceKey<Level> dest = null;

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

    public void burnFuel() {
        IFluidHandler tanks = storage.getFluids();

        float energyToBurn = weightCost + fuelCost * (1 - computingPower);

        for (int slot = 0; slot < tanks.getTanks(); slot++) {
            FluidStack stack = tanks.getFluidInTank(slot);
            FuelType fuel = FuelType.getFuelType(stack.getFluid());
            if (fuel == null || Mth.equal(fuel.gjPerMb(), 0))
                continue;
            int burnable = Math.min(Mth.floor(energyToBurn / fuel.gjPerMb()), stack.getAmount());

            stack.shrink(burnable);
            energyToBurn -= burnable;
            if (energyToBurn < 1)
                return;
        }
    }

    @Override
    protected Pair<StructureBlockInfo, BlockEntity> capture(Level world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (NorthstarBlocks.ROCKET_STATION.has(blockState)) {
            rocket_station = true;
            if (blockEntity instanceof RocketStationBlockEntity rsbe) {
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
            this.hasAutoLander = true;
        }
        if (blockState.is(NorthstarBlocks.INTERPLANETARY_NAVIGATOR.get())) {
            this.hasInterplanetaryNavigation = true;
        }
        if (NorthstarBlocks.COMPUTER_RACK.has(blockState)) {
            if (blockEntity instanceof TargetingComputerRackBlockEntity crbe) {
                for (int b = 0; b < crbe.container.getContainerSize(); b++) {
                    if (crbe.container.getItem(b).is(NorthstarItems.TARGETING_COMPUTER.get())) {
                        if (computingPower < 0.4)
                            computingPower += 0.0025;
                    }
                }
            }
        }

        if (NorthstarBlocks.ROCKET_CONTROLS.has(blockState)) {
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
        if (blockEntity instanceof FluidTankBlockEntity tank && Float.isFinite(fuelAmount)) {
            FluidTank tankInventory = tank.getTankInventory();
            for (int i = 0; i < tankInventory.getTanks(); i++) {
                FuelType fuel = FuelType.getFuelType(tankInventory.getFluidInTank(i).getFluid());
                if (fuel != null) {
                    fuelAmount += tankInventory.getFluidAmount() * fuel.gjPerMb();
                }
            }

            if (blockState.is(AllBlocks.CREATIVE_FLUID_TANK.get())) {
                fuelAmount = Float.POSITIVE_INFINITY;
            }
        }
        if (!blockState.is(Blocks.AIR) && !blockState.is(Blocks.CAVE_AIR)) {
            blockCount++;
        }

        BlockState copycat = CopycatsPlusHelper.$.getCopycatMaterial(blockEntity);
        if (copycat != null)
            blockState = copycat;
        if (blockEntity instanceof CopycatBlockEntity cc)
            blockState = cc.getMaterial();

        if (blockState.is(NorthstarBlockTags.HEAVY_BLOCKS.tag) && !blockState.is(Blocks.AIR))
            weightCost += 5;
        else if (blockState.is(NorthstarBlockTags.SUPER_HEAVY_BLOCKS.tag) && !blockState.is(Blocks.AIR))
            weightCost += 10;
        else if (!blockState.is(Blocks.AIR))
            weightCost += 1;

        if (blockState.is(NorthstarBlockTags.TIER_1_HEAT_RESISTANCE.tag) && !blockState.is(Blocks.AIR))
            heatShielding += 3;
        else if (blockState.is(NorthstarBlockTags.TIER_2_HEAT_RESISTANCE.tag) && !blockState.is(Blocks.AIR))
            heatShielding += 8;
        else if (blockState.is(NorthstarBlockTags.TIER_3_HEAT_RESISTANCE.tag) && !blockState.is(Blocks.AIR))
            heatShielding += 20;
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
