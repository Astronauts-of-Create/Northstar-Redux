package com.lightning.northstar.world.gen.feature.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record CraterConfig(IntProvider radius,
                           IntProvider halfHeight,
                           IntProvider depth,
                           BlockStateProvider blockProvider,
                           BlockStateProvider airProvider,
                           HolderSet<Block> canDelete) implements FeatureConfiguration {

    public static final Codec<CraterConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
            IntProvider.codec(1, 128).fieldOf("radius").forGetter(CraterConfig::radius),
            IntProvider.codec(1, 128).fieldOf("half_height").forGetter(CraterConfig::halfHeight),
            IntProvider.codec(1, 128).fieldOf("depth").forGetter(CraterConfig::depth),
            BlockStateProvider.CODEC.fieldOf("block_provider").forGetter(CraterConfig::blockProvider),
            BlockStateProvider.CODEC.fieldOf("air_provider").forGetter(CraterConfig::airProvider),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_delete").forGetter(CraterConfig::canDelete)
    ).apply(i, CraterConfig::new));

}
