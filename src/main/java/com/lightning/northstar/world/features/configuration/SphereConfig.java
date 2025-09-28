package com.lightning.northstar.world.features.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record SphereConfig(GeodeBlockSettings geodeBlockSettings,
                           GeodeLayerSettings geodeLayerSettings,
                           GeodeCrackSettings geodeCrackSettings,
                           double usePotentialPlacementsChance,
                           double useAlternateLayer0Chance,
                           boolean placementsRequireLayer0Alternate,
                           IntProvider outerWallDistance,
                           IntProvider distributionPoints,
                           IntProvider pointOffset,
                           int minGenOffset,
                           int maxGenOffset,
                           double noiseMultiplier,
                           int invalidBlocksThreshold) implements FeatureConfiguration {

    public static final Codec<Double> CHANCE_RANGE = Codec.doubleRange(0.0D, 1.0D);
    public static final Codec<SphereConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
            GeodeBlockSettings.CODEC.fieldOf("blocks").forGetter(SphereConfig::geodeBlockSettings),
            GeodeLayerSettings.CODEC.fieldOf("layers").forGetter(SphereConfig::geodeLayerSettings),
            GeodeCrackSettings.CODEC.fieldOf("crack").forGetter(SphereConfig::geodeCrackSettings),
            CHANCE_RANGE.fieldOf("use_potential_placements_chance").orElse(0.35D).forGetter(SphereConfig::usePotentialPlacementsChance),
            CHANCE_RANGE.fieldOf("use_alternate_layer0_chance").orElse(0.0D).forGetter(SphereConfig::useAlternateLayer0Chance),
            Codec.BOOL.fieldOf("placements_require_layer0_alternate").orElse(true).forGetter(SphereConfig::placementsRequireLayer0Alternate),
            IntProvider.codec(1, 256).fieldOf("outer_wall_distance").orElse(UniformInt.of(4, 5)).forGetter(SphereConfig::outerWallDistance),
            IntProvider.codec(1, 20).fieldOf("distribution_points").orElse(UniformInt.of(3, 4)).forGetter(SphereConfig::distributionPoints),
            IntProvider.codec(0, 10).fieldOf("point_offset").orElse(UniformInt.of(1, 2)).forGetter(SphereConfig::pointOffset),
            Codec.INT.fieldOf("min_gen_offset").orElse(-16).forGetter(SphereConfig::minGenOffset),
            Codec.INT.fieldOf("max_gen_offset").orElse(16).forGetter(SphereConfig::maxGenOffset),
            CHANCE_RANGE.fieldOf("noise_multiplier").orElse(0.05D).forGetter(SphereConfig::noiseMultiplier),
            Codec.INT.fieldOf("invalid_blocks_threshold").forGetter(SphereConfig::invalidBlocksThreshold)
    ).apply(i, SphereConfig::new));

}
