package com.lightning.northstar.compat.kubejs.recipe;

import com.lightning.northstar.compat.kubejs.component.ProcessingOutputRecipeComponent;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.TickDuration;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public interface EngravingRecipeSchema {

    RecipeKey<List<Ingredient>> INGREDIENTS = IngredientComponent.INGREDIENT.instance().asList().inputKey("ingredients");
    RecipeKey<List<ProcessingOutput>> RESULTS = ProcessingOutputRecipeComponent.TYPE.instance().asList().outputKey("results");
    RecipeKey<TickDuration> TIME = TimeComponent.TICKS.inputKey("processingTime").optional(TickDuration.of(100L));

    RecipeSchema SCHEMA = new RecipeSchema(RESULTS, INGREDIENTS, TIME);

}
