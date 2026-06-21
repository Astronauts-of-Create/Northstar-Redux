package com.lightning.northstar.content.world.planet.core;

import com.lightning.northstar.Northstar;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class NorthstarBiomes {

    public static final ResourceKey<Biome>
            VOID = key("void");

    private static ResourceKey<Biome> key(String path) {
        return ResourceKey.create(Registries.BIOME, Northstar.asResource(path));
    }

    public static void bootstrap(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeature = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carver = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(
                VOID,
                new Biome.BiomeBuilder()
                        .temperature(1.0f)
                        .downfall(0.0f)
                        .hasPrecipitation(false)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .fogColor(0)
                                .waterColor(0x3f76e4) // values taken from the plains biome
                                .waterFogColor(0x050533)
                                .skyColor(0)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeature, carver)
                                .build())
                        .build()
        );
    }

}
