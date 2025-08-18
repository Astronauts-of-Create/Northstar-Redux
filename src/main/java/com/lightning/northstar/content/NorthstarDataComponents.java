package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class NorthstarDataComponents {

    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Northstar.MOD_ID);

    public static final DataComponentType<String> PLANET = register(
            "planet",
            builder -> builder.persistent(Codec.STRING)
    );

    public static final DataComponentType<Integer> PLANET_X = register(
            "planet_x",
            builder -> builder.persistent(Codec.INT)
    );
    public static final DataComponentType<Integer> PLANET_Y = register(
            "planet_y",
            builder -> builder.persistent(Codec.INT)
    );

    public static final DataComponentType<Integer> OXYGEN = register(
            "oxygen",
            builder -> builder.persistent(Codec.INT)
    );

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(name, () -> type);
        return type;
    }

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }

}
