package com.lightning.northstar.compat.kubejs;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.compat.kubejs.recipe.ElectrolysisRecipeSchema;
import com.lightning.northstar.compat.kubejs.recipe.EngravingRecipeSchema;
import com.lightning.northstar.compat.kubejs.recipe.FreezingRecipeSchema;
import com.lightning.northstar.data.ModCompat;
import com.lightning.northstar.item.NorthstarRecipeTypes;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeFactoryRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;

public class NorthstarKubeJsPlugin implements KubeJSPlugin {

    private boolean enabled;

    @Override
    public void init() {
        // this could be done inside kubejs.plugins.txt but the message isn't always displayed and is less explicit
        enabled = ModCompat.KJS_CREATE.isLoaded();
        if (!enabled) {
            Northstar.LOGGER.error("KubeJS compatibility depends on KubeJS Create which is not installed. Northstar recipes will not be available.");
        }
    }

    @Override
    public void registerRecipeFactories(RecipeFactoryRegistry registry) {
        if (!enabled)
            return;
        registry.register(FreezingRecipeSchema.FreezingRecipeJS.RECIPE_FACTORY);
    }

    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry registry) {
        if (!enabled)
            return;
        for (NorthstarRecipeTypes type : NorthstarRecipeTypes.values()) {
            RecipeSchema schema = switch (type) {
                case FREEZING -> FreezingRecipeSchema.SCHEMA;
                case ENGRAVING -> EngravingRecipeSchema.SCHEMA;
                case ELECTROLYSIS -> ElectrolysisRecipeSchema.SCHEMA;
            };
            registry.register(type.getId(), schema);
        }
    }

}
