package com.lightning.northstar.block.tech.oxygen_detector;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.mojang.serialization.MapCodec;
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

    public static final MapCodec<OxygenDetectorBlock> CODEC = simpleCodec(OxygenDetectorBlock::new);

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public OxygenDetectorBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(POWERED, Boolean.FALSE));
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, POWERED));
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    @Override
    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return pBlockState.getValue(POWERED) && pBlockState.getValue(FACING) == pSide ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return pBlockState.getSignal(pBlockAccess, pPos, pSide);
    }

    protected void updateNeighborsInFront(Level pLevel, BlockPos pPos, BlockState pState) {
        Direction direction = pState.getValue(FACING);
        BlockPos blockpos = pPos.relative(direction.getOpposite());
        pLevel.neighborChanged(blockpos, this, pPos);
        pLevel.updateNeighborsAtExceptFromFacing(blockpos, this, direction);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getPlayer().isCrouching())
            return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
        else
            return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, world, pos, newState, isMoving);
        IBE.onRemove(state, world, pos, newState);
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
