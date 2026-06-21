package com.lightning.northstar.content.world.planet.moon;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.content.world.NorthstarFeatures;
import com.lightning.northstar.world.gen.feature.configuration.CraterConfig;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.List;

public class MoonConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>>
            CRATER = key("crater"),
            CRATER_BIG = key("crater_big"),
            LUNAR_SAPPHIRE_GEODE = key("lunar_sapphire_geode");

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String path) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("moon_" + path));
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);

        context.register(
                CRATER,
                new ConfiguredFeature<>(
                        NorthstarFeatures.CRATER.get(),
                        new CraterConfig(
                                UniformInt.of(6, 12),
                                ConstantInt.of(2),
                                UniformInt.of(2, 3),
                                BlockStateProvider.simple(NorthstarBlocks.MOON_SAND.get()),
                                BlockStateProvider.simple(Blocks.AIR),
                                blocks.getOrThrow(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
                        )
                )
        );

        context.register(
                CRATER_BIG,
                new ConfiguredFeature<>(
                        NorthstarFeatures.CRATER.get(),
                        new CraterConfig(
                                UniformInt.of(12, 24),
                                ConstantInt.of(2),
                                UniformInt.of(3, 6),
                                BlockStateProvider.simple(NorthstarBlocks.MOON_SAND.get()),
                                BlockStateProvider.simple(Blocks.AIR),
                                blocks.getOrThrow(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
                        )
                )
        );

        context.register(
                LUNAR_SAPPHIRE_GEODE,
                new ConfiguredFeature<>(
                        Feature.GEODE,
                        new GeodeConfiguration(
                                new GeodeBlockSettings(
                                        BlockStateProvider.simple(Blocks.AIR),
                                        BlockStateProvider.simple(NorthstarBlocks.LUNAR_SAPPHIRE_BLOCK.get()),
                                        BlockStateProvider.simple(NorthstarBlocks.BUDDING_LUNAR_SAPPHIRE_BLOCK.get()),
                                        BlockStateProvider.simple(Blocks.CALCITE),
                                        BlockStateProvider.simple(Blocks.SMOOTH_BASALT),
                                        List.of(
                                                NorthstarBlocks.SMALL_LUNAR_SAPPHIRE_BUD.getDefaultState(),
                                                NorthstarBlocks.MEDIUM_LUNAR_SAPPHIRE_BUD.getDefaultState(),
                                                NorthstarBlocks.LARGE_LUNAR_SAPPHIRE_BUD.getDefaultState(),
                                                NorthstarBlocks.LUNAR_SAPPHIRE_CLUSTER.getDefaultState()
                                        ),
                                        BlockTags.FEATURES_CANNOT_REPLACE,
                                        BlockTags.GEODE_INVALID_BLOCKS
                                ),
                                new GeodeLayerSettings(
                                        1.7,
                                        2,
                                        2.7,
                                        3.6
                                ),
                                new GeodeCrackSettings(
                                        0.95,
                                        1,
                                        2
                                ),
                                0.35,
                                0.083,
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
    }

}
