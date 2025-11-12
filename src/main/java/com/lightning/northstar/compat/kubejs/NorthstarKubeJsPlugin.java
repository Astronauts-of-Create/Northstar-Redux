package com.lightning.northstar.compat.kubejs;

import com.lightning.northstar.compat.kubejs.recipe.ElectrolysisRecipeSchema;
import com.lightning.northstar.compat.kubejs.recipe.EngravingRecipeSchema;
import com.lightning.northstar.compat.kubejs.recipe.FreezingRecipeSchema;
import com.lightning.northstar.item.NorthstarRecipeTypes;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;

public class NorthstarKubeJsPlugin extends KubeJSPlugin {

    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        for (NorthstarRecipeTypes type : NorthstarRecipeTypes.values()) {
            RecipeSchema schema = switch (type) {
                case FREEZING -> FreezingRecipeSchema.SCHEMA;
                case ENGRAVING -> EngravingRecipeSchema.SCHEMA;
                case ELECTROLYSIS -> ElectrolysisRecipeSchema.SCHEMA;
            };
            event.register(type.getId(), schema);
        }
    }

}
