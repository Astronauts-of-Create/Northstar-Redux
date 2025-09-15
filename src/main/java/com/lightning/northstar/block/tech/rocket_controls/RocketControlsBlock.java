package com.lightning.northstar.block.tech.rocket_controls;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RocketControlsBlock extends HorizontalDirectionalBlock implements IWrenchable, IBE<RocketControlsBlockEntity> {

    public static final MapCodec<RocketControlsBlock> CODEC = simpleCodec(RocketControlsBlock::new);

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 20.0D, 16.0D);

    public RocketControlsBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = defaultBlockState();
        Direction horizontalDirection = pContext.getHorizontalDirection();

        state = state.setValue(FACING, horizontalDirection.getOpposite());
        return state;
    }

    @Override
    public Class<RocketControlsBlockEntity> getBlockEntityClass() {
        return RocketControlsBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RocketControlsBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.ROCKET_CONTROLS.get();
    }

}
