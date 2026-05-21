package com.lightning.northstar.content.world.planet.mars;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarEntityTypes;
import com.lightning.northstar.content.world.planet.core.NorthstarPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class MarsBiomes {

    public static final ResourceKey<Biome>
            CRIMSITE_CAVERNS = key("martian_crimsite_caverns"),
            DUNES = key("martian_dunes"),
            HIGHLANDS = key("martian_highlands"),
            MAGMATIC_CAVES = key("martian_magmatic_caves"),
            OVERGROWN_CAVERNS = key("martian_overgrown_caverns"),
            PEAKS = key("martian_peaks");

    private static ResourceKey<Biome> key(String path) {
        return ResourceKey.create(Registries.BIOME, Northstar.asResource(path));
    }

    public static void bootstrap(BootstapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> worldCarvers = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(
                CRIMSITE_CAVERNS,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0xdaae9c)
                                .fogColor(0xe8e9d6)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MarsBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .addFeature(Decoration.UNDERGROUND_STRUCTURES, MarsPlacedFeatures.WORM_NEST)
                                .apply(MarsBiomes::commonBlobs)
                                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_VOLCANIC_ASH)
                                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_TUFF)
                                .apply(MarsBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_CRIMSITE_LARGE)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.CRIMSITE_CLUSTER)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.CRIMSITE_COLUMN)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.POINTED_CRIMSITE)
                                .addFeature(Decoration.VEGETAL_DECORATION, MarsPlacedFeatures.ROOTS)
                                .build())
                        .build()
        );

        context.register(
                DUNES,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0xdaae9c)
                                .fogColor(0xffdbbf)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MarsBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.RAW_GENERATION, MarsPlacedFeatures.CRATER)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .apply(MarsBiomes::commonBlobs)
                                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_VOLCANIC_ASH)
                                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_GRAVEL)
                                .apply(MarsBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.BIG_DUMB_ROCK)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, MarsPlacedFeatures.ROCK)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, MarsPlacedFeatures.WORM_NEST)
                                .addFeature(Decoration.VEGETAL_DECORATION, MarsPlacedFeatures.ROOTS)
                                .build())
                        .build()
        );

        context.register(
                HIGHLANDS,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0xdaae9c)
                                .fogColor(0xe8e9d6)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MarsBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.RAW_GENERATION, MarsPlacedFeatures.CRATER_SOIL)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .apply(MarsBiomes::commonBlobs)
                                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_VOLCANIC_ASH)
                                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_TUFF)
                                .apply(MarsBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.BIG_DUMB_ROCK)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, MarsPlacedFeatures.WORM_NEST)
                                .addFeature(Decoration.VEGETAL_DECORATION, MarsPlacedFeatures.ROOTS)
                                .build())
                        .build()
        );

        context.register(
                MAGMATIC_CAVES,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0xdaae9c)
                                .fogColor(0xe8e9d6)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .ambientParticle(new AmbientParticleSettings(
                                        ParticleTypes.WHITE_ASH,
                                        0.025f
                                ))
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MarsBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .apply(MarsBiomes::commonBlobs)
                                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_VOLCANIC_ASH_LARGE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_TUFF)
                                .apply(MarsBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_MAGMA)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, MarsPlacedFeatures.LAVA_SPRING)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, MarsPlacedFeatures.LAVA_LAKE)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.MAGMA_CLUSTER)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.VOLCANIC_ASH_CLUSTER)
                                .build())
                        .build()
        );

        context.register(
                OVERGROWN_CAVERNS,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0xdaae9c)
                                .fogColor(0xe8e9d6)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MarsBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .apply(MarsBiomes::commonBlobs)
                                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_VOLCANIC_ASH)
                                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_TUFF)
                                .apply(MarsBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, MarsPlacedFeatures.WORM_NEST)
                                .addFeature(Decoration.VEGETAL_DECORATION, MarsPlacedFeatures.ARGYRE_TREE)
                                .addFeature(Decoration.VEGETAL_DECORATION, MarsPlacedFeatures.ARGYRE_TREE_CEILING)
                                .addFeature(Decoration.VEGETAL_DECORATION, MarsPlacedFeatures.ROOTS)
                                .build())
                        .build()
        );

        context.register(
                PEAKS,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(true)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0xdaae9c)
                                .fogColor(0xe9e3d6)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MarsBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .apply(MarsBiomes::commonBlobs)
                                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_VOLCANIC_ASH)
                                .apply(MarsBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, MarsPlacedFeatures.WORM_NEST)
                                .addFeature(Decoration.VEGETAL_DECORATION, MarsPlacedFeatures.ROOTS)
                                .build())
                        .build()
        );
    }

    private static void commonMonsters(MobSpawnSettings.Builder builder) {
        builder.addSpawn(
                        MobCategory.MONSTER,
                        new MobSpawnSettings.SpawnerData(
                                NorthstarEntityTypes.MARS_WORM.get(),
                                40,
                                1,
                                4
                        )
                )
                .addSpawn(
                        MobCategory.MONSTER,
                        new MobSpawnSettings.SpawnerData(
                                NorthstarEntityTypes.MARS_COBRA.get(),
                                20,
                                1,
                                3
                        )
                )
                .addSpawn(
                        MobCategory.MONSTER,
                        new MobSpawnSettings.SpawnerData(
                                NorthstarEntityTypes.MARS_TOAD.get(),
                                20,
                                3,
                                6
                        )
                )
                .addSpawn(
                        MobCategory.MONSTER,
                        new MobSpawnSettings.SpawnerData(
                                NorthstarEntityTypes.MARS_MOTH.get(),
                                20,
                                1,
                                3
                        )
                );
    }

    private static void commonBlobs(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_ANDESITE)
                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_GRANITE)
                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_BASALT)
                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_CRIMSITE)
                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.BLOB_MARS_SOIL);
    }

    private static void commonOres(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.ORE_TITANIUM)
                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.ORE_COPPER_LARGE)
                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.ORE_COPPER)
                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.ORE_ZINC)
                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.ORE_IRON_LARGE)
                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.ORE_IRON)
                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.ORE_IRON_SMALL)
                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.ORE_DIAMOND)
                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.ORE_GOLD)
                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.ORE_QUARTZ)
                .addFeature(Decoration.UNDERGROUND_ORES, MarsPlacedFeatures.ORE_REDSTONE);
    }

}
