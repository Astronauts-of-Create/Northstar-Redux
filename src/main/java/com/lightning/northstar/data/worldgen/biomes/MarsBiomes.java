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

public class MarsBiomes {

    private static MobSpawnSettings spawns = new MobSpawnSettings.Builder()
            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(NorthstarEntityTypes.MARS_WORM.get(), 40, 1, 4))
            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(NorthstarEntityTypes.MARS_COBRA.get(), 20, 1, 3))
            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(NorthstarEntityTypes.MARS_TOAD.get(), 20, 3, 6))
            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(NorthstarEntityTypes.MARS_MOTH.get(), 20, 1, 3))
            .build();


    static  BiomeSpecialEffects effects1 = new BiomeSpecialEffects.Builder()
            .skyColor(14331548)
            .fogColor(15264214)
            .waterColor(4159204)
            .waterFogColor(329011)
            .build();

    static  BiomeSpecialEffects effects2 = new BiomeSpecialEffects.Builder()
            .skyColor(14331548)
            .fogColor(15264214)
            .waterColor(4159204)
            .waterFogColor(329011)
//            .ambientParticle(new AmbientParticleSettings(
//                    new SimpleParticleType(true), // placeholder, replaced below
//                    0.025F // probability
//            ))
            .build();

    /*
    *     "particle": {
      "options": {
        "type": "minecraft:white_ash"
      },
      "probability": 0.025
    }*/


    static  private BiomeGenerationSettings.Builder generatorSettings(BootstapContext<Biome> context) {
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
        context.register(NorthstarBiomes.MARTIAN_CRIMSITE_CAVERNS, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1)
                .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.MARTIAN_DUNES, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1) .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.MARTIAN_HIGH_LANDS, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1) .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.MARTIAN_MAGMATIC_CAVERNS, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects2) .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.MARTIAN_OVERGROWN_CAVERNS, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.MARTIAN_PEAKS, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1) .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());
    }
}
