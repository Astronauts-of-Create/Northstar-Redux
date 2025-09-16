package com.lightning.northstar.block.tech.oxygen_detector;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class OxygenDetectorBlock extends DirectionalBlock implements IBE<OxygenDetectorBlockEntity> {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public OxygenDetectorBlock(BlockBehaviour.Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.SOUTH)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, POWERED));
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
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) && state.getValue(FACING) == direction ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getPlayer().isCrouching())
            return defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
        return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    public Class<OxygenDetectorBlockEntity> getBlockEntityClass() {
        return OxygenDetectorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends OxygenDetectorBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.OXYGEN_DETECTOR.get();
    }

}
