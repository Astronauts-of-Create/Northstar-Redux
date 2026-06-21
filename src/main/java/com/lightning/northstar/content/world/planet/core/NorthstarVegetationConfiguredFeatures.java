package com.lightning.northstar.content.world.planet.core;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.crops.TallFungusBlock;
import com.lightning.northstar.block.simple.VenusMushroomBlock;
import com.lightning.northstar.block.simple.VenusTallMyceliumBlock;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.world.NorthstarFeatures;
import com.lightning.northstar.world.gen.feature.trunkplacers.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

public class NorthstarVegetationConfiguredFeatures {

    // FIXME: Coiler and Wilter aren't implemented but used by their respective saplings
    // FIXME: Trees probably shouldn't be using dirt if growing on different planets

    public static final ResourceKey<ConfiguredFeature<?, ?>>
            ARGYRE = key("argyre"),
            ARGYRE_CEILING = key("argyre_ceiling"),
            BLOOM_FUNGUS = key("bloom_fungus"),
            BLOOM_FUNGUS_ROOF = key("bloom_fungus_roof"),
            CALORIAN_VINES = key("calorian_vines"),
            COILER = key("coiler"),
            HUGE_BLOOM_FUNGUS = key("huge_bloom_fungus"),
            HUGE_BLOOM_FUNGUS_ROOF = key("huge_bloom_fungus_roof"),
            HUGE_PLATE_FUNGUS = key("huge_plate_fungus"),
            HUGE_PLATE_FUNGUS_ROOF = key("huge_plate_fungus_roof"),
            HUGE_SPIKE_FUNGUS = key("huge_spike_fungus"),
            HUGE_TOWER_FUNGUS = key("huge_tower_fungus"),
            HUGE_TOWER_FUNGUS_ROOF = key("huge_tower_fungus_roof"),
            PLATE_FUNGUS = key("plate_fungus"),
            PLATE_FUNGUS_ROOF = key("plate_fungus_roof"),
            SPIKE_FUNGUS = key("spike_fungus"),
            SPIKE_FUNGUS_ROOF = key("spike_fungus_roof"),
            TALL_VENUS_MYCELIUM = key("tall_venus_mycelium"),
            TALL_VENUS_MYCELIUM_ROOF = key("tall_venus_mycelium_roof"),
            TOWER_FUNGUS = key("tower_fungus"),
            TOWER_FUNGUS_ROOF = key("tower_fungus_roof"),
            WILTER = key("wilter");

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String path) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("core_" + path));
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<Block> block = context.lookup(Registries.BLOCK);

        // TODO: replace MANGROVE_*_CAN_GROW_THROUGH with proper tags

        context.register(
                ARGYRE,
                new ConfiguredFeature<>(
                        NorthstarFeatures.NATURAL_ARGYRE.get(),
                        new TreeConfiguration(
                                BlockStateProvider.simple(NorthstarBlocks.ARGYRE_LOG.get()),
                                new ArgyreTrunkPlacer(
                                        32,
                                        24,
                                        20,
                                        ConstantInt.of(4),
                                        0.5f,
                                        ConstantInt.of(4),
                                        block.getOrThrow(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH),
                                        ConstantInt.of(1),
                                        BlockStateProvider.simple(Blocks.GLOWSTONE)
                                ),
                                BlockStateProvider.simple(NorthstarBlocks.ARGYRE_LEAVES.get()),
                                new BlobFoliagePlacer(
                                        ConstantInt.of(2),
                                        ConstantInt.of(0),
                                        3
                                ),
                                Optional.empty(),
                                BlockStateProvider.simple(NorthstarBlocks.MARS_SOIL.get()),
                                new TwoLayersFeatureSize(
                                        2,
                                        0,
                                        2,
                                        OptionalInt.of(2)
                                ),
                                List.of(),
                                true,
                                false
                        )
                )
        );

        // TODO: argyre ceiling is the same than regular one, probably should be flipped somehow
        context.register(
                ARGYRE_CEILING,
                new ConfiguredFeature<>(
                        NorthstarFeatures.NATURAL_ARGYRE.get(),
                        new TreeConfiguration(
                                BlockStateProvider.simple(NorthstarBlocks.ARGYRE_LOG.get()),
                                //"glow_provider": BlockStateProvider.simple(Blocks.GLOWSTONE),
                                new ArgyreTrunkPlacer(
                                        32,
                                        24,
                                        20,
                                        ConstantInt.of(4),
                                        0.5f,
                                        ConstantInt.of(4),
                                        block.getOrThrow(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH),
                                        ConstantInt.of(1),
                                        BlockStateProvider.simple(Blocks.GLOWSTONE)
                                ),
                                BlockStateProvider.simple(NorthstarBlocks.ARGYRE_LEAVES.get()),
                                new BlobFoliagePlacer(
                                        ConstantInt.of(2),
                                        ConstantInt.of(0),
                                        3
                                ),
                                Optional.empty(),
                                BlockStateProvider.simple(NorthstarBlocks.MARS_SOIL.get()),
                                new TwoLayersFeatureSize(
                                        2,
                                        0,
                                        2,
                                        OptionalInt.of(2)
                                ),
                                List.of(),
                                true,
                                false
                        )
                )
        );

        context.register(
                BLOOM_FUNGUS,
                new ConfiguredFeature<>(
                        Feature.RANDOM_PATCH,
                        new RandomPatchConfiguration(
                                96,
                                7,
                                3,
                                Holder.direct(
                                        new PlacedFeature(
                                                Holder.direct(
                                                        new ConfiguredFeature<>(
                                                                Feature.SIMPLE_BLOCK,
                                                                new SimpleBlockConfiguration(
                                                                        BlockStateProvider.simple(NorthstarBlocks.BLOOM_FUNGUS.get())
                                                                )
                                                        )
                                                ),
                                                List.of(
                                                        BlockPredicateFilter.forPredicate(
                                                                BlockPredicate.ONLY_IN_AIR_PREDICATE
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        context.register(
                BLOOM_FUNGUS_ROOF,
                new ConfiguredFeature<>(
                        Feature.RANDOM_PATCH,
                        new RandomPatchConfiguration(
                                72,
                                7,
                                3,
                                Holder.direct(
                                        new PlacedFeature(
                                                Holder.direct(
                                                        new ConfiguredFeature<>(
                                                                Feature.SIMPLE_BLOCK,
                                                                new SimpleBlockConfiguration(
                                                                        BlockStateProvider.simple(NorthstarBlocks.BLOOM_FUNGUS.getDefaultState()
                                                                                .setValue(VenusMushroomBlock.IS_ON_CEILING, true))
                                                                )
                                                        )
                                                ),
                                                List.of(
                                                        BlockPredicateFilter.forPredicate(
                                                                BlockPredicate.ONLY_IN_AIR_PREDICATE
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        context.register(
                CALORIAN_VINES,
                new ConfiguredFeature<>(
                        Feature.TREE,
                        new TreeConfiguration(
                                BlockStateProvider.simple(NorthstarBlocks.CALORIAN_LOG.get()),
                                new CalorianVinesTrunkPlacer(
                                        32,
                                        2,
                                        12,
                                        ConstantInt.of(4),
                                        0.5f,
                                        ConstantInt.of(4),
                                        block.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH)
                                ),
                                BlockStateProvider.simple(Blocks.AIR),
                                new BlobFoliagePlacer(
                                        ConstantInt.of(2),
                                        ConstantInt.of(0),
                                        3
                                ),
                                Optional.empty(),
                                BlockStateProvider.simple(Blocks.DIRT),
                                new TwoLayersFeatureSize(
                                        1,
                                        0,
                                        1
                                ),
                                List.of(),
                                true,
                                false
                        )
                )
        );

        context.register(
                HUGE_BLOOM_FUNGUS,
                new ConfiguredFeature<>(
                        Feature.TREE,
                        new TreeConfiguration(
                                BlockStateProvider.simple(NorthstarBlocks.BLOOM_FUNGUS_STEM_BLOCK.get()),
                                new BloomTrunkPlacer(
                                        3,
                                        1,
                                        2,
                                        BlockStateProvider.simple(NorthstarBlocks.BLOOM_FUNGUS_BLOCK.get()),
                                        ConstantInt.of(4),
                                        block.getOrThrow(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
                                ),
                                BlockStateProvider.simple(Blocks.AIR),
                                new BlobFoliagePlacer(
                                        ConstantInt.of(2),
                                        ConstantInt.of(0),
                                        3
                                ),
                                Optional.empty(),
                                BlockStateProvider.simple(Blocks.DIRT),
                                new TwoLayersFeatureSize(
                                        1,
                                        0,
                                        1
                                ),
                                List.of(),
                                true,
                                false
                        )
                )
        );

        context.register(
                HUGE_BLOOM_FUNGUS_ROOF,
                new ConfiguredFeature<>(
                        Feature.TREE,
                        new TreeConfiguration(
                                BlockStateProvider.simple(NorthstarBlocks.BLOOM_FUNGUS_STEM_BLOCK.get()),
                                new RoofBloomTrunkPlacer(
                                        3,
                                        1,
                                        2,
                                        BlockStateProvider.simple(NorthstarBlocks.BLOOM_FUNGUS_BLOCK.get()),
                                        ConstantInt.of(4),
                                        block.getOrThrow(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
                                ),
                                BlockStateProvider.simple(Blocks.AIR),
                                new BlobFoliagePlacer(
                                        ConstantInt.of(2),
                                        ConstantInt.of(0),
                                        3
                                ),
                                Optional.empty(),
                                BlockStateProvider.simple(Blocks.DIRT),
                                new TwoLayersFeatureSize(
                                        1,
                                        0,
                                        1
                                ),
                                List.of(),
                                true,
                                false
                        )
                )
        );

        context.register(
                HUGE_PLATE_FUNGUS,
                new ConfiguredFeature<>(
                        Feature.TREE,
                        new TreeConfiguration(
                                BlockStateProvider.simple(NorthstarBlocks.PLATE_FUNGUS_STEM_BLOCK.get()),
                                new PlateTrunkPlacer(
                                        3,
                                        1,
                                        2,
                                        BlockStateProvider.simple(NorthstarBlocks.PLATE_FUNGUS_CAP_BLOCK.get()),
                                        ConstantInt.of(4),
                                        block.getOrThrow(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
                                ),
                                BlockStateProvider.simple(Blocks.AIR),
                                new BlobFoliagePlacer(
                                        ConstantInt.of(2),
                                        ConstantInt.of(0),
                                        3
                                ),
                                Optional.empty(),
                                BlockStateProvider.simple(Blocks.DIRT),
                                new TwoLayersFeatureSize(
                                        1,
                                        0,
                                        1
                                ),
                                List.of(),
                                true,
                                false
                        )
                )
        );

        context.register(
                HUGE_PLATE_FUNGUS_ROOF,
                new ConfiguredFeature<>(
                        Feature.TREE,
                        new TreeConfiguration(
                                BlockStateProvider.simple(NorthstarBlocks.PLATE_FUNGUS_STEM_BLOCK.get()),
                                new RoofPlateTrunkPlacer(
                                        8,
                                        1,
                                        4,
                                        BlockStateProvider.simple(NorthstarBlocks.PLATE_FUNGUS_CAP_BLOCK.get()),
                                        ConstantInt.of(4),
                                        block.getOrThrow(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
                                ),
                                BlockStateProvider.simple(Blocks.AIR),
                                new BlobFoliagePlacer(
                                        ConstantInt.of(2),
                                        ConstantInt.of(0),
                                        3
                                ),
                                Optional.empty(),
                                BlockStateProvider.simple(Blocks.DIRT),
                                new TwoLayersFeatureSize(
                                        1,
                                        0,
                                        1
                                ),
                                List.of(),
                                true,
                                false
                        )
                )
        );

        context.register(
                HUGE_SPIKE_FUNGUS,
                new ConfiguredFeature<>(
                        Feature.TREE,
                        new TreeConfiguration(
                                BlockStateProvider.simple(NorthstarBlocks.SPIKE_FUNGUS_BLOCK.get()),
                                new SpikeTrunkPlacer(
                                        8,
                                        1,
                                        4,
                                        ConstantInt.of(4),
                                        block.getOrThrow(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
                                ),
                                BlockStateProvider.simple(Blocks.AIR),
                                new BlobFoliagePlacer(
                                        ConstantInt.of(2),
                                        ConstantInt.of(0),
                                        3
                                ),
                                Optional.empty(),
                                BlockStateProvider.simple(Blocks.DIRT),
                                new TwoLayersFeatureSize(
                                        1,
                                        0,
                                        1
                                ),
                                List.of(),
                                true,
                                false
                        )
                )
        );

        context.register(
                HUGE_TOWER_FUNGUS,
                new ConfiguredFeature<>(
                        Feature.TREE,
                        new TreeConfiguration(
                                BlockStateProvider.simple(NorthstarBlocks.TOWER_FUNGUS_STEM_BLOCK.get()),
                                new TowerTrunkPlacer(
                                        8,
                                        1,
                                        4,
                                        BlockStateProvider.simple(NorthstarBlocks.TOWER_FUNGUS_CAP_BLOCK.get()),
                                        ConstantInt.of(4),
                                        block.getOrThrow(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
                                ),
                                BlockStateProvider.simple(Blocks.AIR),
                                new BlobFoliagePlacer(
                                        ConstantInt.of(2),
                                        ConstantInt.of(0),
                                        3
                                ),
                                Optional.empty(),
                                BlockStateProvider.simple(Blocks.DIRT),
                                new TwoLayersFeatureSize(
                                        1,
                                        0,
                                        1
                                ),
                                List.of(),
                                true,
                                false
                        )
                )
        );

        context.register(
                HUGE_TOWER_FUNGUS_ROOF,
                new ConfiguredFeature<>(
                        Feature.TREE,
                        new TreeConfiguration(
                                BlockStateProvider.simple(NorthstarBlocks.TOWER_FUNGUS_STEM_BLOCK.get()),
                                new RoofTowerTrunkPlacer(
                                        8,
                                        1,
                                        4,
                                        BlockStateProvider.simple(NorthstarBlocks.TOWER_FUNGUS_CAP_BLOCK.get()),
                                        ConstantInt.of(4),
                                        block.getOrThrow(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
                                ),
                                BlockStateProvider.simple(Blocks.AIR),
                                new BlobFoliagePlacer(
                                        ConstantInt.of(2),
                                        ConstantInt.of(0),
                                        3
                                ),
                                Optional.empty(),
                                BlockStateProvider.simple(Blocks.DIRT),
                                new TwoLayersFeatureSize(
                                        1,
                                        0,
                                        1
                                ),
                                List.of(),
                                true,
                                false
                        )
                )
        );

        Function<BlockState, RandomPatchConfiguration> randomPatchConfig = state -> new RandomPatchConfiguration(
                96,
                7,
                3,
                Holder.direct(
                        new PlacedFeature(
                                Holder.direct(
                                        new ConfiguredFeature<>(
                                                Feature.SIMPLE_BLOCK,
                                                new SimpleBlockConfiguration(
                                                        BlockStateProvider.simple(state)
                                                )
                                        )
                                ),
                                List.of(
                                        BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
                                )
                        )
                )
        );

        context.register(
                PLATE_FUNGUS,
                new ConfiguredFeature<>(
                        Feature.RANDOM_PATCH,
                        randomPatchConfig.apply(NorthstarBlocks.PLATE_FUNGUS
                                .getDefaultState()
                                .setValue(VenusMushroomBlock.IS_ON_CEILING, false))
                )
        );

        context.register(
                PLATE_FUNGUS_ROOF,
                new ConfiguredFeature<>(
                        Feature.RANDOM_PATCH,
                        randomPatchConfig.apply(NorthstarBlocks.PLATE_FUNGUS
                                .getDefaultState()
                                .setValue(VenusMushroomBlock.IS_ON_CEILING, true))
                )
        );

        context.register(
                SPIKE_FUNGUS,
                new ConfiguredFeature<>(
                        Feature.RANDOM_PATCH,
                        randomPatchConfig.apply(NorthstarBlocks.SPIKE_FUNGUS
                                .getDefaultState()
                                .setValue(VenusMushroomBlock.IS_ON_CEILING, false))
                )
        );

        context.register(
                SPIKE_FUNGUS_ROOF,
                new ConfiguredFeature<>(
                        Feature.RANDOM_PATCH,
                        randomPatchConfig.apply(NorthstarBlocks.SPIKE_FUNGUS
                                .getDefaultState()
                                .setValue(VenusMushroomBlock.IS_ON_CEILING, true))
                )
        );

        context.register(
                TALL_VENUS_MYCELIUM,
                new ConfiguredFeature<>(
                        Feature.RANDOM_PATCH,
                        randomPatchConfig.apply(NorthstarBlocks.TALL_VENUS_MYCELIUM
                                .getDefaultState()
                                .setValue(VenusTallMyceliumBlock.IS_ON_CEILING, false))
                )
        );

        context.register(
                TALL_VENUS_MYCELIUM_ROOF,
                new ConfiguredFeature<>(
                        Feature.RANDOM_PATCH,
                        randomPatchConfig.apply(NorthstarBlocks.TALL_VENUS_MYCELIUM
                                .getDefaultState()
                                .setValue(VenusTallMyceliumBlock.IS_ON_CEILING, true))
                )
        );

        context.register(
                TOWER_FUNGUS,
                new ConfiguredFeature<>(
                        Feature.RANDOM_PATCH,
                        randomPatchConfig.apply(NorthstarBlocks.TOWER_FUNGUS
                                .getDefaultState()
                                .setValue(TallFungusBlock.IS_ON_CEILING, false))
                )
        );

        context.register(
                TOWER_FUNGUS_ROOF,
                new ConfiguredFeature<>(
                        Feature.RANDOM_PATCH,
                        randomPatchConfig.apply(NorthstarBlocks.TOWER_FUNGUS
                                .getDefaultState()
                                .setValue(TallFungusBlock.IS_ON_CEILING, true))
                )
        );
    }

}