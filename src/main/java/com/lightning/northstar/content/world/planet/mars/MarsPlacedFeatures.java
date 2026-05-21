package com.lightning.northstar.content.world.planet.mars;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.content.world.planet.core.NorthstarVegetationConfiguredFeatures;
import com.lightning.northstar.world.gen.OreHelper;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.heightproviders.BiasedToBottomHeight;
import net.minecraft.world.level.levelgen.heightproviders.VeryBiasedToBottomHeight;
import net.minecraft.world.level.levelgen.placement.*;

import static com.lightning.northstar.content.world.planet.core.NorthstarPlacedFeatures.register;

public class MarsPlacedFeatures {

    public static final ResourceKey<PlacedFeature>
            ARGYRE_TREE = key("argyre_tree"),
            ARGYRE_TREE_CEILING = key("argyre_tree_ceiling"),
            BLOB_ANDESITE = key("blob_andesite"),
            BLOB_BASALT = key("blob_basalt"),
            BLOB_CRIMSITE = key("blob_crimsite"),
            BLOB_CRIMSITE_LARGE = key("blob_crimsite_large"),
            BLOB_GRANITE = key("blob_granite"),
            BLOB_GRAVEL = key("blob_gravel"),
            BLOB_MAGMA = key("blob_magma"),
            BLOB_MARS_SOIL = key("blob_mars_soil"),
            BLOB_TUFF = key("blob_tuff"),
            BLOB_VOLCANIC_ASH = key("blob_volcanic_ash"),
            BLOB_VOLCANIC_ASH_LARGE = key("blob_volcanic_ash_large"),
            CRATER = key("crater"),
            CRATER_SOIL = key("crater_soil"),
            DUNES_ROCK = key("dunes_rock"),
            GRAVEL_PILE = key("gravel_pile"),
            LAVA_LAKE = key("lava_lake"),
            LAVA_SPRING = key("lava_spring"),
            ORE_COPPER = key("ore_copper"),
            ORE_COPPER_LARGE = key("ore_copper_large"),
            ORE_DIAMOND = key("ore_diamond"),
            ORE_GOLD = key("ore_gold"),
            ORE_IRON = key("ore_iron"),
            ORE_IRON_LARGE = key("ore_iron_large"),
            ORE_IRON_SMALL = key("ore_iron_small"),
            ORE_QUARTZ = key("ore_quartz"),
            ORE_REDSTONE = key("ore_redstone"),
            ORE_TITANIUM = key("ore_titanium"),
            ORE_ZINC = key("ore_zinc"),
            ROCK = key("rock"),
            ROOTS = key("roots"),
            WORM_NEST = key("worm_nest");

    private static ResourceKey<PlacedFeature> key(String path) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource("mars_" + path));
    }

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        OreHelper ores = new OreHelper(context, NorthstarBlockTags.MARS_STONE_REPLACEABLE, NorthstarBlockTags.MARS_DEEP_STONE_REPLACEABLE);

        ores.builder(ORE_COPPER)
                .sized(6, 0)
                .ores(NorthstarBlocks.MARS_COPPER_ORE, NorthstarBlocks.MARS_DEEP_COPPER_ORE)
                .trianglePlacement(18, VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112))
                .register();

        ores.builder(ORE_COPPER_LARGE)
                .sized(11, 0)
                .ores(NorthstarBlocks.MARS_COPPER_ORE, NorthstarBlocks.MARS_DEEP_COPPER_ORE)
                .trianglePlacement(16, VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112))
                .register();

        ores.builder(ORE_DIAMOND)
                .sized(5, 0)
                .ores(NorthstarBlocks.MARS_DIAMOND_ORE, NorthstarBlocks.MARS_DEEP_DIAMOND_ORE)
                .trianglePlacement(4, VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))
                .register();

        ores.builder(ORE_GOLD)
                .sized(5, 0)
                .ores(NorthstarBlocks.MARS_GOLD_ORE, NorthstarBlocks.MARS_DEEP_GOLD_ORE)
                .trianglePlacement(2, VerticalAnchor.absolute(-64), VerticalAnchor.absolute(48))
                .register();

        ores.builder(ORE_IRON)
                .sized(13, 0)
                .ores(NorthstarBlocks.MARS_IRON_ORE, NorthstarBlocks.MARS_DEEP_IRON_ORE)
                .placement(
                        CountPlacement.of(18),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.of(BiasedToBottomHeight.of(
                                VerticalAnchor.BOTTOM,
                                VerticalAnchor.aboveBottom(128),
                                1
                        )),
                        BiomeFilter.biome()
                )
                .register();

        ores.builder(ORE_IRON_LARGE)
                .sized(26, 0)
                .ores(NorthstarBlocks.MARS_IRON_ORE, NorthstarBlocks.MARS_DEEP_IRON_ORE)
                .placement(
                        CountPlacement.of(10),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.of(BiasedToBottomHeight.of(
                                VerticalAnchor.BOTTOM,
                                VerticalAnchor.aboveBottom(96),
                                1
                        )),
                        BiomeFilter.biome()
                )
                .register();

        ores.builder(ORE_IRON_SMALL)
                .sized(6, 0)
                .ores(NorthstarBlocks.MARS_IRON_ORE, NorthstarBlocks.MARS_DEEP_IRON_ORE)
                .placement(
                        CountPlacement.of(20),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.of(BiasedToBottomHeight.of(
                                VerticalAnchor.BOTTOM,
                                VerticalAnchor.absolute(320),
                                1
                        )),
                        BiomeFilter.biome()
                )
                .register();

        ores.builder(ORE_QUARTZ)
                .sized(9, 0)
                .ores(NorthstarBlocks.MARS_QUARTZ_ORE, NorthstarBlocks.MARS_DEEP_QUARTZ_ORE)
                .uniformPlacement(20, VerticalAnchor.absolute(-50), VerticalAnchor.absolute(50))
                .register();

        ores.builder(ORE_REDSTONE)
                .sized(10, 0)
                .ores(NorthstarBlocks.MARS_REDSTONE_ORE, NorthstarBlocks.MARS_DEEP_REDSTONE_ORE)
                .uniformPlacement(7, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(24))
                .register();

        ores.builder(ORE_TITANIUM)
                .sized(13, 0)
                .ores(NorthstarBlocks.MARS_TITANIUM_ORE, NorthstarBlocks.MARS_DEEP_TITANIUM_ORE)
                .placement(
                        CountPlacement.of(6),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.of(BiasedToBottomHeight.of(
                                VerticalAnchor.BOTTOM,
                                VerticalAnchor.aboveBottom(100),
                                1
                        )),
                        BiomeFilter.biome()
                )
                .register();

        ores.builder(ORE_ZINC)
                .sized(12, 0)
                .ores(NorthstarBlocks.MARS_ZINC_ORE, NorthstarBlocks.MARS_DEEP_ZINC_ORE)
                .uniformPlacement(8, VerticalAnchor.absolute(-63), VerticalAnchor.absolute(70))
                .register();

        OreHelper blobs = new OreHelper(context, NorthstarBlockTags.BASE_STONE_MARS, NorthstarBlockTags.BASE_STONE_MARS);

        blobs.builder(BLOB_ANDESITE)
                .blobSize()
                .ores(Blocks.ANDESITE, null)
                .uniformPlacement(1, VerticalAnchor.absolute(-10), VerticalAnchor.absolute(50))
                .register();

        blobs.builder(BLOB_BASALT)
                .blobSize()
                .ores(Blocks.BASALT, null)
                .uniformPlacement(4, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(8))
                .register();

        blobs.builder(BLOB_CRIMSITE)
                .blobSize()
                .ores(AllPaletteStoneTypes.CRIMSITE.baseBlock.get(), null)
                .placement(
                        RarityFilter.onAverageOnceEvery(2),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.of(BiasedToBottomHeight.of(
                                VerticalAnchor.absolute(-64),
                                VerticalAnchor.absolute(64),
                                1
                        )),
                        BiomeFilter.biome()
                )
                .register();

        blobs.builder(BLOB_CRIMSITE_LARGE)
                .blobSize()
                .ores(AllPaletteStoneTypes.CRIMSITE.baseBlock.get(), null)
                .placement(
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64)),
                        BiomeFilter.biome()
                )
                .register();

        blobs.builder(BLOB_GRANITE)
                .blobSize()
                .ores(Blocks.GRANITE, null)
                .uniformPlacement(1, VerticalAnchor.absolute(10), VerticalAnchor.absolute(60))
                .register();

        // FIXME: Weird placement, is that intended?
        blobs.builder(BLOB_GRAVEL)
                .blobSize()
                .ores(Blocks.GRAVEL, null)
                .placement(
                        RarityFilter.onAverageOnceEvery(64),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        BiomeFilter.biome()
                )
                .register();

        blobs.builder(BLOB_MAGMA)
                .blobSize()
                .ores(Blocks.MAGMA_BLOCK, null)
                .uniformPlacement(4, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(8))
                .register();

        blobs.builder(BLOB_MARS_SOIL)
                .blobSize()
                .ores(NorthstarBlocks.MARS_SOIL, null)
                .uniformPlacement(2, VerticalAnchor.absolute(20), VerticalAnchor.absolute(80))
                .register();

        blobs.builder(BLOB_TUFF)
                .blobSize()
                .ores(Blocks.TUFF, null)
                .uniformPlacement(1, VerticalAnchor.absolute(0), VerticalAnchor.absolute(64))
                .register();

        blobs.builder(BLOB_VOLCANIC_ASH)
                .blobSize()
                .ores(NorthstarBlocks.VOLCANIC_ASH, null)
                .uniformPlacement(2, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(0))
                .register();

        blobs.builder(BLOB_VOLCANIC_ASH_LARGE)
                .blobSize()
                .ores(NorthstarBlocks.VOLCANIC_ASH, null)
                .uniformPlacement(6, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(20))
                .register();

        register(
                context,
                ARGYRE_TREE,
                NorthstarVegetationConfiguredFeatures.ARGYRE,
                CountPlacement.of(8),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                EnvironmentScanPlacement.scanningFor(
                        Direction.DOWN,
                        BlockPredicate.solid(),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        32
                ),
                RandomOffsetPlacement.vertical(ConstantInt.of(1)),
                BiomeFilter.biome()
        );

        register(
                context,
                ARGYRE_TREE_CEILING,
                NorthstarVegetationConfiguredFeatures.ARGYRE_CEILING,
                CountPlacement.of(16),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                EnvironmentScanPlacement.scanningFor(
                        Direction.UP,
                        BlockPredicate.solid(),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        32
                ),
                RandomOffsetPlacement.vertical(ConstantInt.of(-1)),
                BiomeFilter.biome()
        );

        register(
                context,
                CRATER,
                MarsConfiguredFeatures.CRATER,
                RarityFilter.onAverageOnceEvery(64),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                BiomeFilter.biome()
        );

        register(
                context,
                CRATER_SOIL,
                MarsConfiguredFeatures.CRATER_SOIL,
                RarityFilter.onAverageOnceEvery(64),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                BiomeFilter.biome()
        );

        register(
                context,
                DUNES_ROCK,
                MarsConfiguredFeatures.DUNES_ROCK,
                RarityFilter.onAverageOnceEvery(6),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome()
        );

        register(
                context,
                GRAVEL_PILE,
                MarsConfiguredFeatures.GRAVEL_PILE,
                RarityFilter.onAverageOnceEvery(54),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                BiomeFilter.biome()
        );

        register(
                context,
                LAVA_LAKE,
                MarsConfiguredFeatures.LAVA_LAKE,
                RarityFilter.onAverageOnceEvery(6),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.TOP),
                EnvironmentScanPlacement.scanningFor(
                        Direction.DOWN,
                        BlockPredicate.allOf(
                                BlockPredicate.not(
                                        BlockPredicate.ONLY_IN_AIR_PREDICATE
                                ),
                                BlockPredicate.insideWorld(new Vec3i(0, -5, 0))
                        ),
                        32),
                SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5),
                BiomeFilter.biome()
        );

        register(
                context,
                LAVA_SPRING,
                MarsConfiguredFeatures.LAVA_SPRING,
                CountPlacement.of(100),
                InSquarePlacement.spread(),
                HeightRangePlacement.of(VeryBiasedToBottomHeight.of(
                        VerticalAnchor.BOTTOM,
                        VerticalAnchor.belowTop(8),
                        1
                )),
                BiomeFilter.biome()
        );

        register(
                context,
                ROCK,
                MarsConfiguredFeatures.ROCK,
                RarityFilter.onAverageOnceEvery(24),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                BiomeFilter.biome()
        );

        register(
                context,
                ROOTS,
                MarsConfiguredFeatures.ROOTS,
                CountPlacement.of(UniformInt.of(8, 24)),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(128)),
                InSquarePlacement.spread(),
                SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -13),
                BiomeFilter.biome()
        );

        register(
                context,
                WORM_NEST,
                MarsConfiguredFeatures.WORM_NEST,
                RarityFilter.onAverageOnceEvery(2),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(10)),
                EnvironmentScanPlacement.scanningFor(
                        Direction.DOWN,
                        BlockPredicate.solid(),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        32
                ),
                BiomeFilter.biome()
        );
    }

}
