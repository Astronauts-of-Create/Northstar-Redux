package com.lightning.northstar.content.world.planet.mercury;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.world.gen.OreHelper;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.*;

import static com.lightning.northstar.content.world.planet.core.NorthstarPlacedFeatures.register;

public class MercuryPlacedFeatures {

    public static final ResourceKey<PlacedFeature>
            BLOB_ANDESITE = key("blob_andesite"),
            BLOB_BLACKSTONE = key("blob_blackstone"),
            BLOB_BLUE_ICE = key("blob_blue_ice"),
            BLOB_PACKED_ICE = key("blob_packed_ice"),
            BLOB_SCORCHIA = key("blob_scorchia"),
            BLOB_SCORIA = key("blob_scoria"),
            CACTUS = key("cactus"),
            CRATER = key("crater"),
            CRATER_BIG = key("crater_big"),
            CRATER_BIG_SPARSE = key("crater_big_sparse"),
            CRATER_SPARSE = key("crater_sparse"),
            LARGE_SHELVES = key("large_shelves"),
            ORE_COPPER = key("ore_copper"),
            ORE_DIAMOND = key("ore_diamond"),
            ORE_GLOWSTONE = key("ore_glowstone"),
            ORE_GOLD = key("ore_gold"),
            ORE_IRON = key("ore_iron"),
            ORE_LAPIS = key("ore_lapis"),
            ORE_REDSTONE = key("ore_redstone"),
            ORE_REDSTONE_LARGE = key("ore_redstone_large"),
            ORE_TITANIUM = key("ore_titanium"),
            ORE_TUNGSTEN = key("ore_tungsten"),
            ORE_TUNGSTEN_BURIED = key("ore_tungsten_buried"),
            ORE_TUNGSTEN_SMALL = key("ore_tungsten_small"),
            ORE_ZINC = key("ore_zinc"),
            SMALL_SHELVES = key("small_shelves");

    private static ResourceKey<PlacedFeature> key(String path) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource("mercury_" + path));
    }

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        OreHelper ores = new OreHelper(context, NorthstarBlockTags.MERCURY_STONE_REPLACEABLE, NorthstarBlockTags.MERCURY_DEEP_STONE_REPLACEABLE);
        ores.builder(ORE_COPPER)
                .sized(10, 0)
                .ores(NorthstarBlocks.MERCURY_COPPER_ORE, NorthstarBlocks.MERCURY_DEEP_COPPER_ORE)
                .trianglePlacement(7, VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112))
                .register();

        ores.builder(ORE_DIAMOND)
                .sized(8, 0.6f)
                .ores(NorthstarBlocks.MERCURY_DIAMOND_ORE, NorthstarBlocks.MERCURY_DEEP_DIAMOND_ORE)
                .trianglePlacement(6, VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))
                .register();

        ores.builder(ORE_GLOWSTONE)
                .sized(10, 0)
                .ores(NorthstarBlocks.MERCURY_GLOWSTONE_ORE, NorthstarBlocks.MERCURY_DEEP_GLOWSTONE_ORE)
                .trianglePlacement(10, VerticalAnchor.absolute(0), VerticalAnchor.absolute(40))
                .register();

        ores.builder(ORE_GOLD)
                .sized(7, 0)
                .ores(NorthstarBlocks.MERCURY_GOLD_ORE, NorthstarBlocks.MERCURY_DEEP_GOLD_ORE)
                .trianglePlacement(8, VerticalAnchor.absolute(-64), VerticalAnchor.absolute(48))
                .register();

        ores.builder(ORE_IRON)
                .sized(6, 0)
                .ores(NorthstarBlocks.MERCURY_IRON_ORE, NorthstarBlocks.MERCURY_DEEP_IRON_ORE)
                .uniformPlacement(9, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(72))
                .register();

        ores.builder(ORE_LAPIS)
                .sized(14, 0)
                .ores(NorthstarBlocks.MERCURY_LAPIS_ORE, NorthstarBlocks.MERCURY_DEEP_LAPIS_ORE)
                .uniformPlacement(12, VerticalAnchor.absolute(-50), VerticalAnchor.absolute(40))
                .register();

        ores.builder(ORE_REDSTONE)
                .sized(5, 0)
                .ores(NorthstarBlocks.MERCURY_REDSTONE_ORE, NorthstarBlocks.MERCURY_DEEP_REDSTONE_ORE)
                .uniformPlacement(7, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(24))
                .register();

        ores.builder(ORE_REDSTONE_LARGE)
                .sized(15, 0)
                .ores(NorthstarBlocks.MERCURY_REDSTONE_ORE, NorthstarBlocks.MERCURY_DEEP_REDSTONE_ORE)
                .uniformPlacement(4, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(24))
                .register();

        ores.builder(ORE_TITANIUM)
                .sized(10, 0.1f)
                .ores(NorthstarBlocks.MERCURY_TITANIUM_ORE, NorthstarBlocks.MERCURY_DEEP_TITANIUM_ORE)
                .uniformPlacement(3, VerticalAnchor.absolute(-10), VerticalAnchor.absolute(10))
                .register();

        ores.builder(ORE_TUNGSTEN)
                .sized(9, 0)
                .ores(NorthstarBlocks.MERCURY_TITANIUM_ORE, NorthstarBlocks.MERCURY_DEEP_TITANIUM_ORE)
                .trianglePlacement(50, VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(60))
                .register();

        ores.builder(ORE_TUNGSTEN_BURIED)
                .sized(12, 0.7f)
                .ores(NorthstarBlocks.MERCURY_TITANIUM_ORE, NorthstarBlocks.MERCURY_DEEP_TITANIUM_ORE)
                .trianglePlacement(50, VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(50))
                .register();

        ores.builder(ORE_TUNGSTEN_SMALL)
                .sized(4, 0)
                .ores(NorthstarBlocks.MERCURY_TITANIUM_ORE, NorthstarBlocks.MERCURY_DEEP_TITANIUM_ORE)
                .trianglePlacement(10, VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(110))
                .register();

        ores.builder(ORE_ZINC)
                .sized(12, 0)
                .ores(NorthstarBlocks.MERCURY_ZINC_ORE, NorthstarBlocks.MERCURY_DEEP_ZINC_ORE)
                .uniformPlacement(8, VerticalAnchor.absolute(-63), VerticalAnchor.absolute(70))
                .register();


        OreHelper blobs = new OreHelper(context, NorthstarBlockTags.BASE_STONE_MERCURY, NorthstarBlockTags.BASE_STONE_MERCURY);

        blobs.builder(BLOB_ANDESITE)
                .blobSize()
                .ores(Blocks.ANDESITE, null)
                .uniformPlacement(1, VerticalAnchor.absolute(0), VerticalAnchor.absolute(72))
                .register();

        blobs.builder(BLOB_BLACKSTONE)
                .blobSize()
                .ores(Blocks.BLACKSTONE, null)
                .uniformPlacement(1, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(8))
                .register();

        blobs.builder(BLOB_BLUE_ICE)
                .blobSize()
                .ores(Blocks.BLUE_ICE, null)
                .placement(
                        RarityFilter.onAverageOnceEvery(6),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.BOTTOM,
                                VerticalAnchor.absolute(8)
                        ),
                        BiomeFilter.biome()
                )
                .register();

        blobs.builder(BLOB_PACKED_ICE)
                .blobSize()
                .ores(Blocks.PACKED_ICE, null)
                .uniformPlacement(2, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(8))
                .register();

        blobs.builder(BLOB_SCORCHIA)
                .blobSize()
                .ores(AllPaletteStoneTypes.SCORCHIA.baseBlock.get(), null)
                .uniformPlacement(1, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(8))
                .register();

        blobs.builder(BLOB_SCORIA)
                .blobSize()
                .ores(AllPaletteStoneTypes.SCORIA.baseBlock.get(), null)
                .uniformPlacement(1, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(24))
                .register();

        register(
                context,
                CACTUS,
                MercuryConfiguredFeatures.CACTUS,
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(
                        VerticalAnchor.BOTTOM,
                        VerticalAnchor.aboveBottom(128)
                ),
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
                CRATER,
                MercuryConfiguredFeatures.CRATER,
                RarityFilter.onAverageOnceEvery(6),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                BiomeFilter.biome()
        );

        register(
                context,
                CRATER_BIG,
                MercuryConfiguredFeatures.CRATER_BIG,
                RarityFilter.onAverageOnceEvery(10),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                BiomeFilter.biome()
        );

        register(
                context,
                CRATER_BIG_SPARSE,
                MercuryConfiguredFeatures.CRATER_BIG,
                RarityFilter.onAverageOnceEvery(20),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                BiomeFilter.biome()
        );

        register(
                context,
                CRATER_SPARSE,
                MercuryConfiguredFeatures.CRATER,
                RarityFilter.onAverageOnceEvery(10),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                BiomeFilter.biome()
        );

        register(
                context,
                LARGE_SHELVES,
                MercuryConfiguredFeatures.LARGE_SHELVES,
                CountPlacement.of(6),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.aboveBottom(128)),
                BiomeFilter.biome()
        );

        register(
                context,
                SMALL_SHELVES,
                MercuryConfiguredFeatures.SMALL_SHELVES,
                CountPlacement.of(24),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(
                        VerticalAnchor.BOTTOM,
                        VerticalAnchor.aboveBottom(128)
                ),
                EnvironmentScanPlacement.scanningFor(
                        Direction.DOWN,
                        BlockPredicate.solid(),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        32
                ),
                RandomOffsetPlacement.vertical(ConstantInt.of(1)),
                BiomeFilter.biome()
        );
    }

}
