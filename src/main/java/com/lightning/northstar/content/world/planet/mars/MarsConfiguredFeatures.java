package com.lightning.northstar.content.world.planet.mars;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.content.world.NorthstarFeatures;
import com.lightning.northstar.world.gen.feature.StructureFeatureConfig;
import com.lightning.northstar.world.gen.feature.configuration.BlockPileConfig;
import com.lightning.northstar.world.gen.feature.configuration.CraterConfig;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class MarsConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>>
            CRATER = key("crater"),
            CRATER_SOIL = key("crater_soil"),
            DUNES_ROCK = key("dunes_rock"),
            GRAVEL_PILE = key("gravel_pile"),
            LAVA_LAKE = key("lava_lake"),
            LAVA_SPRING = key("lava_spring"),
            ROCK = key("rock"),
            ROOTS = key("roots"),
            WORM_NEST = key("worm_nest");

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String path) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("mars_" + path));
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);

        context.register(
                CRATER,
                new ConfiguredFeature<>(
                        NorthstarFeatures.CRATER.get(),
                        new CraterConfig(
                                UniformInt.of(9, 18),
                                ConstantInt.of(2),
                                UniformInt.of(4, 5),
                                BlockStateProvider.simple(NorthstarBlocks.MARS_SAND.get()),
                                BlockStateProvider.simple(Blocks.AIR),
                                blocks.getOrThrow(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
                        )
                )
        );

        context.register(
                CRATER_SOIL,
                new ConfiguredFeature<>(
                        NorthstarFeatures.CRATER.get(),
                        new CraterConfig(
                                UniformInt.of(9, 18),
                                ConstantInt.of(2),
                                UniformInt.of(4, 5),
                                BlockStateProvider.simple(NorthstarBlocks.MARS_SOIL.get()),
                                BlockStateProvider.simple(Blocks.AIR),
                                blocks.getOrThrow(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
                        )
                )
        );

        context.register(
                DUNES_ROCK,
                new ConfiguredFeature<>(
                        Feature.ICEBERG,
                        new BlockStateConfiguration(NorthstarBlocks.MARS_DEEP_STONE.getDefaultState())
                )
        );

        context.register(
                GRAVEL_PILE,
                new ConfiguredFeature<>(
                        NorthstarFeatures.BLOCK_PILE.get(),
                        new BlockPileConfig(
                                UniformInt.of(12, 24),
                                ConstantInt.of(1),
                                UniformInt.of(1, 2),
                                BlockStateProvider.simple(NorthstarBlocks.MARS_GRAVEL.get()),
                                BlockStateProvider.simple(Blocks.AIR)
                        )
                )
        );

        context.register(
                LAVA_LAKE,
                new ConfiguredFeature<>(
                        Feature.LAKE,
                        new LakeFeature.Configuration(
                                BlockStateProvider.simple(Blocks.LAVA.defaultBlockState()),
                                BlockStateProvider.simple(NorthstarBlocks.MARS_DEEP_STONE.get())
                        )
                )
        );

        context.register(
                LAVA_SPRING,
                new ConfiguredFeature<>(
                        Feature.SPRING,
                        new SpringConfiguration(
                                Fluids.LAVA.getSource(true),
                                true,
                                4,
                                1,
                                HolderSet.direct(
                                        Block::builtInRegistryHolder,
                                        Blocks.STONE,
                                        Blocks.GRANITE,
                                        Blocks.DIORITE,
                                        Blocks.ANDESITE,
                                        Blocks.DEEPSLATE,
                                        Blocks.TUFF,
                                        Blocks.CALCITE,
                                        Blocks.DIRT,
                                        NorthstarBlocks.MARS_SOIL.get(),
                                        NorthstarBlocks.MARS_STONE.get(),
                                        NorthstarBlocks.MARS_DEEP_STONE.get(),
                                        NorthstarBlocks.VOLCANIC_ASH.get(),
                                        AllPaletteStoneTypes.CRIMSITE.baseBlock.get()
                                )
                        )
                )
        );

        context.register(
                ROCK,
                new ConfiguredFeature<>(
                        NorthstarFeatures.SMALL_ROCK.get(),
                        new BlockStateConfiguration(
                                NorthstarBlocks.MARS_STONE.getDefaultState()
                        )
                )
        );

        context.register(
                ROOTS,
                new ConfiguredFeature<>(
                        NorthstarFeatures.MARS_ROOTS.get(),
                        new MultifaceGrowthConfiguration(
                                NorthstarBlocks.MARS_ROOTS.get(),
                                32,
                                false,
                                true,
                                true,
                                0.93f,
                                HolderSet.direct(
                                        Block::builtInRegistryHolder,
                                        Blocks.BASALT,
                                        NorthstarBlocks.MARS_GRAVEL.get(),
                                        NorthstarBlocks.MARS_STONE.get(),
                                        NorthstarBlocks.MARS_DEEP_STONE.get(),
                                        NorthstarBlocks.VOLCANIC_ASH.get()
                                )
                        )
                )
        );

        context.register(
                WORM_NEST,
                new ConfiguredFeature<>(
                        NorthstarFeatures.WORM_NEST.get(),
                        new StructureFeatureConfig(
                                List.of(
                                        Northstar.asResource("worm_nest/mars_worm_nest"),
                                        Northstar.asResource("worm_nest/mars_worm_nest_2"),
                                        Northstar.asResource("worm_nest/mars_worm_nest_3"),
                                        Northstar.asResource("worm_nest/mars_worm_nest_4"),
                                        Northstar.asResource("worm_nest/mars_worm_nest_big")
                                ),
                                1
                        )
                )
        );
    }

}
