package com.lightning.northstar.block.simple;

import net.createmod.catnip.data.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BuddingCrystalBlock extends AmethystBlock {

    private Block small;
    private Block medium;
    private Block large;
    private Block cluster;

    public BuddingCrystalBlock(BlockBehaviour.Properties properties, Block small, Block medium, Block large, Block cluster) {
        super(properties);
        this.small = small;
        this.medium = medium;
        this.large = large;
        this.cluster = cluster;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(BuddingAmethystBlock.GROWTH_CHANCE) == 0) {
            Direction direction = Iterate.directions[random.nextInt(Iterate.directions.length)];
            BlockPos blockpos = pos.relative(direction);
            BlockState blockstate = level.getBlockState(blockpos);
            Block block = null;
            if (BuddingAmethystBlock.canClusterGrowAtState(blockstate)) {
                block = small;
            } else if (blockstate.is(small) && blockstate.getValue(AmethystClusterBlock.FACING) == direction) {
                block = medium;
            } else if (blockstate.is(medium) && blockstate.getValue(AmethystClusterBlock.FACING) == direction) {
                block = large;
            } else if (blockstate.is(large) && blockstate.getValue(AmethystClusterBlock.FACING) == direction) {
                block = cluster;
            }

            if (block != null) {
                level.setBlockAndUpdate(blockpos, block.defaultBlockState()
                        .setValue(AmethystClusterBlock.FACING, direction)
                        .setValue(AmethystClusterBlock.WATERLOGGED, blockstate.getFluidState().getType() == Fluids.WATER));
            }
        }
    }

}
