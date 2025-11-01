package com.lightning.northstar.block.simple;

import com.lightning.northstar.content.NorthstarBlocks;
import com.simibubi.create.foundation.utility.DyeHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class VentBlock extends GrateBlock {

    public VentBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack item, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!item.isEmpty() && item.is(ItemTags.WOOL)) {
            DyeColor color = Arrays.stream(DyeColor.values())
                    .filter(dye -> DyeHelper.getWoolOfDye(dye).asItem() == item.getItem())
                    .findFirst()
                    .orElse(null);

            if (color != null) {
                if (state.hasProperty(InsulatedVentBlock.COLOR)) {
                    if (state.getValue(InsulatedVentBlock.COLOR) == color)
                        return ItemInteractionResult.sidedSuccess(level.isClientSide);

                    popResource(level, pos, new ItemStack(DyeHelper.getWoolOfDye(state.getValue(InsulatedVentBlock.COLOR))));
                }

                BlockState newState = NorthstarBlocks.INSULATED_VENT.get()
                        .withPropertiesOf(state)
                        .setValue(InsulatedVentBlock.COLOR, color);
                level.setBlock(pos, newState, Block.UPDATE_ALL);

                if (!player.isCreative())
                    item.setCount(item.getCount() - 1);
                player.setItemInHand(hand, item);

                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useItemOn(item, state, level, pos, player, hand, hitResult);
    }

}
