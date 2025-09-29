package com.lightning.northstar.mixin.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TorchBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TorchBlock.class)
public class TorchBlockMixin extends Block {

    public TorchBlockMixin(Properties properties) {
        super(properties);
    }

    /*@Inject(method = "updateShape", at = @At("TAIL"), cancellable = true)
    public void northstar$updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
                                      BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> info) {
        if (state.getBlock() == Blocks.TORCH && level instanceof Level l && !NorthstarOxygen.hasOxygen(l, pos)) {
            info.setReturnValue(NorthstarBlocks.EXTINGUISHED_TORCH.get().defaultBlockState());
        }
    }*/

}
