package com.lightning.northstar.compat.kubejs.recipe;

import dev.latvian.mods.kubejs.create.recipe.CreateRecipeComponents;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.FluidStackComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public interface ElectrolysisRecipeSchema {

    RecipeKey<List<SizedFluidIngredient>> INGREDIENTS = CreateRecipeComponents.SIZED_FLUID_INGREDIENT.instance().asList().inputKey("ingredients");
    RecipeKey<List<FluidStack>> RESULTS = FluidStackComponent.FLUID_STACK.instance().asList().outputKey("results");

    RecipeSchema SCHEMA = new RecipeSchema(RESULTS, INGREDIENTS);

}
