package com.lightning.northstar.content.world.planet.venus;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarEntityTypes;
import com.lightning.northstar.content.world.planet.core.NorthstarPlacedFeatures;
import com.lightning.northstar.content.world.planet.core.NorthstarVegetationPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VenusBiomes {

    // TODO: Fungal forest is unused
    // FIXME: Name inconsistency (venus/venusian)
    public static final ResourceKey<Biome>
            FUNGAL_CAVERNS = key("venus_fungal_caverns"),
            FUNGAL_FOREST = key("venus_fungal_forest"),
            LAVA_CAVES = key("venus_lava_caves"),
            SULFURIC_CAVERNS = key("venus_sulfuric_caverns"),
            PLAINS = key("venusian_plains"),
            WASTES = key("venusian_wastes");

    private static ResourceKey<Biome> key(String path) {
        return ResourceKey.create(Registries.BIOME, Northstar.asResource(path));
    }

    public static void bootstrap(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> worldCarvers = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(
                FUNGAL_CAVERNS,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0xf1f5a9)
                                .fogColor(0xddd800)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(VenusBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .addFeature(Decoration.LAKES, VenusPlacedFeatures.LAVA_LAKE)
                                .apply(VenusBiomes::commonOres)
                                .apply(VenusBiomes::commonFungus)
                                .addFeature(Decoration.VEGETAL_DECORATION, VenusPlacedFeatures.TALL_MYCELIUM)
                                .addFeature(Decoration.VEGETAL_DECORATION, VenusPlacedFeatures.TALL_MYCELIUM_ROOF)
                                .addFeature(Decoration.VEGETAL_DECORATION, VenusPlacedFeatures.VINES)
                                .build())
                        .build()
        );

        context.register(
                FUNGAL_FOREST,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0xf1f5a9)
                                .fogColor(0xddd800)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .addFeature(Decoration.LAKES, VenusPlacedFeatures.LAVA_LAKE)
                                .apply(VenusBiomes::commonOres)
                                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.SPIKE_FUNGUS)
                                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.BLOOM_FUNGUS)
                                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.HUGE_SPIKE_FUNGUS_SURFACE)
                                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.HUGE_SPIKE_FUNGUS)
                                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.HUGE_BLOOM_FUNGUS_SURFACE)
                                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.HUGE_TOWER_FUNGUS)
                                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.HUGE_PLATE_FUNGUS)
                                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.TOWER_FUNGUS)
                                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.PLATE_FUNGUS)
                                .build())
                        .build()
        );

        context.register(
                LAVA_CAVES,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0xf1f5a9)
                                .fogColor(0xddd800)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .ambientParticle(new AmbientParticleSettings(
                                        ParticleTypes.WHITE_ASH,
                                        0.025f
                                ))
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .addFeature(Decoration.LAKES, VenusPlacedFeatures.LAVA_LAKE)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, VenusPlacedFeatures.BASALT_PILLARS_MANY)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.MAGMA_CLUSTER)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.VOLCANIC_ASH_CLUSTER)
                                .apply(VenusBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_ORES, VenusPlacedFeatures.BLOB_MAGMA)
                                .addFeature(Decoration.FLUID_SPRINGS, VenusPlacedFeatures.LAVA_SPRING)
                                .addFeature(Decoration.FLUID_SPRINGS, VenusPlacedFeatures.LAVA_SPRING_CEILING)
                                .build())
                        .build()
        );

        context.register(
                SULFURIC_CAVERNS,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0xf1f5a9)
                                .fogColor(0xddd800)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .addSpawn(
                                        MobCategory.MONSTER,
                                        new MobSpawnSettings.SpawnerData(
                                                NorthstarEntityTypes.VENUS_SCORPION.get(),
                                                50,
                                                1,
                                                4
                                        )
                                )
                                .addSpawn(
                                        MobCategory.MONSTER,
                                        new MobSpawnSettings.SpawnerData(
                                                NorthstarEntityTypes.VENUS_VULTURE.get(),
                                                10,
                                                1,
                                                3
                                        )
                                )
                                .addSpawn(
                                        MobCategory.MONSTER,
                                        new MobSpawnSettings.SpawnerData(
                                                NorthstarEntityTypes.VENUS_STONE_BULL.get(),
                                                20,
                                                2,
                                                6
                                        )
                                )
                                .addSpawn(
                                        MobCategory.MONSTER,
                                        new MobSpawnSettings.SpawnerData(
                                                NorthstarEntityTypes.VENUS_MIMIC.get(),
                                                40,
                                                1,
                                                3
                                        )
                                )
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .addFeature(Decoration.LAKES, VenusPlacedFeatures.LAVA_LAKE)
                                .addFeature(Decoration.LAKES, VenusPlacedFeatures.SULFURIC_ACID_LAKE_COMMON)
                                .apply(VenusBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, VenusPlacedFeatures.BASALT_PILLARS_MANY)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, VenusPlacedFeatures.GRAVEL_SULFUR_CLUSTER)
                                .addFeature(Decoration.FLUID_SPRINGS, VenusPlacedFeatures.SULFURIC_ACID_SPRING_CEILING)
                                .addFeature(Decoration.VEGETAL_DECORATION, VenusPlacedFeatures.VINES)
                                .build())
                        .build()
        );

        context.register(
                PLAINS,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0xddec77)
                                .fogColor(0xddd800)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(VenusBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .addFeature(Decoration.LAKES, VenusPlacedFeatures.LAVA_LAKE_SURFACE)
                                .addFeature(Decoration.LAKES, VenusPlacedFeatures.SULFURIC_ACID_LAKE)
                                .addFeature(Decoration.LAKES, VenusPlacedFeatures.SULFURIC_ACID_LAKE_SURFACE)
                                .addFeature(Decoration.SURFACE_STRUCTURES, VenusPlacedFeatures.SURFACE_FOSSIL)
                                .addFeature(Decoration.SURFACE_STRUCTURES, VenusPlacedFeatures.PLUMES)
                                .addFeature(Decoration.SURFACE_STRUCTURES, VenusPlacedFeatures.RIB_CAGES_SURFACE)
                                .apply(VenusBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_ORES, VenusPlacedFeatures.BLOB_GRAVEL)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, VenusPlacedFeatures.GRAVEL_CLUSTER)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, VenusPlacedFeatures.BASALT_PILLARS)
                                .build())
                        .build()
        );

        context.register(
                WASTES,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0xf1f5a9)
                                .fogColor(0xddd800)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                                .addFeature(Decoration.LAKES, VenusPlacedFeatures.LAVA_LAKE)
                                .addFeature(Decoration.LAKES, VenusPlacedFeatures.SULFURIC_ACID_LAKE)
                                .addFeature(Decoration.LAKES, VenusPlacedFeatures.SULFURIC_ACID_LAKE_SURFACE)
                                .addFeature(Decoration.UNDERGROUND_STRUCTURES, VenusPlacedFeatures.PLUMES_COMMON)
                                .apply(VenusBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_ORES, VenusPlacedFeatures.BLOB_GRAVEL)
                                .addFeature(Decoration.UNDERGROUND_ORES, NorthstarPlacedFeatures.VOLCANIC_ROCK_SURFACE_CLUSTER)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, VenusPlacedFeatures.BASALT_PILLARS_MANY)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.MAGMA_CLUSTER)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.VOLCANIC_ASH_CLUSTER)
                                .build())
                        .build()
        );
    }

    private static void commonMonsters(MobSpawnSettings.Builder builder) {
        builder.addSpawn(
                        MobCategory.MONSTER,
                        new MobSpawnSettings.SpawnerData(
                                NorthstarEntityTypes.VENUS_SCORPION.get(),
                                30,
                                1,
                                4
                        )
                )
                .addSpawn(
                        MobCategory.MONSTER,
                        new MobSpawnSettings.SpawnerData(
                                NorthstarEntityTypes.VENUS_VULTURE.get(),
                                30,
                                1,
                                3
                        )
                )
                .addSpawn(
                        MobCategory.MONSTER,
                        new MobSpawnSettings.SpawnerData(
                                NorthstarEntityTypes.VENUS_STONE_BULL.get(),
                                30,
                                2,
                                6
                        )
                )
                .addSpawn(
                        MobCategory.MONSTER,
                        new MobSpawnSettings.SpawnerData(
                                NorthstarEntityTypes.VENUS_MIMIC.get(),
                                40,
                                1,
                                3
                        )
                );
    }

    private static void commonOres(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(Decoration.UNDERGROUND_ORES, VenusPlacedFeatures.ORE_COPPER)
                .addFeature(Decoration.UNDERGROUND_ORES, VenusPlacedFeatures.ORE_ZINC)
                .addFeature(Decoration.UNDERGROUND_ORES, VenusPlacedFeatures.ORE_DIAMOND)
                .addFeature(Decoration.UNDERGROUND_ORES, VenusPlacedFeatures.ORE_GLOWSTONE)
                .addFeature(Decoration.UNDERGROUND_ORES, VenusPlacedFeatures.ORE_GOLD)
                .addFeature(Decoration.UNDERGROUND_ORES, VenusPlacedFeatures.ORE_TITANIUM)
                .addFeature(Decoration.UNDERGROUND_ORES, VenusPlacedFeatures.ORE_IRON)
                .addFeature(Decoration.UNDERGROUND_ORES, VenusPlacedFeatures.ORE_QUARTZ)
                .addFeature(Decoration.UNDERGROUND_ORES, VenusPlacedFeatures.ORE_REDSTONE)
                .addFeature(Decoration.UNDERGROUND_ORES, VenusPlacedFeatures.BLOB_SCORIA)
                .addFeature(Decoration.UNDERGROUND_ORES, VenusPlacedFeatures.BLOB_SCORCHIA)
                .addFeature(Decoration.UNDERGROUND_ORES, VenusPlacedFeatures.BLOB_TUFF);
    }

    private static void commonFungus(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.SPIKE_FUNGUS)
                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.SPIKE_FUNGUS_ROOF)
                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.BLOOM_FUNGUS)
                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.BLOOM_FUNGUS_ROOF)
                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.HUGE_SPIKE_FUNGUS)
                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.HUGE_BLOOM_FUNGUS)
                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.HUGE_BLOOM_FUNGUS_ROOF)
                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.HUGE_TOWER_FUNGUS)
                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.HUGE_TOWER_FUNGUS_ROOF)
                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.HUGE_PLATE_FUNGUS)
                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.HUGE_PLATE_FUNGUS_ROOF)
                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.TOWER_FUNGUS)
                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.TOWER_FUNGUS_ROOF)
                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.PLATE_FUNGUS)
                .addFeature(Decoration.VEGETAL_DECORATION, NorthstarVegetationPlacedFeatures.PLATE_FUNGUS_ROOF);
    }

}
