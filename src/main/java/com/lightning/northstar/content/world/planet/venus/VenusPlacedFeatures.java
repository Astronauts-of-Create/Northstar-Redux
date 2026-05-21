package com.lightning.northstar.content.world.planet.venus;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.world.planet.core.NorthstarVegetationConfiguredFeatures;
import com.lightning.northstar.world.gen.OreHelper;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.heightproviders.VeryBiasedToBottomHeight;
import net.minecraft.world.level.levelgen.placement.*;

import static com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import static com.lightning.northstar.content.world.planet.core.NorthstarPlacedFeatures.register;

public class VenusPlacedFeatures {

    public static final ResourceKey<PlacedFeature>
            BASALT_PILLARS = key("basalt_pillars"),
            BASALT_PILLARS_LARGE = key("basalt_pillars_large"),
            BASALT_PILLARS_MANY = key("basalt_pillars_many"),
            BLOB_GRAVEL = key("blob_gravel"),
            BLOB_MAGMA = key("blob_magma"),
            BLOB_OCHRUM = key("blob_ochrum"),
            BLOB_SCORCHIA = key("blob_scorchia"),
            BLOB_SCORIA = key("blob_scoria"),
            BLOB_TUFF = key("blob_tuff"),
            GRAVEL_CLUSTER = key("gravel_cluster"),
            GRAVEL_SULFUR_CLUSTER = key("gravel_sulfur_cluster"),
            LAVA_LAKE = key("lava_lake"),
            LAVA_LAKE_SURFACE = key("lava_lake_surface"),
            LAVA_SPRING = key("lava_spring"),
            LAVA_SPRING_CEILING = key("lava_spring_ceiling"),
            ORE_COPPER = key("ore_copper"),
            ORE_DIAMOND = key("ore_diamond"),
            ORE_GLOWSTONE = key("ore_glowstone"),
            ORE_GOLD = key("ore_gold"),
            ORE_IRON = key("ore_iron"),
            ORE_QUARTZ = key("ore_quartz"),
            ORE_REDSTONE = key("ore_redstone"),
            ORE_TITANIUM = key("ore_titanium"),
            ORE_ZINC = key("ore_zinc"),
            PLUMES = key("plumes"),
            PLUMES_COMMON = key("plumes_common"),
            RIB_CAGES_SURFACE = key("rib_cages_surface"),
            SULFURIC_ACID_LAKE = key("sulfuric_acid_lake"),
            SULFURIC_ACID_LAKE_COMMON = key("sulfuric_acid_lake_common"),
            SULFURIC_ACID_LAKE_SURFACE = key("sulfuric_acid_lake_surface"),
            SULFURIC_ACID_SPRING_CEILING = key("sulfuric_acid_spring_ceiling"),
            SURFACE_FOSSIL = key("surface_fossil"),
            TALL_MYCELIUM = key("tall_mycelium"),
            TALL_MYCELIUM_ROOF = key("tall_mycelium_roof"),
            VINES = key("vines");

    private static ResourceKey<PlacedFeature> key(String path) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource("venus_" + path));
    }

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        OreHelper ores = new OreHelper(context, NorthstarBlockTags.VENUS_STONE_REPLACEABLE, NorthstarBlockTags.VENUS_DEEP_STONE_REPLACEABLE);

        ores.builder(ORE_COPPER)
                .sized(9, 0)
                .ores(NorthstarBlocks.VENUS_COPPER_ORE, NorthstarBlocks.VENUS_DEEP_COPPER_ORE)
                .trianglePlacement(6, VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112))
                .register();

        ores.builder(ORE_DIAMOND)
                .sized(6, 0.3f)
                .ores(NorthstarBlocks.VENUS_DIAMOND_ORE, NorthstarBlocks.VENUS_DEEP_DIAMOND_ORE)
                .trianglePlacement(7, VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))
                .register();

        ores.builder(ORE_GLOWSTONE)
                .sized(3, 0)
                .ores(NorthstarBlocks.VENUS_GLOWSTONE_ORE, NorthstarBlocks.VENUS_DEEP_GLOWSTONE_ORE)
                .trianglePlacement(5, VerticalAnchor.absolute(10), VerticalAnchor.absolute(60))
                .register();

        ores.builder(ORE_GOLD)
                .sized(5, 0)
                .ores(NorthstarBlocks.VENUS_GOLD_ORE, NorthstarBlocks.VENUS_DEEP_GOLD_ORE)
                .trianglePlacement(5, VerticalAnchor.absolute(-64), VerticalAnchor.absolute(48))
                .register();

        ores.builder(ORE_IRON)
                .sized(13, 0)
                .ores(NorthstarBlocks.VENUS_IRON_ORE, NorthstarBlocks.VENUS_DEEP_IRON_ORE)
                .uniformPlacement(10, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(72))
                .register();

        ores.builder(ORE_QUARTZ)
                .sized(12, 0)
                .ores(NorthstarBlocks.VENUS_QUARTZ_ORE, NorthstarBlocks.VENUS_DEEP_QUARTZ_ORE)
                .uniformPlacement(20, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(72))
                .register();

        ores.builder(ORE_REDSTONE)
                .sized(10, 0)
                .ores(NorthstarBlocks.VENUS_REDSTONE_ORE, NorthstarBlocks.VENUS_DEEP_REDSTONE_ORE)
                .uniformPlacement(4, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(15))
                .register();

        ores.builder(ORE_TITANIUM)
                .sized(15, 0)
                .ores(NorthstarBlocks.VENUS_TITANIUM_ORE, NorthstarBlocks.VENUS_DEEP_TITANIUM_ORE)
                .uniformPlacement(8, VerticalAnchor.absolute(-63), VerticalAnchor.absolute(60))
                .register();

        ores.builder(ORE_ZINC)
                .sized(12, 0)
                .ores(NorthstarBlocks.VENUS_ZINC_ORE, NorthstarBlocks.VENUS_DEEP_ZINC_ORE)
                .uniformPlacement(8, VerticalAnchor.absolute(-63), VerticalAnchor.absolute(70))
                .register();


        OreHelper blobs = new OreHelper(context, NorthstarBlockTags.BASE_STONE_VENUS, NorthstarBlockTags.BASE_STONE_VENUS);

        blobs.builder(BLOB_GRAVEL)
                .blobSize()
                .ores(NorthstarBlocks.VENUS_GRAVEL, null)
                .uniformPlacement(2, VerticalAnchor.absolute(20), VerticalAnchor.absolute(64))
                .register();

        blobs.builder(BLOB_MAGMA)
                .blobSize()
                .ores(Blocks.MAGMA_BLOCK, null)
                .uniformPlacement(6, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(8))
                .register();

        blobs.builder(BLOB_OCHRUM)
                .blobSize()
                .ores(AllPaletteStoneTypes.OCHRUM.baseBlock.get(), null)
                .uniformPlacement(2, VerticalAnchor.absolute(0), VerticalAnchor.absolute(32))
                .register();

        blobs.builder(BLOB_SCORCHIA)
                .blobSize()
                .ores(AllPaletteStoneTypes.SCORCHIA.baseBlock.get(), null)
                .uniformPlacement(2, VerticalAnchor.BOTTOM, VerticalAnchor.aboveBottom(64))
                .register();

        blobs.builder(BLOB_SCORIA)
                .blobSize()
                .ores(AllPaletteStoneTypes.SCORIA.baseBlock.get(), null)
                .uniformPlacement(2, VerticalAnchor.absolute(0), VerticalAnchor.absolute(32))
                .register();

        blobs.builder(BLOB_TUFF)
                .blobSize()
                .ores(Blocks.TUFF, null)
                .uniformPlacement(1, VerticalAnchor.absolute(-10), VerticalAnchor.absolute(32))
                .register();

        register(
                context,
                BASALT_PILLARS,
                VenusConfiguredFeatures.BASALT_PILLARS,
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                RarityFilter.onAverageOnceEvery(50),
                CountOnEveryLayerPlacement.of(2),
                BiomeFilter.biome()
        );

        register(
                context,
                BASALT_PILLARS_LARGE,
                VenusConfiguredFeatures.BASALT_PILLARS_LARGE,
                CountOnEveryLayerPlacement.of(2),
                BiomeFilter.biome()
        );

        register(
                context,
                BASALT_PILLARS_MANY,
                VenusConfiguredFeatures.BASALT_PILLARS,
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                RarityFilter.onAverageOnceEvery(12),
                CountOnEveryLayerPlacement.of(2),
                BiomeFilter.biome()
        );

        register(
                context,
                GRAVEL_CLUSTER,
                VenusConfiguredFeatures.GRAVEL_CLUSTER,
                CountPlacement.of(UniformInt.of(
                        16,
                        32
                )),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                BiomeFilter.biome()
        );

        register(
                context,
                GRAVEL_SULFUR_CLUSTER,
                VenusConfiguredFeatures.GRAVEL_SULFUR_CLUSTER,
                CountPlacement.of(UniformInt.of(
                        32,
                        64
                )),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(
                        VerticalAnchor.BOTTOM,
                        VerticalAnchor.absolute(256)
                ),
                BiomeFilter.biome()
        );

        EnvironmentScanPlacement lakePlacementScan = EnvironmentScanPlacement.scanningFor(
                Direction.DOWN,
                BlockPredicate.allOf(
                        BlockPredicate.not(
                                BlockPredicate.ONLY_IN_AIR_PREDICATE
                        ),
                        BlockPredicate.insideWorld(new Vec3i(0, -5, 0))
                ),
                32
        );

        register(
                context,
                LAVA_LAKE,
                VenusConfiguredFeatures.LAVA_LAKE,
                RarityFilter.onAverageOnceEvery(4),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(
                        VerticalAnchor.BOTTOM,
                        VerticalAnchor.TOP
                ),
                lakePlacementScan,
                SurfaceRelativeThresholdFilter.of(
                        Heightmap.Types.OCEAN_FLOOR_WG,
                        Integer.MIN_VALUE,
                        -5
                ),
                BiomeFilter.biome()
        );

        register(
                context,
                LAVA_LAKE_SURFACE,
                VenusConfiguredFeatures.LAVA_LAKE,
                RarityFilter.onAverageOnceEvery(20),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                BiomeFilter.biome()
        );

        register(
                context,
                LAVA_SPRING,
                VenusConfiguredFeatures.LAVA_SPRING,
                CountPlacement.of(150),
                InSquarePlacement.spread(),
                HeightRangePlacement.of(VeryBiasedToBottomHeight.of(
                        VerticalAnchor.BOTTOM,
                        VerticalAnchor.belowTop(8),
                        8
                )),
                BiomeFilter.biome()
        );

        register(
                context,
                LAVA_SPRING_CEILING,
                VenusConfiguredFeatures.LAVA_SPRING,
                CountPlacement.of(25),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(
                        VerticalAnchor.BOTTOM,
                        VerticalAnchor.absolute(128)
                ),
                EnvironmentScanPlacement.scanningFor(
                        Direction.UP,
                        BlockPredicate.solid(),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        12
                ),
                BiomeFilter.biome()
        );

        register(
                context,
                PLUMES,
                VenusConfiguredFeatures.PLUMES,
                RarityFilter.onAverageOnceEvery(160),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                BiomeFilter.biome()
        );

        register(
                context,
                PLUMES_COMMON,
                VenusConfiguredFeatures.PLUMES,
                RarityFilter.onAverageOnceEvery(90),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                BiomeFilter.biome()
        );

        register(
                context,
                RIB_CAGES_SURFACE,
                VenusConfiguredFeatures.RIB_CAGES,
                RarityFilter.onAverageOnceEvery(200),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                BiomeFilter.biome()
        );

        register(
                context,
                SULFURIC_ACID_LAKE,
                VenusConfiguredFeatures.SULFURIC_ACID_LAKE,
                RarityFilter.onAverageOnceEvery(20),
                InSquarePlacement.spread(),
                lakePlacementScan,
                SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5),
                BiomeFilter.biome()
        );

        register(
                context,
                SULFURIC_ACID_LAKE_COMMON,
                VenusConfiguredFeatures.SULFURIC_ACID_LAKE,
                RarityFilter.onAverageOnceEvery(2),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP),
                lakePlacementScan,
                SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5),
                BiomeFilter.biome()
        );

        register(
                context,
                SULFURIC_ACID_LAKE_SURFACE,
                VenusConfiguredFeatures.SULFURIC_ACID_LAKE,
                RarityFilter.onAverageOnceEvery(20),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                BiomeFilter.biome()
        );

        register(
                context,
                SULFURIC_ACID_SPRING_CEILING,
                VenusConfiguredFeatures.SULFURIC_ACID_SPRING,
                RarityFilter.onAverageOnceEvery(2),
                InSquarePlacement.spread(),
                EnvironmentScanPlacement.scanningFor(
                        Direction.UP,
                        BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE),
                        32
                ),
                BiomeFilter.biome()
        );

        register(
                context,
                SURFACE_FOSSIL,
                VenusConfiguredFeatures.RIB_CAGES,
                RarityFilter.onAverageOnceEvery(56),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                BiomeFilter.biome()
        );

        register(
                context,
                TALL_MYCELIUM,
                NorthstarVegetationConfiguredFeatures.TALL_VENUS_MYCELIUM,
                CountPlacement.of(32),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP),
                BiomeFilter.biome()
        );

        register(
                context,
                TALL_MYCELIUM_ROOF,
                NorthstarVegetationConfiguredFeatures.TALL_VENUS_MYCELIUM_ROOF,
                CountPlacement.of(32),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP),
                BiomeFilter.biome()
        );

        register(
                context,
                VINES,
                VenusConfiguredFeatures.VINES,
                CountPlacement.of(20),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(
                        VerticalAnchor.BOTTOM,
                        VerticalAnchor.absolute(128)
                )
        );
    }

}
