package com.lightning.northstar.data.worldgen.biomes;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBiomes;
import com.lightning.northstar.content.NorthstarEntityTypes;
import com.lightning.northstar.data.worldgen.PlanetOres;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class LunarBiomes {

    private static MobSpawnSettings spawns = new MobSpawnSettings.Builder()
            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(NorthstarEntityTypes.MOON_LUNARGRADE.get(), 40, 1, 4))
            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(NorthstarEntityTypes.MOON_SNAIL.get(), 20, 1, 3))
            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(NorthstarEntityTypes.MOON_EEL.get(), 20, 2, 5))
            .build();


    static BiomeSpecialEffects effects1 = new BiomeSpecialEffects.Builder()
            .skyColor(0)
            .fogColor(0)
            .waterColor(4159204)
            .waterFogColor(329011)
            .build();

    static BiomeSpecialEffects effects2 = new BiomeSpecialEffects.Builder()
            .skyColor(0)
            .fogColor(0)
            .waterColor(4159204)
            .waterFogColor(329011)
//            .ambientMoodSound(new AmbientMoodSettings(
//                    SoundEvents.AMETHYST_BLOCK_CHIME, // same as "block.amethyst_block.chime"
//                    6000, // tick_delay
//                    8,    // block_search_extent
//                    2.0   // offset
//            ))
            .ambientParticle(new AmbientParticleSettings(
                    new SimpleParticleType(true), // placeholder, replaced below
                    1.0F // probability
            ))
            .build();


    static private BiomeGenerationSettings.Builder generatorSettings(BootstapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);
        BiomeGenerationSettings.Builder gen = new BiomeGenerationSettings.Builder(placedFeatures, carvers);

        // carvers
        gen.addCarver(GenerationStep.Carving.AIR, carvers.getOrThrow(Carvers.CAVE));
        gen.addCarver(GenerationStep.Carving.AIR, carvers.getOrThrow(Carvers.CAVE_EXTRA_UNDERGROUND));

        // features
        gen.addFeature(GenerationStep.Decoration.RAW_GENERATION, placedFeatures.getOrThrow(
                ResourceKey.create(Registries.PLACED_FEATURE,Northstar.asResource("rare_lava_lake")
                )
        ));
        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatures.getOrThrow(PlanetOres.MOON_ORE_COPPER.placedFeature));
//        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatures.getOrThrow(NorthstarPlacedFeatures.MOON_ORE_TITANIUM));
//        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatures.getOrThrow(NorthstarPlacedFeatures.MOON_ORE_DIAMOND));
        // ... add the rest from your JSON list
        return gen;
    }

    public static void boostrap(BootstapContext<Biome> context) {
        context.register(NorthstarBiomes.LUNAR_ASURINE_CAVES, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.LUNAR_COOLED_LAVA_CAVE, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.LUNAR_CRATER_FIELDS, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.LUNAR_GLOWSTONE_CAVERN, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.LUNAR_HILLS, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.LUNAR_ICE_CAVES, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects1)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());

        context.register(NorthstarBiomes.LUNAR_PLAINS, new Biome.BiomeBuilder()
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(false)
                .specialEffects(effects2)
                .mobSpawnSettings(spawns)
                .generationSettings(generatorSettings(context).build())
                .build());
    }
}
