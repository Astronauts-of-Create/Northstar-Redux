package com.lightning.northstar.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;

public class LaserLenseBlock extends TransparentBlock {

    public LaserLenseBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        LaserBlock.updateColumn(pLevel, pPos, pState);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        pLevel.scheduleTick(pPos, this, 1);
    }

    public void onRemove(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        tick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        pLevel.scheduleTick(pCurrentPos, this, 1);
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

}
