package com.lightning.northstar.world.gen.feature.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record NestConfig(IntProvider radius,
                         BlockStateProvider blockProvider,
                         BlockStateProvider nestProvider,
                         BlockStateProvider groundProvider) implements FeatureConfiguration {

    public static final Codec<NestConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
            IntProvider.codec(1, 128).fieldOf("radius").forGetter(NestConfig::radius),
            BlockStateProvider.CODEC.fieldOf("block_provider").forGetter(NestConfig::blockProvider),
            BlockStateProvider.CODEC.fieldOf("nest_provider").forGetter(NestConfig::nestProvider),
            BlockStateProvider.CODEC.fieldOf("ground_provider").forGetter(NestConfig::groundProvider)
    ).apply(i, NestConfig::new));

}
