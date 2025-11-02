package com.lightning.northstar.block.simple;

import com.google.common.collect.Maps;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class CrystalBlock extends Block implements ProperWaterloggedBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final int OFFSET = 4;
    private static final int SIZE = 7;
    public static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(Map.of(
            Direction.UP, Block.box(OFFSET, 0, OFFSET, 16 - OFFSET, SIZE, 16 - OFFSET),
            Direction.DOWN, Block.box(OFFSET, 16 - SIZE, OFFSET, 16 - OFFSET, 16, 16 - OFFSET),
            Direction.NORTH, Block.box(OFFSET, OFFSET, 16 - SIZE, 16 - OFFSET, 16 - OFFSET, 16),
            Direction.SOUTH, Block.box(OFFSET, OFFSET, 0, 16 - OFFSET, 16 - OFFSET, SIZE),
            Direction.EAST, Block.box(0, OFFSET, OFFSET, SIZE, 16 - OFFSET, 16 - OFFSET),
            Direction.WEST, Block.box(16 - SIZE, OFFSET, OFFSET, 16, 16 - OFFSET, 16 - OFFSET)));

    public CrystalBlock(BlockBehaviour.Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.UP)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, WATERLOGGED));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockpos = pos.relative(direction.getOpposite());
        return level.getBlockState(blockpos).isFaceSturdy(level, blockpos, direction);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        updateWater(level, state, pos);

        MutableBlockPos neighbor = new MutableBlockPos();
        for (Direction dir : Iterate.directions) {
            neighbor.setWithOffset(pos, dir);
            level.scheduleTick(neighbor, level.getBlockState(neighbor).getBlock(), 3);
        }

        return direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos) ?
                Blocks.AIR.defaultBlockState() :
                super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return withWater(defaultBlockState(), context).setValue(FACING, context.getClickedFace());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return fluidState(state);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

}
