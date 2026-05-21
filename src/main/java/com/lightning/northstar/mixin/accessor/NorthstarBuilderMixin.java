package com.lightning.northstar.mixin.accessor;

import com.lightning.northstar.accessor.NorthstarBuilder;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.spongepowered.asm.mixin.Mixin;

public class NorthstarBuilderMixin {

    @Mixin(BiomeGenerationSettings.Builder.class)
    public static class BiomeGenerationSettingsMixin implements NorthstarBuilder.BiomeGenerationSettingsBuilder {
    }

    @Mixin(MobSpawnSettings.Builder.class)
    public static class MobSpawnSettingsMixin implements NorthstarBuilder.MobSpawnSettingsBuilder {
    }

}
