package com.lightning.northstar.world.gen.feature.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record GlowstoneBranchConfig(
        BlockStateProvider glowProvider,
        int attempts,
        boolean downwards
) implements FeatureConfiguration {

    public static final Codec<GlowstoneBranchConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
            BlockStateProvider.CODEC.fieldOf("glow_provider").forGetter(GlowstoneBranchConfig::glowProvider),
            Codec.INT.fieldOf("attempts").forGetter(GlowstoneBranchConfig::attempts),
            Codec.BOOL.fieldOf("downwards").forGetter(GlowstoneBranchConfig::downwards)
    ).apply(i, GlowstoneBranchConfig::new));

}
