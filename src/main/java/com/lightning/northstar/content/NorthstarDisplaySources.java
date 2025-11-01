package com.lightning.northstar.content;

import com.lightning.northstar.block.display.TemperatureDisplaySource;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.tterrag.registrate.util.entry.RegistryEntry;

import java.util.function.Supplier;

import static com.lightning.northstar.Northstar.REGISTRATE;

public class NorthstarDisplaySources {

    public static final RegistryEntry<TemperatureDisplaySource> TEMPERATURE = simple("temperature", TemperatureDisplaySource::new);

    private static <T extends DisplaySource> RegistryEntry<T> simple(String name, Supplier<T> supplier) {
        return REGISTRATE.displaySource(name, supplier).register();
    }

}
