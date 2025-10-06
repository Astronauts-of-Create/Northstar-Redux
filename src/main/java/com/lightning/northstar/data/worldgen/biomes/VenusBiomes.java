package com.lightning.northstar.data.worldgen.biomes;

import com.lightning.northstar.content.NorthstarBiomes;
import com.lightning.northstar.content.NorthstarEntityTypes;
import com.lightning.northstar.data.worldgen.PlanetOres;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VenusBiomes {

    private static MobSpawnSettings spawns = new MobSpawnSettings.Builder()
            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(NorthstarEntityTypes.VENUS_MIMIC.get(), 40, 1, 4))
            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(NorthstarEntityTypes.VENUS_SCORPION.get(), 20, 1, 3))
            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(NorthstarEntityTypes.VENUS_VULTURE.get(), 20, 2, 5))
            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(NorthstarEntityTypes.VENUS_STONE_BULL.get(), 20, 2, 5))
            .build();


    BiomeSpecialEffects effects1 = new BiomeSpecialEffects.Builder()
            .skyColor(14543991)
            .fogColor(14538752)
            .waterColor(4159204)
            .waterFogColor(329011)
            .build();

    private BiomeGenerationSettings.Builder generatorSettings(BootstapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);
        BiomeGenerationSettings.Builder gen = new BiomeGenerationSettings.Builder(placedFeatures, carvers);

        // carvers
        gen.addCarver(GenerationStep.Carving.AIR, carvers.getOrThrow(Carvers.CAVE));
        gen.addCarver(GenerationStep.Carving.AIR, carvers.getOrThrow(Carvers.CAVE_EXTRA_UNDERGROUND));

        // features: step 1 = underground ores/blobs
        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatures.getOrThrow(PlanetOres.MOON_ORE_COPPER.placedFeature));
//        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatures.getOrThrow(NorthstarPlacedFeatures.MOON_ORE_TITANIUM));
//        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatures.getOrThrow(NorthstarPlacedFeatures.MOON_ORE_DIAMOND));
        // ... add the rest from your JSON list
        return gen;
    }

    public static void boostrap(BootstapContext<Biome> context) {
        context.register(NorthstarBiomes.VENUS_FUNGAL_FOREST, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.VENUS_FUNGAL_CAVES, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.VENUS_SULFURIC_CAVERNS, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.VENUS_LAVA_CAVES, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.VENUSIAN_PLAINS, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.VENUSIAN_WASTES, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());
    }
}
