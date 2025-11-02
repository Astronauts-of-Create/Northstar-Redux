package com.lightning.northstar.mixin.block;

import com.lightning.northstar.block.simple.ExtinguishedTorchBlock;
import com.lightning.northstar.block.simple.ExtinguishedTorchWallBlock;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StandingAndWallBlockItem.class)
public class TorchItemMixin {

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    public void getPlacementState(BlockPlaceContext context, CallbackInfoReturnable<BlockState> info) {
        BlockState state = info.getReturnValue();
        if (state == null)
            return;

        boolean water = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        if (NorthstarOxygen.hasOxygen(context.getLevel(), context.getClickedPos()) && !water)
            return;

        if (state.is(Blocks.TORCH)) {
            info.setReturnValue(NorthstarBlocks.EXTINGUISHED_TORCH.get()
                    .defaultBlockState()
                    .setValue(ExtinguishedTorchBlock.WATERLOGGED, water));
        } else if (state.is(Blocks.WALL_TORCH)) {
            info.setReturnValue(NorthstarBlocks.EXTINGUISHED_TORCH_WALL.get()
                    .defaultBlockState()
                    .setValue(ExtinguishedTorchWallBlock.FACING, state.getValue(WallTorchBlock.FACING))
                    .setValue(ExtinguishedTorchWallBlock.WATERLOGGED, water));
        }
        // otherwise it's likely a soul/redstone torch, those shouldn't require air to work as they are special
    }

}
