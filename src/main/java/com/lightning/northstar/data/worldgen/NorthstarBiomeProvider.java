package com.lightning.northstar.data.worldgen;

import com.lightning.northstar.content.NorthstarBiomes;
import com.lightning.northstar.content.NorthstarEntityTypes;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class NorthstarBiomeProvider {

    public static void bootstrap(BootstapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);

        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .skyColor(0)
                .fogColor(0)
                .waterColor(4159204)
                .waterFogColor(329011)
                .build();

        MobSpawnSettings spawns = new MobSpawnSettings.Builder()
                .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(NorthstarEntityTypes.MOON_LUNARGRADE.get(), 40, 1, 4))
                .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(NorthstarEntityTypes.MOON_SNAIL.get(), 20, 1, 3))
                .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(NorthstarEntityTypes.MOON_EEL.get(), 20, 2, 5))
                .build();

        BiomeGenerationSettings.Builder gen = new BiomeGenerationSettings.Builder(placedFeatures, carvers);

        // carvers
        gen.addCarver(GenerationStep.Carving.AIR, carvers.getOrThrow(Carvers.CAVE));
        gen.addCarver(GenerationStep.Carving.AIR, carvers.getOrThrow(Carvers.CAVE_EXTRA_UNDERGROUND));

        // features: step 1 = underground ores/blobs
        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatures.getOrThrow(PlanetOres.MOON_ORE_COPPER.placedFeature));
//        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatures.getOrThrow(NorthstarPlacedFeatures.MOON_ORE_TITANIUM));
//        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatures.getOrThrow(NorthstarPlacedFeatures.MOON_ORE_DIAMOND));
        // ... add the rest from your JSON list

        Biome biome = new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects)
                .mobSpawnSettings(spawns)
                .generationSettings(gen.build())
                .build();

        context.register(NorthstarBiomes.LUNAR_ASURINE_CAVES, biome);
    }
}
