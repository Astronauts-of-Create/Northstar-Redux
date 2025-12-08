package com.lightning.northstar.mixin.block;

import com.lightning.northstar.block.simple.ExtinguishedTorchBlock;
import com.lightning.northstar.block.simple.ExtinguishedTorchWallBlock;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StandingAndWallBlockItem.class)
public class TorchItemMixin {

    @Nullable
    @ModifyReturnValue(method = "getPlacementState", at = @At("RETURN"))
    public BlockState northstar$updatePlacementLit(@Nullable BlockState state,
                                                   @Local(argsOnly = true) BlockPlaceContext context) {
        if (state == null)
            return null;

        boolean water = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        if (NorthstarOxygen.hasOxygen(context.getLevel(), context.getClickedPos()) && !water)
            return state;

        if (state.is(Blocks.TORCH)) {
            return NorthstarBlocks.EXTINGUISHED_TORCH.get()
                    .defaultBlockState()
                    .setValue(ExtinguishedTorchBlock.WATERLOGGED, water);
        } else if (state.is(Blocks.WALL_TORCH)) {
            return NorthstarBlocks.EXTINGUISHED_TORCH_WALL.get()
                    .defaultBlockState()
                    .setValue(ExtinguishedTorchWallBlock.FACING, state.getValue(WallTorchBlock.FACING))
                    .setValue(ExtinguishedTorchWallBlock.WATERLOGGED, water);
        }
        // otherwise it's likely a soul/redstone torch, those shouldn't require air to work as they are special
        return state;
    }

}
