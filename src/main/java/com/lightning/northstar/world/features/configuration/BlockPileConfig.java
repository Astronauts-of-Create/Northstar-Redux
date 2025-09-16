package com.lightning.northstar.world.features.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record BlockPileConfig(IntProvider radius,
                              IntProvider halfHeight,
                              IntProvider depth,
                              BlockStateProvider blockProvider,
                              BlockStateProvider airProvider) implements FeatureConfiguration {

    public static final Codec<BlockPileConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
            IntProvider.codec(1, 128).fieldOf("radius").forGetter(BlockPileConfig::radius),
            IntProvider.codec(1, 128).fieldOf("half_height").forGetter(BlockPileConfig::halfHeight),
            IntProvider.codec(1, 128).fieldOf("depth").forGetter(BlockPileConfig::depth),
            BlockStateProvider.CODEC.fieldOf("block_provider").forGetter(BlockPileConfig::blockProvider),
            BlockStateProvider.CODEC.fieldOf("air_provider").forGetter(BlockPileConfig::airProvider)
    ).apply(i, BlockPileConfig::new));

}
