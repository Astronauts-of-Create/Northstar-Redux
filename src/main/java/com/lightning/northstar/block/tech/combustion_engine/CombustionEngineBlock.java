package com.lightning.northstar.block.tech.combustion_engine;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CombustionEngineBlock extends HorizontalKineticBlock implements IBE<CombustionEngineBlockEntity> {

    protected static final VoxelShape SHAPE_AXIS_X = Block.box(0, 0, 2, 16, 13, 14);
    protected static final VoxelShape SHAPE_AXIS_Z = Block.box(2, 0, 0, 14, 13, 16);

    public CombustionEngineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HORIZONTAL_FACING).getAxis() == Axis.X ? SHAPE_AXIS_X : SHAPE_AXIS_Z;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        Player player = context.getPlayer();
        return this.defaultBlockState()
                .setValue(HORIZONTAL_FACING, player != null && player.isCrouching() ? direction : direction.getOpposite());
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return state.getValue(HORIZONTAL_FACING).getOpposite() == face;
    }

    @Override
    public Class<CombustionEngineBlockEntity> getBlockEntityClass() {
        return CombustionEngineBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CombustionEngineBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.COMBUSTION_ENGINE.get();
    }

}
