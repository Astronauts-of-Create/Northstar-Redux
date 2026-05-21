package com.lightning.northstar.accessor;

import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.function.Consumer;

public interface NorthstarBuilder<T> {

    @SuppressWarnings("unchecked")
    default T apply(Consumer<T> action) {
        action.accept((T) this);
        return (T) this;
    }

    interface BiomeGenerationSettingsBuilder extends NorthstarBuilder<BiomeGenerationSettings.Builder> {
    }

    interface MobSpawnSettingsBuilder extends NorthstarBuilder<MobSpawnSettings.Builder> {
    }

}
