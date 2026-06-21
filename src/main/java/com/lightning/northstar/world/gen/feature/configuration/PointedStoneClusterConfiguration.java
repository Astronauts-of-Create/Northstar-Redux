package com.lightning.northstar.world.gen.feature.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/** {@link PointedDripstoneConfiguration} */
public record PointedStoneClusterConfiguration(
        float chanceOfTallerDripstone,
        float chanceOfDirectionalSpread,
        float chanceOfSpreadRadius2,
        float chanceOfSpreadRadius3,
        BlockStateProvider stoneProvider,
        BlockStateProvider dripstoneBlock
) implements FeatureConfiguration {

    public static final Codec<PointedStoneClusterConfiguration> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_taller_dripstone").orElse(0.2F).forGetter(PointedStoneClusterConfiguration::chanceOfTallerDripstone),
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_directional_spread").orElse(0.7F).forGetter(PointedStoneClusterConfiguration::chanceOfDirectionalSpread),
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spread_radius2").orElse(0.5F).forGetter(PointedStoneClusterConfiguration::chanceOfSpreadRadius2),
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spread_radius3").orElse(0.5F).forGetter(PointedStoneClusterConfiguration::chanceOfSpreadRadius3),
            BlockStateProvider.CODEC.fieldOf("stone_provider").forGetter(PointedStoneClusterConfiguration::stoneProvider),
            BlockStateProvider.CODEC.fieldOf("base_stone_provider").forGetter(PointedStoneClusterConfiguration::dripstoneBlock)
    ).apply(i, PointedStoneClusterConfiguration::new));

}
