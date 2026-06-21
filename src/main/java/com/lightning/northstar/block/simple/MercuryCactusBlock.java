package com.lightning.northstar.block.simple;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class MercuryCactusBlock extends PipeBlock {

    public static final MapCodec<MercuryCactusBlock> CODEC = simpleCodec(MercuryCactusBlock::new);

    public MercuryCactusBlock(BlockBehaviour.Properties pProperties) {
        super(0.3125F, pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(UP, Boolean.FALSE).setValue(DOWN, Boolean.FALSE));
    }

    @Override
    protected MapCodec<? extends PipeBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.getStateForPlacement(pContext.getLevel(), pContext.getClickedPos());
    }

    public BlockState getStateForPlacement(BlockGetter pLevel, BlockPos pPos) {
        BlockState blockstate = pLevel.getBlockState(pPos.below());
        BlockState blockstate1 = pLevel.getBlockState(pPos.above());
        BlockState blockstate2 = pLevel.getBlockState(pPos.north());
        BlockState blockstate3 = pLevel.getBlockState(pPos.east());
        BlockState blockstate4 = pLevel.getBlockState(pPos.south());
        BlockState blockstate5 = pLevel.getBlockState(pPos.west());
        return this.defaultBlockState()
                .setValue(DOWN, blockstate.is(this) || blockstate.isSolidRender(pLevel, pPos.below()))
                .setValue(UP, blockstate1.is(this) || blockstate1.isSolidRender(pLevel, pPos.above()))
                .setValue(NORTH, blockstate2.is(this) || blockstate2.isSolidRender(pLevel, pPos.north()))
                .setValue(EAST, blockstate3.is(this) || blockstate3.isSolidRender(pLevel, pPos.east()))
                .setValue(SOUTH, blockstate4.is(this) || blockstate4.isSolidRender(pLevel, pPos.south()))
                .setValue(WEST, blockstate5.is(this) || blockstate5.isSolidRender(pLevel, pPos.west()));
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        BlockState newState = this.defaultBlockState();
        for (Direction direction : Direction.values()) {
            BlockPos blockpos = pCurrentPos.relative(direction);
            BlockState blockstate1 = pLevel.getBlockState(blockpos);
            if (blockstate1.is(this) || blockstate1.isSolidRender(pLevel, blockpos)) {
                newState = newState.setValue(PROPERTY_BY_DIRECTION.get(direction), true);
            }
        }
        return newState;

    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockState newState = this.updateShape(pState, Direction.UP, pState, pLevel, pPos, pPos);
        if (!pState.canSurvive(pLevel, pPos)) {
            // kind of debating whether i should keep this or not
            // pLevel.destroyBlock(pPos, true);
        }
        pLevel.setBlock(pPos, newState, 3);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.hurt(level.damageSources().cactus(), 1.0F);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockState blockstate = pLevel.getBlockState(pPos.below());
        for (Direction direction : Direction.values()) {
            BlockPos blockpos = pPos.relative(direction);
            BlockState blockstate1 = pLevel.getBlockState(blockpos);
            if (blockstate1.is(this) || blockstate1.isSolidRender(pLevel, blockpos)) {
                return true;
            }
        }

        return blockstate.is(this) || blockstate.isSolidRender(pLevel, pPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

}
