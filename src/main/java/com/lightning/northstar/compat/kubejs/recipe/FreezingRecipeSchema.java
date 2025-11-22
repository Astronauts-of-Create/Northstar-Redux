package com.lightning.northstar.compat.kubejs.recipe;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.FluidComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface FreezingRecipeSchema {

    RecipeKey<Either<InputFluid, InputItem>[]> INGREDIENTS = FluidComponents.INPUT_OR_ITEM_ARRAY.key("ingredients");
    RecipeKey<Either<OutputFluid, OutputItem>[]> OUTPUTS = FluidComponents.OUTPUT_OR_ITEM_ARRAY.key("results");
    RecipeKey<Integer> MIN_TEMPERATURE = NumberComponent.ANY_INT.key("minTemperature").optional(Integer.MIN_VALUE);
    RecipeKey<Integer> MAX_TEMPERATURE = NumberComponent.ANY_INT.key("maxTemperature").optional(Integer.MAX_VALUE);
    RecipeKey<Long> TIME = TimeComponent.TICKS.key("processingTime").optional(100L);

    class FreezingRecipeJS extends RecipeJS {
        public RecipeJS duration(long ticks) {
            return setValue(TIME, ticks);
        }

        public RecipeJS colderThan(int temp) {
            return withinTemperature(Integer.MIN_VALUE, temp);
        }

        public RecipeJS hotterThan(int temp) {
            return withinTemperature(temp, Integer.MAX_VALUE);
        }

        public RecipeJS withTemperature(int temp) {
            return withinTemperature(temp, temp);
        }

        public RecipeJS withinTemperature(int min, int max) {
            return setValue(MIN_TEMPERATURE, min).setValue(MAX_TEMPERATURE, max);
        }
    }

    RecipeSchema SCHEMA = new RecipeSchema(FreezingRecipeJS.class, FreezingRecipeJS::new, OUTPUTS, INGREDIENTS, MIN_TEMPERATURE, MAX_TEMPERATURE, TIME)
            .constructor(OUTPUTS, INGREDIENTS, TIME);

}
