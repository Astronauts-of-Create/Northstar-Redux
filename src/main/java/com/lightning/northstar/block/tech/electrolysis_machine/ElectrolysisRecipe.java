package com.lightning.northstar.block.tech.electrolysis_machine;

import com.lightning.northstar.content.NorthstarRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ElectrolysisRecipe extends StandardProcessingRecipe<RecipeWrapper> {

    public static boolean match(ElectrolysisMachineBlockEntity machine, Recipe<?> recipe) {
        if (!(recipe instanceof ElectrolysisRecipe er)) {
            return false;
        }

        FluidStack fluid = machine.inputTank.getPrimaryHandler().getFluid();
        if (!fluid.isEmpty() && !er.getFluidIngredients().get(0).test(fluid)) {
            return false;
        }

        FluidStack left = machine.outputTankL.getPrimaryHandler().getFluid();
        if (!left.isEmpty() && !FluidStack.isSameFluidSameComponents(left, er.getFluidResults().get(0))) {
            return false;
        }

        FluidStack right = machine.outputTankR.getPrimaryHandler().getFluid();
        if (!right.isEmpty() && !FluidStack.isSameFluidSameComponents(right, er.getFluidResults().get(1))) {
            return false;
        }

        return apply(machine, recipe, true);
    }

    public static boolean apply(ElectrolysisMachineBlockEntity machine, Recipe<?> recipe, boolean simulate) {
        if (!(recipe instanceof ElectrolysisRecipe er)) {
            return false;
        }

        if (er.getFluidIngredients().get(0).amount() > machine.inputTank.getPrimaryHandler().getFluidAmount()) {
            return false;
        }

        SmartFluidTank left = machine.outputTankL.getPrimaryHandler();
        if (er.getFluidResults().get(0).getAmount() > left.getCapacity() - left.getFluidAmount()) {
            return false;
        }

        SmartFluidTank right = machine.outputTankR.getPrimaryHandler();
        if (er.getFluidResults().get(1).getAmount() > right.getCapacity() - right.getFluidAmount()) {
            return false;
        }

        if (!simulate) {
            machine.inputTank.getPrimaryHandler().drain(er.getFluidIngredients().get(0).amount(), IFluidHandler.FluidAction.EXECUTE);
            left.fill(er.getFluidResults().get(0), IFluidHandler.FluidAction.EXECUTE);
            right.fill(er.getFluidResults().get(1), IFluidHandler.FluidAction.EXECUTE);
        }
        return true;
    }

    public ElectrolysisRecipe(ProcessingRecipeParams params) {
        super(NorthstarRecipeTypes.ELECTROLYSIS, params);

        // game would crash elsewhere if those conditions aren't met, ensure them at data load time to prevent it.
        if (getFluidIngredients().size() != 1)
            throw new IllegalArgumentException("Electrolysis recipes must have exactly 1 fluid input");
        if (getFluidResults().size() != 2)
            throw new IllegalArgumentException("Electrolysis recipes require 2 fluid results");
    }

    @Override
    protected int getMaxInputCount() {
        return 0;
    }

    @Override
    protected int getMaxOutputCount() {
        return 0;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 1;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 2;
    }

    @Override
    public boolean matches(RecipeWrapper input, Level level) {
        return true;
    }

}