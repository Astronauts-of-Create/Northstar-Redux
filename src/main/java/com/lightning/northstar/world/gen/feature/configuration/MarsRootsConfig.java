package com.lightning.northstar.world.gen.feature.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/** {@link net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration} */
public record MarsRootsConfig(Holder<PlacedFeature> treeFeature,
                              int requiredVerticalSpaceForTree,
                              int rootRadius,
                              TagKey<Block> rootReplaceable,
                              BlockStateProvider rootStateProvider,
                              int rootPlacementAttempts,
                              int rootColumnMaxHeight,
                              int hangingRootRadius,
                              int hangingRootsVerticalSpan,
                              BlockStateProvider hangingRootStateProvider,
                              int hangingRootPlacementAttempts,
                              int allowedVerticalWaterForTree,
                              BlockPredicate allowedTreePosition) implements FeatureConfiguration {

    public static final Codec<MarsRootsConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
            PlacedFeature.CODEC.fieldOf("feature").forGetter(MarsRootsConfig::treeFeature),
            Codec.intRange(1, 64).fieldOf("required_vertical_space_for_tree").forGetter(MarsRootsConfig::requiredVerticalSpaceForTree),
            Codec.intRange(1, 64).fieldOf("root_radius").forGetter(MarsRootsConfig::rootRadius),
            TagKey.hashedCodec(Registries.BLOCK).fieldOf("root_replaceable").forGetter(MarsRootsConfig::rootReplaceable),
            BlockStateProvider.CODEC.fieldOf("root_state_provider").forGetter(MarsRootsConfig::rootStateProvider),
            Codec.intRange(1, 256).fieldOf("root_placement_attempts").forGetter(MarsRootsConfig::rootPlacementAttempts),
            Codec.intRange(1, 4096).fieldOf("root_column_max_height").forGetter(MarsRootsConfig::rootColumnMaxHeight),
            Codec.intRange(1, 64).fieldOf("hanging_root_radius").forGetter(MarsRootsConfig::hangingRootRadius),
            Codec.intRange(0, 16).fieldOf("hanging_roots_vertical_span").forGetter(MarsRootsConfig::hangingRootsVerticalSpan),
            BlockStateProvider.CODEC.fieldOf("hanging_root_state_provider").forGetter(MarsRootsConfig::hangingRootStateProvider),
            Codec.intRange(1, 256).fieldOf("hanging_root_placement_attempts").forGetter(MarsRootsConfig::hangingRootPlacementAttempts),
            Codec.intRange(1, 64).fieldOf("allowed_vertical_water_for_tree").forGetter(MarsRootsConfig::allowedVerticalWaterForTree),
            BlockPredicate.CODEC.fieldOf("allowed_tree_position").forGetter(MarsRootsConfig::allowedTreePosition)
    ).apply(i, MarsRootsConfig::new));

}
