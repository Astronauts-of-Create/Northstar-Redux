package com.lightning.northstar.compat.kubejs.recipe;

import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.FluidComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeConstructor;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface ElectrolysisRecipeSchema {

    RecipeKey<InputFluid[]> INGREDIENTS = FluidComponents.INPUT_ARRAY.key("ingredients");
    RecipeKey<OutputFluid[]> RESULTS = FluidComponents.OUTPUT_ARRAY.key("results");

    RecipeConstructor.Factory FACTORY = (recipe, schemaType, keys, from) -> {
        InputFluid[] input = from.getValue(recipe, INGREDIENTS);
        if (input.length != 1)
            throw new RecipeExceptionJS("Electrolysis recipes must have exactly 1 fluid input");

        OutputFluid[] output = from.getValue(recipe, RESULTS);
        if (output.length != 2)
            throw new RecipeExceptionJS("Electrolysis recipes require 2 fluid results");

        recipe.setValue(INGREDIENTS, input);
        recipe.setValue(RESULTS, output);
    };

    RecipeSchema SCHEMA = new RecipeSchema(RESULTS, INGREDIENTS)
            .constructor(FACTORY, RESULTS, INGREDIENTS);

}
