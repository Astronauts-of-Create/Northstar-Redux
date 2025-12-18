package com.lightning.northstar.compat.kubejs;

import com.lightning.northstar.compat.kubejs.event.NorthstarKubeDataEvent;
import com.lightning.northstar.compat.kubejs.recipe.ElectrolysisRecipeSchema;
import com.lightning.northstar.compat.kubejs.recipe.EngravingRecipeSchema;
import com.lightning.northstar.compat.kubejs.recipe.FreezingRecipeSchema;
import com.lightning.northstar.content.NorthstarRecipeTypes;
import com.lightning.northstar.contraption.FuelType;
import com.lightning.northstar.data.ModCompat;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeFactoryRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.GeneratedDataStage;

public class NorthstarKubeJsPlugin implements KubeJSPlugin {

    public static final EventGroup EVENTS = EventGroup.of("NorthstarEvents");
    public static final EventHandler GENERATE_DATA_EVENT = EVENTS.server("generateData", () -> NorthstarKubeDataEvent.class);

    private boolean recipesEnabled;

    @Override
    public void init() {
        recipesEnabled = ModCompat.KJS_CREATE.isLoaded();
        if (!recipesEnabled) {
            ConsoleJS.SERVER.warn("[Northstar] KubeJS compatibility depends on KubeJS Create, which is not installed. Northstar recipes will not be available.");
        }
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(EVENTS);

        ServerEvents.GENERATE_DATA.listenJava(ScriptType.SERVER, GeneratedDataStage.REGISTRIES, event -> {
            if (event instanceof KubeDataGenerator generator) {
                NorthstarKubeDataEvent dataEvent = new NorthstarKubeDataEvent(generator);
                GENERATE_DATA_EVENT.post(dataEvent);
                dataEvent.postProcess();
            }
            return null;
        });
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("FuelType", FuelType.class);
    }

    @Override
    public void registerRecipeFactories(RecipeFactoryRegistry registry) {
        if (!recipesEnabled)
            return;
        registry.register(FreezingRecipeSchema.FreezingRecipeJS.RECIPE_FACTORY);
    }

    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry registry) {
        if (!recipesEnabled)
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
