package com.lightning.northstar.util;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IIngredientConsumer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraftforge.fluids.FluidStack;

public class BackportUtil {

    public static IIngredientConsumer addFluidSlot(IRecipeLayoutBuilder builder, int x, int y, FluidIngredient ingredient) {
        return addFluidSlot(builder, x, y, RecipeIngredientRole.INPUT)
                .addIngredients(ForgeTypes.FLUID_STACK, ingredient.getMatchingFluidStacks());

    }

    public static IIngredientConsumer addFluidSlot(IRecipeLayoutBuilder builder, int x, int y, FluidStack stack) {
        return addFluidSlot(builder, x, y, RecipeIngredientRole.OUTPUT)
                .addIngredient(ForgeTypes.FLUID_STACK, stack);
    }

    public static IIngredientConsumer addFluidSlot(IRecipeLayoutBuilder builder, int x, int y, RecipeIngredientRole role) {
        return builder.addSlot(role, x, y)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .setFluidRenderer(1, false, 16, 16); // make fluid take up the full slot;
    }

}
