package com.lightning.northstar.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;

import java.util.ArrayList;
import java.util.List;

// Modified version of MultifaceGrowthFeature without isAirOrWater checks and custom placement radius in placeGrowthIfPossible
public class MultifaceGrowthCustomFeature extends Feature<MultifaceGrowthConfiguration> {

    public MultifaceGrowthCustomFeature(Codec<MultifaceGrowthConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<MultifaceGrowthConfiguration> context) {
        WorldGenLevel worldGenLevel = context.level();
        BlockPos blockPos = context.origin();
        RandomSource randomSource = context.random();
        MultifaceGrowthConfiguration multifaceGrowthConfiguration = context.config();
        List<Direction> list = multifaceGrowthConfiguration.getShuffledDirections(randomSource);
        if (placeGrowthIfPossible(worldGenLevel, blockPos, worldGenLevel.getBlockState(blockPos), multifaceGrowthConfiguration, randomSource, list)) {
            return true;
        }

        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();

        for (Direction direction : list) {
            mutableBlockPos.set(blockPos);
            List<Direction> list1 = multifaceGrowthConfiguration.getShuffledDirectionsExcept(randomSource, direction.getOpposite());

            for (int i = 0; i < multifaceGrowthConfiguration.searchRange; ++i) {
                mutableBlockPos.setWithOffset(blockPos, direction);
                BlockState blockState = worldGenLevel.getBlockState(mutableBlockPos);
                if (!blockState.is(multifaceGrowthConfiguration.placeBlock)) {
                    break;
                }

                if (placeGrowthIfPossible(worldGenLevel, mutableBlockPos, blockState, multifaceGrowthConfiguration, randomSource, list1)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean placeGrowthIfPossible(WorldGenLevel level, BlockPos pos, BlockState state, MultifaceGrowthConfiguration config, RandomSource random, List<Direction> directions) {
        BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();

        int radius = random.nextIntBetweenInclusive(3, 7);

        for (BlockPos blockpos1 : BlockPos.betweenClosed(pos.offset(-radius, -radius / 2, -radius), pos.offset(radius, radius / 2, radius))) {
            int difX = blockpos1.getX() - pos.getX();
            int difZ = blockpos1.getZ() - pos.getZ();
            List<Direction> dirs = new ArrayList<>();
            if (difX * difX + difZ * difZ <= radius * radius - 0.1) {
                for (Direction direction : Direction.values()) {
                    BlockState blockstate = level.getBlockState(mutableBlockPos.setWithOffset(blockpos1, direction));
                    if (blockstate.is(config.canBePlacedOn)) {
                        BlockState blockstate1 = config.placeBlock.getStateForPlacement(state, level, blockpos1, direction);
                        // BlockState blockstate1 = NorthstarBlocks.MARS_ROOTS.get().defaultBlockState();

                        if (blockstate1 == null) {
                            continue;
                        }
                        dirs.add(direction);
                        // blockstate1.randomTick(level.getLevel(), blockpos1, rando);

                    }
                }

                BlockState blockstate = config.placeBlock.defaultBlockState();
                for (Direction direction2 : dirs) {
                    blockstate = blockstate.setValue(MultifaceBlock.getFaceProperty(direction2), true);
                }
                if (level.getBlockState(blockpos1).isAir()) {
                    level.setBlock(blockpos1, blockstate, 8);
                }
                level.getChunk(blockpos1).markPosForPostprocessing(blockpos1);

            }
        }


        return true;
    }
}