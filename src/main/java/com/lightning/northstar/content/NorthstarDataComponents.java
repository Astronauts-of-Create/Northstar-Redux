package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.telescope.AstronomicalReadingData;
import com.lightning.northstar.contraption.rocket.RocketDestination;
import com.lightning.northstar.item.atlas.SpaceAtlasContent;
import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NorthstarDataComponents {

    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Northstar.MOD_ID);

    public static final DataComponentType<Integer> OXYGEN = register("oxygen", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DataComponentType<SpaceAtlasContent> SPACE_ATLAS_CONTENT = register("space_atlas_content", SpaceAtlasContent.CODEC, null);
    public static final DataComponentType<RocketDestination> RETURN_DESTINATION = register("return_destination", RocketDestination.CODEC, null);
    public static final DataComponentType<AstronomicalReadingData> ASTRONOMICAL_READING_DATA = register("astronomical_reading_data", AstronomicalReadingData.CODEC, null);

    private static <T> DataComponentType<T> register(
            String name,
            @Nullable Codec<T> persitentCodec,
            @Nullable StreamCodec<? super RegistryFriendlyByteBuf, T> networkCodec
    ) {
        DataComponentType.Builder<T> builder = DataComponentType.builder();
        if (persitentCodec != null) builder.persistent(persitentCodec);
        if (networkCodec != null) builder.networkSynchronized(networkCodec);
        DataComponentType<T> type = builder.build();
        DATA_COMPONENTS.register(name, () -> type);
        return type;
    }

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }

}
