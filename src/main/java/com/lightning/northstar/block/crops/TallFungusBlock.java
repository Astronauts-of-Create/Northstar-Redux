package com.lightning.northstar.block.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TallFungusBlock extends TallFlowerBlock {

    protected static final BooleanProperty IS_ON_CEILING = BooleanProperty.create("is_on_ceiling");

    private final ResourceKey<ConfiguredFeature<?, ?>> ground;
    private final ResourceKey<ConfiguredFeature<?, ?>> ceiling;

    public TallFungusBlock(Properties properties, ResourceKey<ConfiguredFeature<?, ?>> ground, ResourceKey<ConfiguredFeature<?, ?>> ceiling) {
        super(properties);
        this.ground = ground;
        this.ceiling = ceiling;

        registerDefaultState(defaultBlockState()
                .setValue(IS_ON_CEILING, false)
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(IS_ON_CEILING));
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        if (!pState.getValue(IS_ON_CEILING)) {
            if (pState.getValue(HALF) != DoubleBlockHalf.UPPER) {
                return pLevel.getBlockState(pPos.below()).isSolidRender(pLevel, pPos.below());
            } else {
                BlockState blockstate = pLevel.getBlockState(pPos.below());
                if (pState.getBlock() != this) return this.mayPlaceOn(pState, pLevel, pPos);
                return blockstate.is(this) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
            }
        } else {
            if (pState.getValue(HALF) != DoubleBlockHalf.UPPER) {
                return pLevel.getBlockState(pPos.above()).isSolidRender(pLevel, pPos.above());
            } else {
                BlockState blockstate = pLevel.getBlockState(pPos.above());
                if (pState.getBlock() != this) return this.mayPlaceOn(pState, pLevel, pPos);
                return blockstate.is(this) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (!pState.getValue(IS_ON_CEILING)) {
            DoubleBlockHalf doubleblockhalf = pState.getValue(HALF);
            if (pFacing.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (pFacing == Direction.UP) || pFacingState.is(this) && pFacingState.getValue(HALF) != doubleblockhalf) {
                return doubleblockhalf == DoubleBlockHalf.LOWER && pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        } else {
            DoubleBlockHalf doubleblockhalf = pState.getValue(HALF);
            if (doubleblockhalf == DoubleBlockHalf.LOWER && !pLevel.getBlockState(pCurrentPos.below()).is(this)) {
                return Blocks.AIR.defaultBlockState();
            }
            if (pFacing.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (pFacing == Direction.UP) || pFacingState.is(this) && pFacingState.getValue(HALF) != doubleblockhalf) {
                return doubleblockhalf == DoubleBlockHalf.LOWER && pFacing == Direction.UP && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        boolean ceiling_flag = pContext.getClickedFace() == Direction.DOWN;
        BlockPos blockpos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        if (ceiling_flag)
            return blockpos.getY() < level.getMaxBuildHeight() && level.getBlockState(blockpos.below()).canBeReplaced(pContext) ? this.defaultBlockState().setValue(IS_ON_CEILING, ceiling_flag) : null;
        return blockpos.getY() < level.getMaxBuildHeight() && level.getBlockState(blockpos.above()).canBeReplaced(pContext) ? this.defaultBlockState().setValue(IS_ON_CEILING, ceiling_flag).setValue(HALF, DoubleBlockHalf.LOWER) : null;
    }

    public static void placeAt(LevelAccessor pLevel, BlockState pState, BlockPos pPos, int pFlags) {
        BlockPos blockpos = pPos.above();
        if (pLevel.getBlockState(blockpos).isSolidRender(pLevel, blockpos)) {
            pLevel.setBlock(pPos, copyWaterloggedFrom(pLevel, pPos, pState.setValue(HALF, DoubleBlockHalf.LOWER).setValue(IS_ON_CEILING, true)), pFlags);
            pLevel.setBlock(pPos.below(), copyWaterloggedFrom(pLevel, pPos.below(), pState.setValue(HALF, DoubleBlockHalf.UPPER).setValue(IS_ON_CEILING, true)), pFlags);
        } else {
            pLevel.setBlock(pPos, copyWaterloggedFrom(pLevel, pPos, pState.setValue(HALF, DoubleBlockHalf.LOWER)), pFlags);
            pLevel.setBlock(blockpos, copyWaterloggedFrom(pLevel, blockpos, pState.setValue(HALF, DoubleBlockHalf.UPPER)), pFlags);
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        if (pState.getValue(IS_ON_CEILING)) {
            return pState.isSolidRender(pLevel, pPos.above());
        } else {
            return pState.isSolidRender(pLevel, pPos);
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockPos otherPos = state.getValue(IS_ON_CEILING) ? pos.below() : pos.above();
        BlockState otherState = copyWaterloggedFrom(level, otherPos, defaultBlockState()
                .setValue(HALF, DoubleBlockHalf.UPPER)
                .setValue(IS_ON_CEILING, true));

        level.setBlock(otherPos, otherState, Block.UPDATE_ALL);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        Optional<Holder.Reference<ConfiguredFeature<?, ?>>> feature = level.registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getHolder(state.getValue(IS_ON_CEILING) ? ceiling : ground);
        if (feature.isEmpty())
            return;

        BlockPos placePos = pos;
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER && state.getValue(IS_ON_CEILING))
            placePos = placePos.above();
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER && !state.getValue(IS_ON_CEILING))
            placePos = placePos.below();
        BlockGrowFeatureEvent event = EventHooks.fireBlockGrowFeature(level, random, placePos, feature.get());
        if (event.isCanceled())
            return;

        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 0);
        level.setBlock(placePos, Blocks.AIR.defaultBlockState(), 0);

        if (event.getFeature().value().place(level, level.getChunkSource().getGenerator(), random, placePos))
            return;

        level.setBlock(pos, state, 3);
    }

}
