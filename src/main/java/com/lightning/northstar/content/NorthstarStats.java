package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NorthstarStats {

    private static final DeferredRegister<ResourceLocation> REGISTER = DeferredRegister.create(Registries.CUSTOM_STAT, Northstar.MOD_ID);

    public static final ResourceLocation
            INTERACT_WITH_ASTRONOMY_TABLE = register("interact_with_astronomy_table"),
            INTERACT_WITH_COMPUTER_RACK = register("interact_with_computer_rack"),
            INTERACT_WITH_OXYGEN_FILLER = register("interact_with_oxygen_filler"),
            INTERACT_WITH_OXYGEN_SEALER = register("interact_with_oxygen_sealer"),
            INTERACT_WITH_ROCKET_CONTROLS = register("interact_with_rocket_controls"),
            INTERACT_WITH_ROCKET_STATION = register("interact_with_rocket_station"),
            INTERACT_WITH_TELESCOPE = register("interact_with_telescope"),
            INTERACT_WITH_TEMPERATURE_REGULATOR = register("interact_with_temperature_regulator"),
            ROCKET_CRASHES = register("rocket_crashes"),
            ROCKET_LAUNCHES = register("rocket_launches"),
            ROCKET_TRAVELS = register("rocket_travels");

    private static ResourceLocation register(String name) {
        ResourceLocation id = Northstar.asResource(name);
        REGISTER.register(name, () -> id);
        return id;
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    public static void registerFormatters() {
        for (DeferredHolder<ResourceLocation, ?> entry : REGISTER.getEntries()) {
            Stats.CUSTOM.get(entry.get());
        }
    }

}
