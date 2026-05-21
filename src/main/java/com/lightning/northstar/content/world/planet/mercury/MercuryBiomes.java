package com.lightning.northstar.content.world.planet.mercury;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarEntityTypes;
import com.lightning.northstar.content.world.planet.core.NorthstarPlacedFeatures;
import com.lightning.northstar.content.world.planet.core.NorthstarVegetationPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class MercuryBiomes {

    public static final ResourceKey<Biome>
            BASINS = key("mercury_basins"),
            HILLS = key("mercury_hills"),
            ICY_CAVERNS = key("mercury_icy_caverns"),
            MAGMATIC_CAVERNS = key("mercury_magmatic_caverns");

    private static ResourceKey<Biome> key(String path) {
        return ResourceKey.create(Registries.BIOME, Northstar.asResource(path));
    }

    public static void bootstrap(BootstapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> worldCarvers = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(
                BASINS,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0)
                                .fogColor(0)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MercuryBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.RAW_GENERATION, MercuryPlacedFeatures.CRATER)
                                .addFeature(Decoration.RAW_GENERATION, MercuryPlacedFeatures.CRATER_BIG)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .apply(MercuryBiomes::commonBlobs)
                                .apply(MercuryBiomes::commonOres)
                                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.CALORIAN_VINES)
                                .addFeature(Decoration.VEGETAL_DECORATION, MercuryPlacedFeatures.CACTUS)
                                .addFeature(Decoration.VEGETAL_DECORATION, MercuryPlacedFeatures.SMALL_SHELVES)
                                .addFeature(Decoration.VEGETAL_DECORATION, MercuryPlacedFeatures.LARGE_SHELVES)
                                .build())
                        .build()
        );

        context.register(
                HILLS,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0)
                                .fogColor(0)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MercuryBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.RAW_GENERATION, MercuryPlacedFeatures.CRATER_SPARSE)
                                .addFeature(Decoration.RAW_GENERATION, MercuryPlacedFeatures.CRATER_BIG_SPARSE)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .apply(MercuryBiomes::commonBlobs)
                                .apply(MercuryBiomes::commonOres)
                                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.CALORIAN_VINES)
                                .addFeature(Decoration.VEGETAL_DECORATION, MercuryPlacedFeatures.CACTUS)
                                .addFeature(Decoration.VEGETAL_DECORATION, MercuryPlacedFeatures.SMALL_SHELVES)
                                .addFeature(Decoration.VEGETAL_DECORATION, MercuryPlacedFeatures.LARGE_SHELVES)
                                .build())
                        .build()
        );

        context.register(
                ICY_CAVERNS,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0)
                                .fogColor(0)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MercuryBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.RAW_GENERATION, MercuryPlacedFeatures.CRATER)
                                .addFeature(Decoration.RAW_GENERATION, MercuryPlacedFeatures.CRATER_BIG)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .apply(MercuryBiomes::commonBlobs)
                                .apply(MercuryBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.BLOB_PACKED_ICE)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.PACKED_ICE_CLUSTER)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.FROST)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.ICICLES)
                                .build())
                        .build()
        );

        context.register(
                MAGMATIC_CAVERNS,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0)
                                .fogColor(0)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MercuryBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.RAW_GENERATION, MercuryPlacedFeatures.CRATER)
                                .addFeature(Decoration.RAW_GENERATION, MercuryPlacedFeatures.CRATER_BIG)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .apply(MercuryBiomes::commonBlobs)
                                .apply(MercuryBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.MAGMA_CLUSTER)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.VOLCANIC_ASH_CLUSTER)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.VOLCANIC_ROCK_CLUSTER)
                                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.CALORIAN_VINES)
                                .build())
                        .build()
        );
    }

    private static void commonMonsters(MobSpawnSettings.Builder builder) {
        builder.addSpawn(
                MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(
                        NorthstarEntityTypes.MERCURY_RAPTOR.get(),
                        30,
                        1,
                        3
                )
        ).addSpawn(
                MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(
                        NorthstarEntityTypes.MERCURY_TORTOISE.get(),
                        30,
                        1,
                        3
                )
        ).addSpawn(
                MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(
                        NorthstarEntityTypes.MERCURY_ROACH.get(),
                        30,
                        2,
                        5
                )
        );
    }

    private static void commonBlobs(BiomeGenerationSettings.Builder builder) {

    }

    private static void commonOres(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.ORE_ZINC)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.ORE_TITANIUM)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.ORE_TUNGSTEN)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.ORE_TUNGSTEN_BURIED)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.ORE_TUNGSTEN_SMALL)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.ORE_IRON)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.ORE_DIAMOND)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.ORE_COPPER)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.ORE_GOLD)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.ORE_LAPIS)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.ORE_REDSTONE)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.ORE_REDSTONE_LARGE)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.BLOB_ANDESITE)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.BLOB_BLACKSTONE)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.BLOB_SCORCHIA)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.BLOB_SCORIA)
                .addFeature(Decoration.UNDERGROUND_ORES, MercuryPlacedFeatures.ORE_GLOWSTONE);
    }

}
