package com.lightning.northstar.block.crops;

import com.lightning.northstar.content.NorthstarBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MartianTallFlowerBlock extends TallFlowerBlock {

    public MartianTallFlowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT) ||
               state.is(Blocks.FARMLAND) ||
               state.is(NorthstarBlocks.MARS_SOIL.get()) ||
               state.is(NorthstarBlocks.MARS_FARMLAND.get());
    }

}
