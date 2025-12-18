package com.lightning.northstar.block.tech.electrolysis_machine;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarRecipeTypes;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.IRotate.StressImpact;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElectrolysisMachineBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {

    public static final BehaviourType<SmartFluidTankBehaviour>
            OUTPUT1 = new BehaviourType<>("Output1"),
            OUTPUT2 = new BehaviourType<>("Output2");

    private static final Object ELECTROLYSIS_RECIPE_KEY = new Object();

    protected SmartFluidTankBehaviour inputTank;
    protected SmartFluidTankBehaviour outputTankL;
    protected SmartFluidTankBehaviour outputTankR;
    protected float processingTime;
    protected Recipe<?> currentRecipe;

    public ElectrolysisMachineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        inputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 1000, true);
        outputTankL = new SmartFluidTankBehaviour(OUTPUT1, this, 1, 1000, true).forbidInsertion();
        outputTankR = new SmartFluidTankBehaviour(OUTPUT2, this, 1, 1000, true).forbidInsertion();
        behaviours.add(inputTank);
        behaviours.add(outputTankL);
        behaviours.add(outputTankR);
    }

    @Override
    public void tick() {
        super.tick();

        if (!ElectrolysisRecipe.match(this, currentRecipe)) {
            currentRecipe = RecipeFinder.get(ELECTROLYSIS_RECIPE_KEY, level, this::matchStaticFilters)
                    .stream()
                    .filter(r -> ElectrolysisRecipe.match(this, r))
                    .findFirst()
                    .orElse(null);
        }
        if (currentRecipe == null) {
            return;
        }


        processingTime += Math.abs(speed);

        int toProcess = (int) (processingTime / 256);
        processingTime %= 256;

        for (int i = 0; i < toProcess; i++) {
            if (!ElectrolysisRecipe.apply(this, currentRecipe, false)) {
                break;
            }
        }
    }

    protected <C extends Container> boolean matchStaticFilters(Recipe<C> recipe) {
        return recipe.getType() == NorthstarRecipeTypes.ELECTROLYSIS.getType();
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("ProcessingTime", processingTime);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        processingTime = compound.getFloat("ProcessingTime");
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        Lang.translate("gui.goggles.electrolysis_machine")
                .forGoggles(tooltip);

        if (StressImpact.isEnabled())
            addStressImpactStats(tooltip, calculateStressApplied());

        addTankToolTip(tooltip, "gui.goggles.electrolysis_input", inputTank);
        addTankToolTip(tooltip, "gui.goggles.electrolysis_orange_port", outputTankL);
        addTankToolTip(tooltip, "gui.goggles.electrolysis_blue_port", outputTankR);
        return true;
    }

    private void addTankToolTip(List<Component> tooltip, String color, SmartFluidTankBehaviour tank) {
        FluidStack fluidStack = tank.getPrimaryHandler().getFluidInTank(0);

        if (!fluidStack.getFluid().getFluidType().isAir()) {
            Lang.translate(color)
                    .add(Lang.fluidName(fluidStack))
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
        } else {
            Lang.translate(color)
                    .add(Lang.translate("gui.goggles.empty"))
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
        }

        Lang.builder(Northstar.MOD_ID)
                .add(Lang.number(fluidStack.getAmount())
                        .add(Lang.translate("generic.unit.millibuckets"))
                        .style(ChatFormatting.GOLD))
                .text(ChatFormatting.GRAY, " / ")
                .add(Lang.number(tank.getPrimaryHandler().getTankCapacity(0))
                        .add(Lang.translate("generic.unit.millibuckets"))
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (isFluidHandlerCap(cap)) {
            if (side == null)
                return LazyOptional.of(() -> new CombinedTankWrapper(inputTank.getPrimaryHandler(), outputTankL.getPrimaryHandler(), outputTankR.getPrimaryHandler())).cast();
            if (side == Direction.UP)
                return inputTank.getCapability().cast();
            Direction facing = getBlockState().getValue(ElectrolysisMachineBlock.HORIZONTAL_FACING);
            if (side == facing.getClockWise())
                return outputTankL.getCapability().cast();
            if (side == facing.getCounterClockWise())
                return outputTankR.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }

}
