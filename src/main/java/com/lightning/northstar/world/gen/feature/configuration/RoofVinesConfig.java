package com.lightning.northstar.world.gen.feature.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record RoofVinesConfig(BlockStateProvider blockProvider,
                              BlockStateProvider glowProvider,
                              IntProvider size) implements FeatureConfiguration {

    public static final Codec<RoofVinesConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
            BlockStateProvider.CODEC.fieldOf("block_provider").forGetter(RoofVinesConfig::blockProvider),
            BlockStateProvider.CODEC.fieldOf("glow_provider").forGetter(RoofVinesConfig::glowProvider),
            IntProvider.codec(1, 128).fieldOf("size").forGetter(RoofVinesConfig::size)
    ).apply(i, RoofVinesConfig::new));

}
