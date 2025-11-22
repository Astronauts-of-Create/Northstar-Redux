package com.lightning.northstar.compat.kubejs.recipe;

import com.lightning.northstar.Northstar;
import com.mojang.datafixers.util.Either;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.TickDuration;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public interface FreezingRecipeSchema {

    RecipeKey<List<Either<SizedFluidIngredient, Ingredient>>> INGREDIENTS = NorthstarComponents.SIZED_FLUID_OR_ITEM_LIST.inputKey("ingredients");
    RecipeKey<List<Either<FluidStack, ProcessingOutput>>> RESULTS = NorthstarComponents.FLUID_OR_PROCESSING_OUTPUT_LIST.outputKey("results");
    RecipeKey<Integer> MIN_TEMPERATURE = NumberComponent.INT.inputKey("minTemperature").optional(Integer.MIN_VALUE);
    RecipeKey<Integer> MAX_TEMPERATURE = NumberComponent.INT.inputKey("maxTemperature").optional(Integer.MAX_VALUE);
    RecipeKey<TickDuration> TIME = TimeComponent.TICKS.inputKey("processingTime").optional(TickDuration.of(100));

    class FreezingRecipeJS extends KubeRecipe {
        public static final KubeRecipeFactory RECIPE_FACTORY = new KubeRecipeFactory(Northstar.asResource("freezing"), FreezingRecipeJS.class, FreezingRecipeJS::new);

        public KubeRecipe duration(TickDuration ticks) {
            return setValue(TIME, ticks);
        }

        public KubeRecipe colderThan(int temp) {
            return withinTemperature(Integer.MIN_VALUE, temp);
        }

        public KubeRecipe hotterThan(int temp) {
            return withinTemperature(temp, Integer.MAX_VALUE);
        }

        public KubeRecipe withTemperature(int temp) {
            return withinTemperature(temp, temp);
        }

        public KubeRecipe withinTemperature(int min, int max) {
            return setValue(MIN_TEMPERATURE, min).setValue(MAX_TEMPERATURE, max);
        }
    }

    RecipeSchema SCHEMA = new RecipeSchema(RESULTS, INGREDIENTS, MIN_TEMPERATURE, MAX_TEMPERATURE, TIME)
            .constructor(RESULTS, INGREDIENTS, TIME)
            .factory(FreezingRecipeJS.RECIPE_FACTORY);

}
