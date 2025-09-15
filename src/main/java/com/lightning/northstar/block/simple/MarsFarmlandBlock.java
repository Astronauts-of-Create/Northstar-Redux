package com.lightning.northstar.block.simple;

import com.lightning.northstar.content.NorthstarBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.FarmlandWaterManager;
import net.neoforged.neoforge.common.util.TriState;

public class MarsFarmlandBlock extends FarmBlock {

    public MarsFarmlandBlock(Properties properties) {
        super(properties);
    }

    public static void turntoMarsSoil(BlockState state, Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos, pushEntitiesUp(state, NorthstarBlocks.MARS_SOIL.get().defaultBlockState(), level, pos));
    }

    private static boolean isNearWater(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (state.canBeHydrated(level, pos, level.getFluidState(blockpos), blockpos)) {
                return true;
            }
        }
        return FarmlandWaterManager.hasBlockWaterTicket(level, pos);
    }

    private static TriState isUnderCrops(BlockGetter level, BlockPos pos) {
        BlockState plant = level.getBlockState(pos.above());
        BlockState state = level.getBlockState(pos);
        return state.canSustainPlant(level, pos, Direction.UP, plant);
    }


    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState aboveState = level.getBlockState(pos.above());
        return super.canSurvive(state, level, pos) || aboveState.getBlock() instanceof AttachedStemBlock;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos()) ? NorthstarBlocks.MARS_SOIL.get().defaultBlockState() : super.getStateForPlacement(context);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!state.canSurvive(level, pos)) {
            turntoMarsSoil(state, level, pos);
        }
    }

    @Override
    public void fallOn(Level level, BlockState blockState, BlockPos pos, Entity entity, float p_153231_) {
         if (!level.isClientSide && CommonHooks.onFarmlandTrample(level, pos, Blocks.DIRT.defaultBlockState(), p_153231_, entity)) { // Forge: Move logic to Entity#canTrample
    turntoMarsSoil(blockState, level, pos);}
                }
    @Override
    public void randomTick(BlockState blockState, ServerLevel level, BlockPos pos, RandomSource p_221142_) {
        int i = blockState.getValue(MOISTURE);
        if (!isNearWater(level, pos) && !level.isRainingAt(pos.above())) {
            if (i > 0) {
                level.setBlock(pos, blockState.setValue(MOISTURE, i - 1), 2);
            } else if (isUnderCrops(level, pos).isFalse()) {
                turntoMarsSoil(blockState, level, pos);
            }
        } else if (i < 7) {
            level.setBlock(pos, blockState.setValue(MOISTURE, 7), 2);
        }
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
        if (plant.getBlock() instanceof CropBlock || plant.is(BlockTags.CROPS)) {
            return TriState.TRUE;
        }
        return super.canSustainPlant(state, level, soilPosition, facing, plant);
    }

}
