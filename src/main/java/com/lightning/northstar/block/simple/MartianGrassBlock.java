package com.lightning.northstar.block.simple;

import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MartianGrassBlock extends Block implements BonemealableBlock {

    public MartianGrassBlock(Properties properties) {
        super(properties);
    }

    private static boolean canBeGrass(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos above = pos.above();
        BlockState blockstate = level.getBlockState(above);
        if (blockstate.getFluidState().getAmount() == 8) {
            return false;
        }
        int i = BlockLightEngine.getLightBlockInto(level, state, pos, blockstate, above, Direction.UP, blockstate.getLightBlock(level, above));
        return i < level.getMaxLightLevel() && NorthstarTemperature.getTemperature((Level) level, above) > 0;
    }

    private static boolean canPropagate(BlockState state, LevelReader level, BlockPos pos) {
        return canBeGrass(state, level, pos) && !level.getFluidState(pos.above()).is(FluidTags.WATER);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable) {
        PlantType plantType = plantable.getPlantType(level, pos.relative(facing));
        return plantType != PlantType.CROP && plantType != PlantType.WATER;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canBeGrass(state, level, pos)) {
            if (!level.isAreaLoaded(pos, 1)) return;
            if (random.nextInt(5) == 0) {
                level.setBlockAndUpdate(pos, NorthstarBlocks.MARS_SOIL.get().defaultBlockState());
            }
            return;
        }

        if (!level.isAreaLoaded(pos, 3)) return;
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
            BlockState blockstate = defaultBlockState();

            for (int i = 0; i < 4; ++i) {
                BlockPos blockpos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                if (level.getBlockState(blockpos).is(NorthstarBlocks.MARS_SOIL.get()) && canPropagate(blockstate, level, blockpos)) {
                    level.setBlockAndUpdate(blockpos, blockstate);
                }
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockState grass = NorthstarBlocks.MARTIAN_TALL_GRASS.get().defaultBlockState();
        if (random.nextBoolean() && level.getBlockState(pos.above()).isAir()) {
            level.setBlock(pos.above(), grass, Block.UPDATE_CLIENTS);
        }

        MutableBlockPos other = new MutableBlockPos();
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    other.setWithOffset(pos, x, y, z);
                    BlockState below = level.getBlockState(other.below());
                    if (level.getBlockState(other).isAir() &&
                        (below.is(NorthstarBlocks.MARTIAN_GRASS.get()) || below.is(NorthstarBlocks.MARS_SOIL.get())) &&
                        random.nextInt(3) == 0) {
                        level.setBlock(other, grass, Block.UPDATE_CLIENTS);
                    }
                }
            }
        }
    }

}
