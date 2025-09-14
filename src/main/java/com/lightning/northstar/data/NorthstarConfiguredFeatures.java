package com.lightning.northstar.data;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.world.features.MercuryCactusFeature;
import com.lightning.northstar.world.features.NorthstarFeatures;
import com.lightning.northstar.world.features.configuration.AlienTreeConfig;
import com.lightning.northstar.world.features.configuration.GlowstoneBranchConfig;
import com.lightning.northstar.world.features.configuration.StoneColumnConfiguration;
import com.lightning.northstar.world.features.foliageplacers.CoilerFoliagePlacer;
import com.lightning.northstar.world.features.trunkplacers.*;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.AboveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.Optional;

public class NorthstarConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> COILER = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("coiler"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILTER = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("wilter"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ARGYRE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("argyre"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> SPIKE_FUNGUS = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("spike_fungus"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLOOM_FUNGUS = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("bloom_fungus"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ROOF_BLOOM_FUNGUS = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("roof_bloom_fungus"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> PLATE_FUNGUS = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("plate_fungus"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ROOF_PLATE_FUNGUS = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("roof_plate_fungus"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> TOWER_FUNGUS = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("tower_fungus"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ROOF_TOWER_FUNGUS = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("roof_tower_fungus"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ARGYRE_CEILING = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("argyre_ceiling"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ARGYRE_SMALL = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("argyre_small"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> CALORIAN_VINES = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("calorian_vines"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> MERCURY_CACTUS = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("mercury_cactus"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> CRIMSITE_COLUMN = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("crimsite_column"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLOWSTONE_BRANCH = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("glowstone_branch"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLOWSTONE_UPSIDE_DOWN_BRANCH = ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("glowstone_upside_down_branch"));

    @SuppressWarnings("unchecked")
    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<Block> block = context.lookup(Registries.BLOCK);

        context.register(COILER, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(NorthstarBlocks.COILER_LOG.get()),
                        new CoilerTrunkPlacer(6, 1, 9, UniformInt.of(1, 4), 0.5F, UniformInt.of(0, 1), block.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), UniformInt.of(-1, 1)),
                        BlockStateProvider.simple(NorthstarBlocks.COILER_LEAVES.get()),
                        new CoilerFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0)), Optional.of(new MangroveRootPlacer(UniformInt.of(2, 4), BlockStateProvider.simple(NorthstarBlocks.COILER_LOG.get()), Optional.of(new AboveRootPlacement(BlockStateProvider.simple(Blocks.AIR), 0.5F)),
                        new MangroveRootPlacement(block.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), HolderSet.direct(Block::builtInRegistryHolder, Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS), BlockStateProvider.simple(NorthstarBlocks.COILER_LOG.get()), 8, 15, 0.2F))),
                        new TwoLayersFeatureSize(3, 0, 2)).build()));

        context.register(WILTER, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(NorthstarBlocks.WILTER_LOG.get()),
                        new WilterTrunkPlacer(9, 1, 5, UniformInt.of(4, 4), 0.5F, UniformInt.of(0, 1), block.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), UniformInt.of(-1, 1)),
                        BlockStateProvider.simple(NorthstarBlocks.WILTER_LEAVES.get()),
                        new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                        new TwoLayersFeatureSize(2, 0, 2)).build()));

        context.register(ARGYRE, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(NorthstarBlocks.ARGYRE_LOG.get()),
                        new ArgyreTrunkPlacer(32, 24, 20, UniformInt.of(4, 4), 0.5F, UniformInt.of(0, 1), block.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), UniformInt.of(1, 1)),
                        BlockStateProvider.simple(NorthstarBlocks.ARGYRE_LEAVES.get()),
                        new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                        new TwoLayersFeatureSize(2, 0, 2)).build()));

        context.register(SPIKE_FUNGUS, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(NorthstarBlocks.SPIKE_FUNGUS_BLOCK.get()),
                        new SpikeTrunkPlacer(8, 1, 4, UniformInt.of(4, 4), block.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH)),
                        BlockStateProvider.simple(NorthstarBlocks.WILTER_LEAVES.get()),
                        new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                        new TwoLayersFeatureSize(2, 0, 2)).build()));

        context.register(BLOOM_FUNGUS, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(NorthstarBlocks.BLOOM_FUNGUS_STEM_BLOCK.get()),
                        new BloomTrunkPlacer(3, 1, 2, UniformInt.of(4, 4), block.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), BlockStateProvider.simple(NorthstarBlocks.BLOOM_FUNGUS_BLOCK.get())), BlockStateProvider.simple(NorthstarBlocks.WILTER_LEAVES.get()),
                        new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                        new TwoLayersFeatureSize(2, 0, 2)).build()));

        context.register(ROOF_BLOOM_FUNGUS, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(NorthstarBlocks.BLOOM_FUNGUS_STEM_BLOCK.get()),
                        new RoofBloomTrunkPlacer(3, 1, 2, UniformInt.of(4, 4), block.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), BlockStateProvider.simple(NorthstarBlocks.BLOOM_FUNGUS_BLOCK.get())),
                        BlockStateProvider.simple(NorthstarBlocks.WILTER_LEAVES.get()),
                        new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                        new TwoLayersFeatureSize(2, 0, 2)).build()));

        context.register(PLATE_FUNGUS, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(NorthstarBlocks.PLATE_FUNGUS_STEM_BLOCK.get()),
                        new PlateTrunkPlacer(3, 1, 2, UniformInt.of(4, 4), block.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), BlockStateProvider.simple(NorthstarBlocks.PLATE_FUNGUS_CAP_BLOCK.get())),
                        BlockStateProvider.simple(NorthstarBlocks.WILTER_LEAVES.get()),
                        new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                        new TwoLayersFeatureSize(2, 0, 2)).build()));

        context.register(ROOF_PLATE_FUNGUS, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(NorthstarBlocks.PLATE_FUNGUS_STEM_BLOCK.get()),
                        new RoofPlateTrunkPlacer(3, 1, 2, UniformInt.of(4, 4), block.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), BlockStateProvider.simple(NorthstarBlocks.PLATE_FUNGUS_CAP_BLOCK.get())),
                        BlockStateProvider.simple(NorthstarBlocks.WILTER_LEAVES.get()),
                        new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                        new TwoLayersFeatureSize(2, 0, 2)).build()));

        context.register(TOWER_FUNGUS, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(NorthstarBlocks.TOWER_FUNGUS_STEM_BLOCK.get()),
                        new TowerTrunkPlacer(8, 1, 4, UniformInt.of(4, 4), block.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), BlockStateProvider.simple(NorthstarBlocks.TOWER_FUNGUS_CAP_BLOCK.get())),
                        BlockStateProvider.simple(NorthstarBlocks.WILTER_LEAVES.get()),
                        new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                        new TwoLayersFeatureSize(2, 0, 2)).build()));

        context.register(ROOF_TOWER_FUNGUS, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(NorthstarBlocks.TOWER_FUNGUS_STEM_BLOCK.get()),
                        new RoofTowerTrunkPlacer(8, 1, 4, UniformInt.of(4, 4), block.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), BlockStateProvider.simple(NorthstarBlocks.TOWER_FUNGUS_CAP_BLOCK.get())),
                        BlockStateProvider.simple(NorthstarBlocks.WILTER_LEAVES.get()),
                        new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                        new TwoLayersFeatureSize(2, 0, 2)).build()));

        context.register(ARGYRE_CEILING, new ConfiguredFeature<>((Feature<AlienTreeConfig>) NorthstarFeatures.NATURAL_ARGYRE.get(),
                new AlienTreeConfig.AlienTreeConfigBuilder(
                        BlockStateProvider.simple(NorthstarBlocks.ARGYRE_LOG.get()),
                        BlockStateProvider.simple(Blocks.GLOWSTONE),
                        new ArgyreCeilingTrunkPlacer(32, 24, 20, UniformInt.of(4, 4), 0.5F, UniformInt.of(0, 1), block.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), UniformInt.of(1, 1)),
                        BlockStateProvider.simple(NorthstarBlocks.ARGYRE_LEAVES.get()),
                        new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                        new TwoLayersFeatureSize(2, 0, 2)).build()));

        context.register(ARGYRE_SMALL, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(NorthstarBlocks.ARGYRE_LOG.get()),
                        new ArgyreSaplingTrunkPlacer(12, 1, 10, UniformInt.of(4, 4), 0.5F, UniformInt.of(0, 1), block.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), UniformInt.of(1, 1)),
                        BlockStateProvider.simple(NorthstarBlocks.ARGYRE_LEAVES.get()),
                        new CoilerFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0)),
                        new TwoLayersFeatureSize(3, 0, 2)).build()));

        context.register(CALORIAN_VINES, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(NorthstarBlocks.CALORIAN_LOG.get()),
                        new TestSaplingTrunkPlacer(90, 2, 12, UniformInt.of(4, 4), 0.5F, UniformInt.of(0, 1), block.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), UniformInt.of(-1, 1)),
                        BlockStateProvider.simple(NorthstarBlocks.WILTER_LEAVES.get()),
                        new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                        new TwoLayersFeatureSize(2, 0, 2)).build()));

        context.register(MERCURY_CACTUS, new ConfiguredFeature<>((MercuryCactusFeature) NorthstarFeatures.MERCURY_CACTUS.get(), new NoneFeatureConfiguration()));

        context.register(CRIMSITE_COLUMN, new ConfiguredFeature<>((Feature<StoneColumnConfiguration>) NorthstarFeatures.STONE_COLUMN.get(),
                new StoneColumnConfiguration(36, UniformInt.of(3, 6), UniformFloat.of(0.4F, 2.0F), 0.45F, UniformFloat.of(0.3F, 0.9F),
                        UniformFloat.of(0.4F, 1.0F), UniformFloat.of(0.0F, 0.3F), 4, 0.6F, BlockStateProvider.simple(AllPaletteStoneTypes.CRIMSITE.baseBlock.get().defaultBlockState()))));

        context.register(GLOWSTONE_BRANCH, new ConfiguredFeature<>((Feature<GlowstoneBranchConfig>) NorthstarFeatures.GLOWSTONE_BRANCH.get(),
                new GlowstoneBranchConfig(BlockStateProvider.simple(Blocks.GLOWSTONE.defaultBlockState()))));

        context.register(GLOWSTONE_UPSIDE_DOWN_BRANCH, new ConfiguredFeature<>((Feature<GlowstoneBranchConfig>) NorthstarFeatures.GLOWSTONE_UPSIDE_DOWN_BRANCH.get(),
                new GlowstoneBranchConfig(BlockStateProvider.simple(Blocks.GLOWSTONE.defaultBlockState()))));
    }

}