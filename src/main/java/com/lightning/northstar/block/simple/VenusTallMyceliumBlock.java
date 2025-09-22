package com.lightning.northstar.block.simple;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class VenusTallMyceliumBlock extends BushBlock {

    private static final MapCodec<VenusTallMyceliumBlock> CODEC = simpleCodec(VenusTallMyceliumBlock::new);

    protected static final BooleanProperty IS_ON_CEILING = BooleanProperty.create("is_on_ceiling");

    protected static final VoxelShape GROUND_SHAPE = box(2, 0, 2, 14, 13, 14);
    protected static final VoxelShape CEILING_SHAPE = box(2, 3, 2, 14, 16, 14);

    public VenusTallMyceliumBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState()
                .setValue(IS_ON_CEILING, false));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(IS_ON_CEILING));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(IS_ON_CEILING) ? CEILING_SHAPE : GROUND_SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos otherPos = state.getValue(IS_ON_CEILING) ? pos.above() : pos.below();
        Direction direction = state.getValue(IS_ON_CEILING) ? Direction.DOWN : Direction.UP;

        BlockState blockstate = level.getBlockState(otherPos);
        return blockstate.is(BlockTags.MUSHROOM_GROW_BLOCK) || blockstate.canSustainPlant(level, otherPos, direction, state).isTrue();
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.isSolidRender(pLevel, pPos);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(IS_ON_CEILING, pContext.getClickedFace() == Direction.DOWN);
    }

}
