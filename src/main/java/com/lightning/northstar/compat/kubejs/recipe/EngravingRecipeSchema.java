package com.lightning.northstar.compat.kubejs.recipe;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeConstructor;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface EngravingRecipeSchema {

    RecipeKey<InputItem[]> INGREDIENTS = ItemComponents.INPUT_ARRAY.key("ingredients");
    RecipeKey<OutputItem[]> RESULTS = ItemComponents.OUTPUT_ARRAY.key("results");
    RecipeKey<Long> TIME = TimeComponent.TICKS.key("processingTime").optional(100L);

    RecipeConstructor.Factory FACTORY = (recipe, schemaType, keys, from) -> {
        InputItem[] input = from.getValue(recipe, INGREDIENTS);
        if (input.length != 1)
            throw new RecipeExceptionJS("Engraving recipes must have exactly 1 item input");

        OutputItem[] output = from.getValue(recipe, RESULTS);
        if (output.length != 1)
            throw new RecipeExceptionJS("Engraving recipes require 1 item result");

        recipe.setValue(INGREDIENTS, input);
        recipe.setValue(RESULTS, output);
        recipe.setValue(TIME, from.getValue(recipe, TIME));
    };

    RecipeSchema SCHEMA = new RecipeSchema(RESULTS, INGREDIENTS, TIME)
            .constructor(FACTORY, RESULTS, INGREDIENTS, TIME);

}
