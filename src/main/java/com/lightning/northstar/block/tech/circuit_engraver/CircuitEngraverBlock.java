package com.lightning.northstar.block.tech.circuit_engraver;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class CircuitEngraverBlock extends HorizontalKineticBlock implements IBE<CircuitEngraverBlockEntity> {

    public CircuitEngraverBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState()
                .setValue(HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferredSide = getPreferredHorizontalFacing(context);
        if (preferredSide != null)
            return defaultBlockState().setValue(HORIZONTAL_FACING, preferredSide);
        return super.getStateForPlacement(context);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public Class<CircuitEngraverBlockEntity> getBlockEntityClass() {
        return CircuitEngraverBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CircuitEngraverBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.CIRCUIT_ENGRAVER.get();
    }


}
