package com.lightning.northstar.content.world.planet.core;

import com.lightning.northstar.Northstar;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class NorthstarPlacedFeatures {

    public static final ResourceKey<PlacedFeature>
            ASURINE_CLUSTER = key("asurine_cluster"),
            ASURINE_COLUMN = key("asurine_column"),
            BIG_DUMB_ROCK = key("big_dumb_rock"),
            BLUE_ICE_CLUSTER = key("blue_ice_cluster"),
            CRIMSITE_CLUSTER = key("crimsite_cluster"),
            CRIMSITE_COLUMN = key("crimsite_column"),
            FROST = key("frost"),
            FROST_SPARSE = key("frost_sparse"),
            GLOWSTONE_BRANCH = key("glowstone_branch"),
            GLOWSTONE_COLUMN = key("glowstone_column"),
            GLOWSTONE_UPSIDE_DOWN_BRANCH = key("glowstone_upside_down_branch"),
            ICE_CLUSTER = key("ice_cluster"),
            ICE_COLUMN = key("ice_column"),
            ICICLES = key("icicles"),
            MAGMA_CLUSTER = key("magma_cluster"),
            OBSIDIAN_CLUSTER = key("obsidian_cluster"),
            PACKED_ICE_CLUSTER = key("packed_ice_cluster"),
            POINTED_CRIMSITE = key("pointed_crimsite"),
            RARE_LAVA_LAKE = key("rare_lava_lake"),
            VOLCANIC_ASH_CLUSTER = key("volcanic_ash_cluster"),
            VOLCANIC_ASH_SURFACE_CLUSTER = key("volcanic_ash_surface_cluster"),
            VOLCANIC_ROCK_CLUSTER = key("volcanic_rock_cluster"),
            VOLCANIC_ROCK_SURFACE_CLUSTER = key("volcanic_rock_surface_cluster");

    private static ResourceKey<PlacedFeature> key(String path) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource(path));
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        register(
                context,
                ASURINE_CLUSTER,
                NorthstarConfiguredFeatures.ASURINE_CLUSTER,
                CountPlacement.of(UniformInt.of(48, 96)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                BiomeFilter.biome()
        );

        register(
                context,
                ASURINE_COLUMN,
                NorthstarConfiguredFeatures.ASURINE_COLUMN,
                CountPlacement.of(UniformInt.of(30, 60)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                BiomeFilter.biome()
        );

        register(
                context,
                BIG_DUMB_ROCK,
                NorthstarConfiguredFeatures.BIG_DUMB_ROCK,
                RarityFilter.onAverageOnceEvery(2),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                BiomeFilter.biome()
        );

        register(
                context,
                BLUE_ICE_CLUSTER,
                NorthstarConfiguredFeatures.BLUE_ICE_CLUSTER,
                CountPlacement.of(UniformInt.of(2, 6)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                BiomeFilter.biome()
        );

        register(
                context,
                CRIMSITE_CLUSTER,
                NorthstarConfiguredFeatures.CRIMSITE_CLUSTER,
                CountPlacement.of(UniformInt.of(48, 96)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                BiomeFilter.biome()
        );

        register(
                context,
                CRIMSITE_COLUMN,
                NorthstarConfiguredFeatures.CRIMSITE_COLUMN,
                CountPlacement.of(UniformInt.of(10, 48)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                BiomeFilter.biome()
        );

        register(
                context,
                FROST,
                NorthstarConfiguredFeatures.FROST,
                CountPlacement.of(UniformInt.of(12, 36)),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(128)),
                InSquarePlacement.spread(),
                SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -13),
                BiomeFilter.biome(

                ));

        register(
                context,
                FROST_SPARSE,
                NorthstarConfiguredFeatures.FROST,
                RarityFilter.onAverageOnceEvery(3),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(128)),
                InSquarePlacement.spread(),
                SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -13),
                BiomeFilter.biome()
        );

        register(
                context,
                GLOWSTONE_BRANCH,
                NorthstarConfiguredFeatures.GLOWSTONE_BRANCH,
                CountPlacement.of(UniformInt.of(15, 50)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP),
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(), BlockPredicate.matchesBlocks(Blocks.AIR), 32),
                RandomOffsetPlacement.vertical(ConstantInt.of(-1)),
                BiomeFilter.biome()
        );

        register(
                context,
                GLOWSTONE_COLUMN,
                NorthstarConfiguredFeatures.GLOWSTONE_COLUMN,
                CountPlacement.of(UniformInt.of(10, 48)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                BiomeFilter.biome()
        );

        register(
                context,
                GLOWSTONE_UPSIDE_DOWN_BRANCH,
                NorthstarConfiguredFeatures.GLOWSTONE_UPSIDE_DOWN_BRANCH,
                CountPlacement.of(UniformInt.of(2, 10)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP),
                EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.matchesBlocks(Blocks.AIR), 32),
                RandomOffsetPlacement.vertical(ConstantInt.of(1)),
                BiomeFilter.biome()
        );

        register(
                context,
                ICE_CLUSTER,
                NorthstarConfiguredFeatures.ICE_CLUSTER,
                CountPlacement.of(UniformInt.of(48, 96)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                BiomeFilter.biome()
        );

        register(
                context,
                ICE_COLUMN,
                NorthstarConfiguredFeatures.ICE_COLUMN,
                CountPlacement.of(UniformInt.of(40, 80)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                BiomeFilter.biome()
        );

        register(
                context,
                ICICLES,
                NorthstarConfiguredFeatures.ICICLE_CLUSTER,
                CountPlacement.of(UniformInt.of(200, 256)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                CountPlacement.of(UniformInt.of(1, 5)),
                RandomOffsetPlacement.of(
                        ClampedNormalInt.of(0, 3, -10, 10),
                        ClampedNormalInt.of(0, 0.6f, -2, 2)
                ),
                BiomeFilter.biome()
        );

        register(
                context, MAGMA_CLUSTER,
                NorthstarConfiguredFeatures.MAGMA_CLUSTER,
                CountPlacement.of(UniformInt.of(8, 16)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                BiomeFilter.biome()
        );

        register(
                context, OBSIDIAN_CLUSTER,
                NorthstarConfiguredFeatures.OBSIDIAN_CLUSTER,
                CountPlacement.of(UniformInt.of(48, 96)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                BiomeFilter.biome()
        );

        register(
                context, PACKED_ICE_CLUSTER,
                NorthstarConfiguredFeatures.PACKED_ICE_CLUSTER,
                CountPlacement.of(UniformInt.of(48, 96)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                BiomeFilter.biome()
        );

        register(
                context, POINTED_CRIMSITE,
                NorthstarConfiguredFeatures.CRIMSITE_POINTED_CLUSTER,
                CountPlacement.of(UniformInt.of(200, 256)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                CountPlacement.of(UniformInt.of(1, 5)),
                RandomOffsetPlacement.of(
                        ClampedNormalInt.of(0, 3, -10, 10),
                        ClampedNormalInt.of(0, 0.6f, -2, 2)
                ),
                BiomeFilter.biome()
        );

        register(
                context,
                RARE_LAVA_LAKE,
                MiscOverworldFeatures.LAKE_LAVA,
                RarityFilter.onAverageOnceEvery(300),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.TOP),
                EnvironmentScanPlacement.scanningFor(
                        Direction.DOWN,
                        BlockPredicate.allOf(
                                BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE),
                                BlockPredicate.insideWorld(new Vec3i(0, -5, 0))
                        ),
                        32
                ),
                SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5),
                BiomeFilter.biome()
        );

        register(
                context,
                VOLCANIC_ASH_CLUSTER,
                NorthstarConfiguredFeatures.VOLCANIC_ASH_CLUSTER,
                CountPlacement.of(UniformInt.of(32, 64)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                BiomeFilter.biome()
        );

        register(
                context,
                VOLCANIC_ASH_SURFACE_CLUSTER,
                NorthstarConfiguredFeatures.VOLCANIC_ASH_CLUSTER,
                CountPlacement.of(UniformInt.of(32, 64)),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                BiomeFilter.biome()
        );

        register(
                context,
                VOLCANIC_ROCK_CLUSTER,
                NorthstarConfiguredFeatures.VOLCANIC_ROCK_CLUSTER,
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                BiomeFilter.biome()
        );

        register(
                context,
                VOLCANIC_ROCK_SURFACE_CLUSTER,
                NorthstarConfiguredFeatures.VOLCANIC_ROCK_CLUSTER,
                RarityFilter.onAverageOnceEvery(12),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                BiomeFilter.biome()
        );
    }

    public static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, ResourceKey<ConfiguredFeature<?, ?>> feature, PlacementModifier... placements) {
        context.register(
                key,
                new PlacedFeature(
                        context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(feature),
                        List.of(placements)
                )
        );
    }

}
