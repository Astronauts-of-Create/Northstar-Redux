package com.lightning.northstar.block;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class LogBlock extends RotatedPillarBlock {

    private Block strippedVariant;

    public LogBlock(Properties pProperties, Block stripped) {
        super(pProperties);
        strippedVariant = stripped;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility.equals(ItemAbilities.AXE_STRIP) ) {
            return strippedVariant.defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

}
