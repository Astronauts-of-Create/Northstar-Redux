package com.lightning.northstar.block.crops;

import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class MarsSproutBlock extends MartianFlowerBlock {

    public MarsSproutBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Item getSeedItem() {
        return NorthstarItems.MARS_SPROUT_SEEDS.get();
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        if (state.is(this)) {
            int age = state.getValue(AGE);
            switch (age) {
                case 0:
                    return 0;
                case 1:
                    return 4;
                case 2:
                    return 7;
            }
        }
        return 7;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (this.isMaxAge(state) && level.getBlockState(pos.above()).isAir()) {
            level.setBlock(pos, NorthstarBlocks.MARS_SPROUT_BIG.get().defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 2);
            level.setBlock(pos.above(), NorthstarBlocks.MARS_SPROUT_BIG.get().defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 2);
        }
        super.performBonemeal(level, random, pos, state);
    }

}
