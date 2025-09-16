package com.lightning.northstar.world.features.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.FluidState;

public record StoneClusterConfiguration(int floorToCeilingSearchRange,
                                        BlockStateProvider stone_provider,
                                        FluidState fluid_provider,
                                        IntProvider height,
                                        IntProvider radius,
                                        int maxStalagmiteStalactiteHeightDiff,
                                        int heightDeviation,
                                        IntProvider dripstoneBlockLayerThickness,
                                        FloatProvider density,
                                        FloatProvider wetness,
                                        FloatProvider lavaness,
                                        FloatProvider thirdthingness,
                                        float chanceOfDripstoneColumnAtMaxDistanceFromCenter,
                                        int maxDistanceFromEdgeAffectingChanceOfDripstoneColumn,
                                        int maxDistanceFromCenterAffectingHeightBias) implements FeatureConfiguration {

    public static final Codec<StoneClusterConfiguration> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").forGetter(StoneClusterConfiguration::floorToCeilingSearchRange),
            BlockStateProvider.CODEC.fieldOf("stone_provider").forGetter(StoneClusterConfiguration::stone_provider),
            FluidState.CODEC.fieldOf("fluid_provider").forGetter(StoneClusterConfiguration::fluid_provider),
            IntProvider.codec(1, 128).fieldOf("height").forGetter(StoneClusterConfiguration::height),
            IntProvider.codec(1, 128).fieldOf("radius").forGetter(StoneClusterConfiguration::radius),
            Codec.intRange(0, 64).fieldOf("max_stalagmite_stalactite_height_diff").forGetter(StoneClusterConfiguration::maxStalagmiteStalactiteHeightDiff),
            Codec.intRange(1, 64).fieldOf("height_deviation").forGetter(StoneClusterConfiguration::heightDeviation),
            IntProvider.codec(0, 128).fieldOf("dripstone_block_layer_thickness").forGetter(StoneClusterConfiguration::dripstoneBlockLayerThickness),
            FloatProvider.codec(0.0F, 2.0F).fieldOf("density").forGetter(StoneClusterConfiguration::density),
            FloatProvider.codec(0.0F, 2.0F).fieldOf("wetness").forGetter(StoneClusterConfiguration::wetness),
            FloatProvider.codec(0.0F, 2.0F).fieldOf("lavaness").forGetter(StoneClusterConfiguration::lavaness),
            FloatProvider.codec(0.0F, 2.0F).fieldOf("thirdthingness").forGetter(StoneClusterConfiguration::thirdthingness),
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_dripstone_column_at_max_distance_from_center").forGetter(StoneClusterConfiguration::chanceOfDripstoneColumnAtMaxDistanceFromCenter),
            Codec.intRange(1, 64).fieldOf("max_distance_from_edge_affecting_chance_of_dripstone_column").forGetter(StoneClusterConfiguration::maxDistanceFromEdgeAffectingChanceOfDripstoneColumn),
            Codec.intRange(1, 64).fieldOf("max_distance_from_center_affecting_height_bias").forGetter(StoneClusterConfiguration::maxDistanceFromCenterAffectingHeightBias)
    ).apply(i, StoneClusterConfiguration::new));

}
