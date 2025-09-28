package com.lightning.northstar.mixin.block;

import com.lightning.northstar.world.NorthstarOxygen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CandleCakeBlock.class)
public abstract class CandleCakeBlockMixin extends AbstractCandleBlock {

    protected CandleCakeBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(LIT, NorthstarOxygen.hasOxygen(context.getLevel(), context.getClickedPos()));
    }

    @Inject(method = "updateShape", at = @At("RETURN"), cancellable = true)
    public void northstar$updateLit(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
                                    BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> info) {
        BlockState returned = info.getReturnValue() == null ? defaultBlockState() : info.getReturnValue();
        if (returned.hasProperty(LIT) && returned.getValue(LIT) && level instanceof Level l && !NorthstarOxygen.hasOxygen(l, pos)) {
            level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1, 0);
            info.setReturnValue(returned.setValue(LIT, false));
        }
    }

}
