package com.lightning.northstar.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.createmod.catnip.data.Iterate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.FluidState;

import java.util.List;
import java.util.function.Predicate;

public class SphereFeature extends Feature<GeodeConfiguration> {

    public SphereFeature(Codec<GeodeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<GeodeConfiguration> context) {
        GeodeConfiguration config = context.config();
        RandomSource randomsource = context.random();
        BlockPos blockPos = context.origin();
        WorldGenLevel worldGenLevel = context.level();
        int i = config.minGenOffset;
        int j = config.maxGenOffset;
        List<Pair<BlockPos, Integer>> list = Lists.newLinkedList();
        int k = config.distributionPoints.sample(randomsource);
        WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(worldGenLevel.getSeed()));
        NormalNoise normalNoise = NormalNoise.create(worldgenRandom, -4, 1.0);
        List<BlockPos> list2 = Lists.newLinkedList();
        double d = (double) k / config.outerWallDistance.getMaxValue();
        GeodeLayerSettings geodeLayerSettings = config.geodeLayerSettings;
        GeodeBlockSettings geodeBlockSettings = config.geodeBlockSettings;
        GeodeCrackSettings geodeCrackSettings = config.geodeCrackSettings;
        double e = 1.0 / Math.sqrt(geodeLayerSettings.filling);
        double f = 1.0 / Math.sqrt(geodeLayerSettings.innerLayer + d);
        double g = 1.0 / Math.sqrt(geodeLayerSettings.middleLayer + d);
        double h = 1.0 / Math.sqrt(geodeLayerSettings.outerLayer + d);
        double l = 1.0 / Math.sqrt(geodeCrackSettings.baseCrackSize + randomsource.nextDouble() / 2.0 + (k > 3 ? d : 0.0));
        boolean bl = randomsource.nextFloat() < geodeCrackSettings.generateCrackChance;
        int m = 0;

        for (int n = 0; n < k; n++) {
            int o = config.outerWallDistance.sample(randomsource);
            int p = config.outerWallDistance.sample(randomsource);
            int q = config.outerWallDistance.sample(randomsource);
            BlockPos blockPos2 = blockPos.offset(o, p, q);
            BlockState blockState = worldGenLevel.getBlockState(blockPos2);
            if (blockState.isAir() || blockState.is(BlockTags.GEODE_INVALID_BLOCKS)) {
                if (++m > config.invalidBlocksThreshold) {
                    return false;
                }
            }

            list.add(Pair.of(blockPos2, config.pointOffset.sample(randomsource)));
        }

        if (bl) {
            int n = randomsource.nextInt(4);
            int o = k * 2 + 1;
            if (n == 0) {
                list2.add(blockPos.offset(o, 7, 0));
                list2.add(blockPos.offset(o, 5, 0));
                list2.add(blockPos.offset(o, 1, 0));
            } else if (n == 1) {
                list2.add(blockPos.offset(0, 7, o));
                list2.add(blockPos.offset(0, 5, o));
                list2.add(blockPos.offset(0, 1, o));
            } else if (n == 2) {
                list2.add(blockPos.offset(o, 7, o));
                list2.add(blockPos.offset(o, 5, o));
                list2.add(blockPos.offset(o, 1, o));
            } else {
                list2.add(blockPos.offset(0, 7, 0));
                list2.add(blockPos.offset(0, 5, 0));
                list2.add(blockPos.offset(0, 1, 0));
            }
        }

        List<BlockPos> list3 = Lists.newArrayList();
        Predicate<BlockState> predicate = isReplaceable(config.geodeBlockSettings.cannotReplace);

        for (BlockPos blockPos3 : BlockPos.betweenClosed(blockPos.offset(i, i, i), blockPos.offset(j, j, j))) {
            double r = normalNoise.getValue(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ()) * config.noiseMultiplier;
            double s = 0.0;
            double t = 0.0;

            for (Pair<BlockPos, Integer> pair : list) {
                s += Mth.invSqrt(blockPos3.distSqr(pair.getFirst()) + pair.getSecond()) + r;
            }

            for (BlockPos blockPos4 : list2) {
                t += Mth.invSqrt(blockPos3.distSqr(blockPos4) + geodeCrackSettings.crackPointOffset) + r;
            }

            if (!(s < h)) {
                if (bl && t >= l && s < e) {
                    this.safeSetBlock(worldGenLevel, blockPos3, Blocks.AIR.defaultBlockState(), predicate);

                    for (Direction direction : Iterate.directions) {
                        BlockPos blockPos5 = blockPos3.relative(direction);
                        FluidState fluidState = worldGenLevel.getFluidState(blockPos5);
                        if (!fluidState.isEmpty()) {
                            worldGenLevel.scheduleTick(blockPos5, fluidState.getType(), 0);
                        }
                    }
                } else if (s >= e) {
                    this.safeSetBlock(worldGenLevel, blockPos3, geodeBlockSettings.fillingProvider.getState(randomsource, blockPos3), predicate);
                } else if (s >= f) {
                    boolean bl2 = randomsource.nextFloat() < config.useAlternateLayer0Chance;
                    if (bl2) {
                        this.safeSetBlock(worldGenLevel, blockPos3, geodeBlockSettings.alternateInnerLayerProvider.getState(randomsource, blockPos3), predicate);
                    } else {
                        this.safeSetBlock(worldGenLevel, blockPos3, geodeBlockSettings.innerLayerProvider.getState(randomsource, blockPos3), predicate);
                    }

                    if ((!config.placementsRequireLayer0Alternate || bl2) && randomsource.nextFloat() < config.usePotentialPlacementsChance) {
                        list3.add(blockPos3.immutable());
                    }
                } else if (s >= g) {
                    this.safeSetBlock(worldGenLevel, blockPos3, geodeBlockSettings.middleLayerProvider.getState(randomsource, blockPos3), predicate);
                } else if (s >= h) {
                    this.safeSetBlock(worldGenLevel, blockPos3, geodeBlockSettings.outerLayerProvider.getState(randomsource, blockPos3), predicate);
                }
            }
        }

        List<BlockState> list4 = geodeBlockSettings.innerPlacements;

        for (BlockPos blockPos2 : list3) {
            BlockState blockState = Util.getRandom(list4, randomsource);

            for (Direction direction2 : Iterate.directions) {
                if (blockState.hasProperty(BlockStateProperties.FACING)) {
                    blockState = blockState.setValue(BlockStateProperties.FACING, direction2);
                }

                BlockPos blockPos6 = blockPos2.relative(direction2);
                BlockState blockState2 = worldGenLevel.getBlockState(blockPos6);
                if (blockState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                    blockState = blockState.setValue(BlockStateProperties.WATERLOGGED, blockState2.getFluidState().isSource());
                }

                // only difference with vanilla geodes
                // if (BuddingAmethystBlock.canClusterGrowAtState(blockState2)) {
                //    this.safeSetBlock(worldGenLevel, blockPos6, blockState, predicate);
                //    break;
                //}
            }
        }

        return true;
    }

}
