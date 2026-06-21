package com.lightning.northstar.content.world.planet.core;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.world.NorthstarFeatures;
import com.lightning.northstar.world.gen.feature.configuration.GlowstoneBranchConfig;
import com.lightning.northstar.world.gen.feature.configuration.PointedStoneClusterConfiguration;
import com.lightning.northstar.world.gen.feature.configuration.StoneClusterConfiguration;
import com.lightning.northstar.world.gen.feature.configuration.StoneColumnConfiguration;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class NorthstarConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>>
            ASURINE_CLUSTER = key("asurine_cluster"),
            ASURINE_COLUMN = key("asurine_column"),
            BIG_DUMB_ROCK = key("big_dumb_rock"),
            BLUE_ICE_CLUSTER = key("blue_ice_cluster"),
            CRIMSITE_CLUSTER = key("crimsite_cluster"),
            CRIMSITE_COLUMN = key("crimsite_column"),
            CRIMSITE_POINTED_CLUSTER = key("crimsite_pointed_cluster"),
            FROST = key("frost"),
            GLOWSTONE_BRANCH = key("glowstone_branch"),
            GLOWSTONE_COLUMN = key("glowstone_column"),
            GLOWSTONE_UPSIDE_DOWN_BRANCH = key("glowstone_upside_down_branch"),
            ICE_CLUSTER = key("ice_cluster"),
            ICE_COLUMN = key("ice_column"),
            ICICLE_CLUSTER = key("icicle_cluster"),
            MAGMA_CLUSTER = key("magma_cluster"),
            OBSIDIAN_CLUSTER = key("obsidian_cluster"),
            PACKED_ICE_CLUSTER = key("packed_ice_cluster"),
            VOLCANIC_ASH_CLUSTER = key("volcanic_ash_cluster"),
            VOLCANIC_ROCK_CLUSTER = key("volcanic_rock_cluster");

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String path) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("core_" + path));
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(
                ASURINE_CLUSTER,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STONE_CLUSTER.get(),
                        new StoneClusterConfiguration(
                                36,
                                BlockStateProvider.simple(AllPaletteStoneTypes.ASURINE.baseBlock.get()),
                                Fluids.LAVA.getSource(true),
                                UniformInt.of(3, 6),
                                UniformInt.of(2, 8),
                                1,
                                3,
                                UniformInt.of(2, 4),
                                UniformFloat.of(0.3f, 0.7f),
                                ConstantFloat.of(0),
                                ConstantFloat.of(0),
                                ConstantFloat.of(0),
                                0.1f,
                                3,
                                8
                        )
                )
        );

        context.register(
                ASURINE_COLUMN,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STONE_COLUMN.get(),
                        new StoneColumnConfiguration(
                                36,
                                UniformInt.of(3, 19),
                                UniformFloat.of(0.4f, 2),
                                0.33f,
                                UniformFloat.of(0.3f, 0.9f),
                                UniformFloat.of(0.4f, 1),
                                UniformFloat.of(0, 0.3f),
                                4,
                                0.6f,
                                BlockStateProvider.simple(AllPaletteStoneTypes.ASURINE.baseBlock.get())
                        )
                )
        );

        // TODO: This should just use a sphere instead of a geode
        context.register(
                BIG_DUMB_ROCK,
                new ConfiguredFeature<>(
                        Feature.GEODE,
                        new GeodeConfiguration(
                                new GeodeBlockSettings(
                                        BlockStateProvider.simple(NorthstarBlocks.MARS_STONE.get()),
                                        BlockStateProvider.simple(NorthstarBlocks.MARS_STONE.get()),
                                        BlockStateProvider.simple(NorthstarBlocks.MARS_STONE.get()),
                                        BlockStateProvider.simple(NorthstarBlocks.MARS_STONE.get()),
                                        BlockStateProvider.simple(NorthstarBlocks.MARS_STONE.get()),
                                        List.of(
                                                NorthstarBlocks.MARS_STONE.getDefaultState(),
                                                NorthstarBlocks.MARS_STONE.getDefaultState(),
                                                NorthstarBlocks.MARS_STONE.getDefaultState(),
                                                NorthstarBlocks.MARS_STONE.getDefaultState()
                                        ),
                                        BlockTags.FEATURES_CANNOT_REPLACE,
                                        BlockTags.GEODE_INVALID_BLOCKS
                                ),
                                new GeodeLayerSettings(
                                        1.7f,
                                        2,
                                        2.7f,
                                        3.6f
                                ),
                                new GeodeCrackSettings(
                                        0.95f,
                                        1,
                                        2
                                ),
                                0.35f,
                                0.083f,
                                true,
                                UniformInt.of(4, 6),
                                UniformInt.of(3, 4),
                                UniformInt.of(1, 2),
                                -16,
                                16,
                                0.06,
                                1
                        )
                )
        );

        context.register(
                BLUE_ICE_CLUSTER,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STONE_CLUSTER.get(),
                        new StoneClusterConfiguration(
                                36,
                                BlockStateProvider.simple(Blocks.BLUE_ICE),
                                Fluids.LAVA.getSource(true),
                                UniformInt.of(3, 6),
                                UniformInt.of(2, 8),
                                1,
                                3,
                                UniformInt.of(1, 2),
                                UniformFloat.of(0.4f, 0.8f),
                                ConstantFloat.of(0),
                                ConstantFloat.of(0),
                                ConstantFloat.of(0),
                                0.1f,
                                3,
                                8
                        )
                )
        );

        context.register(
                CRIMSITE_CLUSTER,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STONE_CLUSTER.get(),
                        new StoneClusterConfiguration(
                                36,
                                BlockStateProvider.simple(AllPaletteStoneTypes.CRIMSITE.baseBlock.get()),
                                Fluids.LAVA.getSource(true),
                                UniformInt.of(3, 6),
                                UniformInt.of(2, 8),
                                1,
                                3,
                                UniformInt.of(2, 4),
                                UniformFloat.of(0.3f, 0.7f),
                                ConstantFloat.of(0),
                                ConstantFloat.of(0),
                                ConstantFloat.of(0),
                                0.1f,
                                3,
                                8
                        )
                )
        );

        context.register(
                CRIMSITE_COLUMN,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STONE_COLUMN.get(),
                        new StoneColumnConfiguration(
                                36,
                                UniformInt.of(3, 19),
                                UniformFloat.of(0.4f, 2),
                                0.33f,
                                UniformFloat.of(0.3f, 0.9f),
                                UniformFloat.of(0.4f, 1),
                                UniformFloat.of(0, 0.3f),
                                4,
                                0.6f,
                                BlockStateProvider.simple(AllPaletteStoneTypes.CRIMSITE.baseBlock.get())
                        )
                )
        );

        Int2ObjectFunction<Holder<PlacedFeature>> pointedCrimsiteCluster = dirY -> Holder.direct(
                new PlacedFeature(
                        Holder.direct(
                                new ConfiguredFeature<>(
                                        NorthstarFeatures.POINTED_STONE_CLUSTER.get(),
                                        new PointedStoneClusterConfiguration(
                                                0.3f,
                                                0.8f,
                                                0.7f,
                                                0.7f,
                                                BlockStateProvider.simple(NorthstarBlocks.POINTED_CRIMSITE.get()),
                                                BlockStateProvider.simple(AllPaletteStoneTypes.CRIMSITE.baseBlock.get())
                                        )
                                )
                        ),
                        List.of(
                                EnvironmentScanPlacement.scanningFor(
                                        Direction.DOWN,
                                        BlockPredicate.solid(),
                                        BlockPredicate.matchesBlocks(
                                                Blocks.AIR,
                                                Blocks.WATER
                                        ),
                                        12
                                ),
                                RandomOffsetPlacement.vertical(
                                        ConstantInt.of(dirY)
                                )
                        )
                )
        );
        context.register(
                CRIMSITE_POINTED_CLUSTER,
                new ConfiguredFeature<>(
                        Feature.SIMPLE_RANDOM_SELECTOR,
                        new SimpleRandomFeatureConfiguration(
                                HolderSet.direct(
                                        pointedCrimsiteCluster.apply(1),
                                        pointedCrimsiteCluster.apply(-1)
                                )
                        )
                )
        );

        context.register(
                FROST,
                new ConfiguredFeature<>(
                        NorthstarFeatures.MULTIFACE_GROWTH_CUSTOM.get(),
                        new MultifaceGrowthConfiguration(
                                NorthstarBlocks.FROST.get(),
                                32,
                                false,
                                true,
                                true,
                                0.93f,
                                HolderSet.direct(
                                        Block::builtInRegistryHolder,
                                        Blocks.ICE,
                                        Blocks.PACKED_ICE,
                                        Blocks.BLACKSTONE,
                                        NorthstarBlocks.MOON_SAND.get(),
                                        NorthstarBlocks.MOON_STONE.get(),
                                        NorthstarBlocks.MOON_DEEP_STONE.get(),
                                        NorthstarBlocks.MERCURY_STONE.get(),
                                        NorthstarBlocks.MERCURY_DEEP_STONE.get(),
                                        AllPaletteStoneTypes.SCORIA.baseBlock.get(),
                                        AllPaletteStoneTypes.SCORCHIA.baseBlock.get()
                                )
                        )
                )
        );


        context.register(
                GLOWSTONE_BRANCH,
                new ConfiguredFeature<>(
                        NorthstarFeatures.GLOWSTONE_BRANCH.get(),
                        new GlowstoneBranchConfig(
                                BlockStateProvider.simple(Blocks.GLOWSTONE),
                                1500,
                                false
                        )
                )
        );

        context.register(
                GLOWSTONE_COLUMN,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STONE_COLUMN.get(),
                        new StoneColumnConfiguration(
                                36,
                                UniformInt.of(3, 19),
                                UniformFloat.of(0.4f, 2.0f),
                                0.33f,
                                UniformFloat.of(0.3f, 0.9f),
                                UniformFloat.of(0.4f, 1.0f),
                                UniformFloat.of(0f, 0.3f),
                                4,
                                0.6f,
                                BlockStateProvider.simple(Blocks.GLOWSTONE)
                        )
                )
        );

        context.register(
                GLOWSTONE_UPSIDE_DOWN_BRANCH,
                new ConfiguredFeature<>(
                        NorthstarFeatures.GLOWSTONE_BRANCH.get(),
                        new GlowstoneBranchConfig(
                                BlockStateProvider.simple(Blocks.GLOWSTONE),
                                750,
                                true
                        )
                )
        );

        context.register(
                ICE_CLUSTER,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STONE_CLUSTER.get(),
                        new StoneClusterConfiguration(
                                36,
                                BlockStateProvider.simple(Blocks.PACKED_ICE),
                                Fluids.LAVA.getSource(true),
                                UniformInt.of(3, 6),
                                UniformInt.of(2, 8),
                                1,
                                3,
                                UniformInt.of(2, 4),
                                UniformFloat.of(0.3f, 0.7f),
                                ConstantFloat.of(0),
                                ConstantFloat.of(0),
                                ConstantFloat.of(0),
                                0.1f,
                                3,
                                8
                        )
                )
        );

        context.register(
                ICE_COLUMN,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STONE_COLUMN.get(),
                        new StoneColumnConfiguration(
                                36,
                                UniformInt.of(3, 19),
                                UniformFloat.of(0.4f, 2),
                                0.33f,
                                UniformFloat.of(0.3f, 0.9f),
                                UniformFloat.of(0.4f, 1),
                                UniformFloat.of(0, 0.3f),
                                4,
                                0.6f,
                                BlockStateProvider.simple(Blocks.PACKED_ICE)
                        )
                )
        );

        Int2ObjectFunction<Holder<PlacedFeature>> icicle = offset -> Holder.direct(
                new PlacedFeature(
                        Holder.direct(
                                new ConfiguredFeature<>(
                                        NorthstarFeatures.POINTED_STONE_CLUSTER.get(),
                                        new PointedStoneClusterConfiguration(
                                                0.3f,
                                                0.8f,
                                                0.7f,
                                                0.7f,
                                                BlockStateProvider.simple(NorthstarBlocks.ICICLE.get()),
                                                BlockStateProvider.simple(Blocks.PACKED_ICE)
                                        )
                                )
                        ),
                        List.of(
                                EnvironmentScanPlacement.scanningFor(
                                        Direction.DOWN,
                                        BlockPredicate.solid(),
                                        BlockPredicate.matchesBlocks(
                                                Blocks.AIR,
                                                Blocks.WATER
                                        ),
                                        12
                                ),
                                RandomOffsetPlacement.vertical(
                                        ConstantInt.of(offset)
                                )
                        )
                )
        );

        context.register(
                ICICLE_CLUSTER,
                new ConfiguredFeature<>(
                        Feature.SIMPLE_RANDOM_SELECTOR,
                        new SimpleRandomFeatureConfiguration(
                                HolderSet.direct(
                                        icicle.apply(1),
                                        icicle.apply(-1)
                                )
                        )
                )
        );

        context.register(
                MAGMA_CLUSTER,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STONE_CLUSTER.get(),
                        new StoneClusterConfiguration(
                                36,
                                BlockStateProvider.simple(Blocks.MAGMA_BLOCK),
                                Fluids.LAVA.getSource(true),
                                UniformInt.of(3, 6),
                                UniformInt.of(1, 3),
                                1,
                                3,
                                UniformInt.of(1, 2),
                                UniformFloat.of(0.2f, 0.4f),
                                ConstantFloat.of(0),
                                ClampedNormalFloat.of(0.1f, 0.9f, 0.1f, 0.3f),
                                ConstantFloat.of(0),
                                0.1f,
                                3,
                                8
                        )
                )
        );

        context.register(
                OBSIDIAN_CLUSTER,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STONE_CLUSTER.get(),
                        new StoneClusterConfiguration(
                                36,
                                BlockStateProvider.simple(Blocks.OBSIDIAN),
                                Fluids.LAVA.getSource(true),
                                UniformInt.of(3, 6),
                                UniformInt.of(2, 4),
                                1,
                                4,
                                UniformInt.of(2, 4),
                                UniformFloat.of(0.4f, 0.9f),
                                ConstantFloat.of(0),
                                ClampedNormalFloat.of(0.05f, 0.05f, 0, 0.1f),
                                ConstantFloat.of(0),
                                0.3f,
                                3,
                                8
                        )
                )
        );

        context.register(
                PACKED_ICE_CLUSTER,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STONE_CLUSTER.get(),
                        new StoneClusterConfiguration(
                                36,
                                BlockStateProvider.simple(Blocks.PACKED_ICE),
                                Fluids.WATER.getSource(true),
                                UniformInt.of(2, 4),
                                UniformInt.of(2, 4),
                                1,
                                3,
                                UniformInt.of(2, 4),
                                UniformFloat.of(0.3f, 0.7f),
                                ConstantFloat.of(0),
                                ConstantFloat.of(0),
                                ConstantFloat.of(0),
                                0.1f,
                                3,
                                8
                        )
                )
        );

        context.register(
                VOLCANIC_ASH_CLUSTER,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STONE_CLUSTER.get(),
                        new StoneClusterConfiguration(
                                36,
                                BlockStateProvider.simple(NorthstarBlocks.VOLCANIC_ASH.get()),
                                Fluids.LAVA.getSource(true),
                                UniformInt.of(3, 6),
                                UniformInt.of(2, 8),
                                1,
                                3,
                                UniformInt.of(1, 2),
                                UniformFloat.of(0.4f, 0.8f),
                                ConstantFloat.of(0),
                                ClampedNormalFloat.of(0.1f, 0.9f, 0.1f, 0.3f),
                                ClampedNormalFloat.of(0.1f, 0.9f, 0.1f, 0.3f),
                                0.1f,
                                3,
                                8
                        )
                )
        );

        context.register(
                VOLCANIC_ROCK_CLUSTER,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STONE_CLUSTER.get(),
                        new StoneClusterConfiguration(
                                36,
                                BlockStateProvider.simple(NorthstarBlocks.VOLCANIC_ROCK.get()),
                                Fluids.LAVA.getSource(true),
                                UniformInt.of(6, 9),
                                UniformInt.of(5, 12),
                                1,
                                3,
                                UniformInt.of(1, 2),
                                UniformFloat.of(0.4f, 0.8f),
                                ConstantFloat.of(0),
                                ClampedNormalFloat.of(0.1f, 0.9f, 0.1f, 0.3f),
                                ClampedNormalFloat.of(0.1f, 0.9f, 0.1f, 0.3f),
                                0.1f,
                                3,
                                8
                        )
                )
        );
    }

}