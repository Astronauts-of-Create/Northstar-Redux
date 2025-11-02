package com.lightning.northstar.block.simple;

import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.core.BlockPos;
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
import org.jetbrains.annotations.Nullable;

public class ClusterBlock extends AmethystBlock implements ProperWaterloggedBlock {


    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    protected final VoxelShape northAabb;
    protected final VoxelShape southAabb;
    protected final VoxelShape eastAabb;
    protected final VoxelShape westAabb;
    protected final VoxelShape upAabb;
    protected final VoxelShape downAabb;

    public ClusterBlock(int size, int offset, BlockBehaviour.Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.UP)
                .setValue(WATERLOGGED, false));

        upAabb = Block.box(offset, 0, offset, (16 - offset), size, (16 - offset));
        downAabb = Block.box(offset, (16 - size), offset, (16 - offset), 16, (16 - offset));
        northAabb = Block.box(offset, offset, (16 - size), (16 - offset), (16 - offset), 16);
        southAabb = Block.box(offset, offset, 0, (16 - offset), (16 - offset), size);
        eastAabb = Block.box(0, offset, offset, size, (16 - offset), (16 - offset));
        westAabb = Block.box((16 - size), offset, offset, 16, (16 - offset), (16 - offset));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, WATERLOGGED));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> northAabb;
            case SOUTH -> southAabb;
            case EAST -> eastAabb;
            case WEST -> westAabb;
            case DOWN -> downAabb;
            default -> upAabb;
        };
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

        return direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos) ?
                Blocks.AIR.defaultBlockState() :
                super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
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