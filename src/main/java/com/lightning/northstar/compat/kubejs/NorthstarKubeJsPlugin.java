package com.lightning.northstar.compat.kubejs;

import com.lightning.northstar.compat.kubejs.event.NorthstarKubeDataEvent;
import com.lightning.northstar.compat.kubejs.recipe.ElectrolysisRecipeSchema;
import com.lightning.northstar.compat.kubejs.recipe.EngravingRecipeSchema;
import com.lightning.northstar.compat.kubejs.recipe.FreezingRecipeSchema;
import com.lightning.northstar.content.NorthstarRecipeTypes;
import com.lightning.northstar.contraption.FuelType;
import com.lightning.northstar.mixin.compat.kubejs.DataPackEventJsAccessor;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;

public class NorthstarKubeJsPlugin extends KubeJSPlugin {

    public static final EventGroup EVENTS = EventGroup.of("NorthstarEvents");
    public static final EventHandler GENERATE_DATA_EVENT = EVENTS.server("generateData", () -> NorthstarKubeDataEvent.class);

    @Override
    public void registerEvents() {
        EVENTS.register();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("FuelType", FuelType.class);

        if (event.getType() == ScriptType.SERVER) {
            ServerEvents.LOW_DATA.listenJava(ScriptType.SERVER, null, e -> {
                if (e instanceof DataPackEventJsAccessor d) {
                    NorthstarKubeDataEvent dataEvent = new NorthstarKubeDataEvent(d.northstar$getVirtualDataPack(), d.northstar$getWrappedManager());
                    GENERATE_DATA_EVENT.post(dataEvent);
                    dataEvent.postProcess();
                }
                return null;
            });
        }
    }

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
