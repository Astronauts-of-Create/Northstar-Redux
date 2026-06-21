package com.lightning.northstar.content.world.planet.mercury;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.content.world.NorthstarFeatures;
import com.lightning.northstar.world.gen.feature.configuration.CraterConfig;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class MercuryConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>>
            CACTUS = key("cactus"),
            CRATER = key("crater"),
            CRATER_BIG = key("crater_big"),
            LARGE_SHELVES = key("large_shelves"),
            SMALL_SHELVES = key("small_shelves");

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String path) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("mercury_" + path));
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<Block> block = context.lookup(Registries.BLOCK);

        context.register(
                CACTUS,
                new ConfiguredFeature<>(
                        NorthstarFeatures.MERCURY_CACTUS.get(),
                        new NoneFeatureConfiguration()
                )
        );

        context.register(
                CRATER,
                new ConfiguredFeature<>(
                        NorthstarFeatures.CRATER.get(),
                        new CraterConfig(
                                UniformInt.of(6, 12),
                                ConstantInt.of(2),
                                UniformInt.of(2, 3),
                                BlockStateProvider.simple(NorthstarBlocks.MERCURY_STONE.get()),
                                BlockStateProvider.simple(Blocks.AIR),
                                block.getOrThrow(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
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
                                BlockStateProvider.simple(NorthstarBlocks.MERCURY_STONE.get()),
                                BlockStateProvider.simple(Blocks.AIR),
                                block.getOrThrow(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
                        )
                )
        );

        context.register(
                LARGE_SHELVES,
                new ConfiguredFeature<>(
                        NorthstarFeatures.MERCURY_LARGE_SHELVES.get(),
                        new NoneFeatureConfiguration()
                )
        );

        context.register(
                SMALL_SHELVES,
                new ConfiguredFeature<>(
                        NorthstarFeatures.MERCURY_SMALL_SHELVES.get(),
                        new NoneFeatureConfiguration()
                )
        );
    }

}
