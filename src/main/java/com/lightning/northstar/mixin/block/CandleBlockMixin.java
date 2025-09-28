package com.lightning.northstar.mixin.block;

import com.lightning.northstar.world.NorthstarOxygen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CandleBlock.class)
public class CandleBlockMixin {

    @Inject(method = "updateShape", at = @At("TAIL"), cancellable = true)
    public void northstar$updateLit(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
                                    BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> info) {
        if (level instanceof Level l && !NorthstarOxygen.hasOxygen(l, pos) && state.getValue(CandleBlock.LIT)) {
            level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1, 0);
            info.setReturnValue(state.setValue(CandleBlock.LIT, false));
        }
    }
}
