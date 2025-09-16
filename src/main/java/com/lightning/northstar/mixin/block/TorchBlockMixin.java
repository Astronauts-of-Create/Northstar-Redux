package com.lightning.northstar.mixin.block;

import com.lightning.northstar.content.NorthstarTechBlocks;
import com.lightning.northstar.world.NorthstarOxygen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TorchBlock.class)
public class TorchBlockMixin extends Block {

    public TorchBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "updateShape", at = @At("TAIL"), cancellable = true)
    public void northstar$updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
                                      BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> info) {
        if (!NorthstarOxygen.hasOxygen((Level) level, pos)) {
            info.setReturnValue(NorthstarTechBlocks.EXTINGUISHED_TORCH.get().defaultBlockState());
        }
    }

}
