package com.lightning.northstar.block;

import com.lightning.northstar.content.NorthstarBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.util.TriState;

public class MarsSoilBlock extends Block {

    public MarsSoilBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility.equals(ItemAbilities.HOE_TILL) && context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
            return NorthstarBlocks.MARS_FARMLAND.get().defaultBlockState();
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
        if (plant.getBlock() instanceof CropBlock || plant.is(BlockTags.CROPS)) {
            return TriState.FALSE;
        }
        return super.canSustainPlant(state, level, soilPosition, facing, plant);
    }

}
