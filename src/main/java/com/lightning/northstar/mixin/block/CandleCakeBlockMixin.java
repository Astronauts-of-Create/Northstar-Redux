package com.lightning.northstar.mixin.block;

import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.sealer.SealReactiveBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
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

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(CandleCakeBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class CandleCakeBlockMixin extends AbstractCandleBlock implements SealReactiveBlock {

    protected CandleCakeBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(LIT, NorthstarOxygen.hasOxygen(context.getLevel(), context.getClickedPos()));
    }

    @ModifyReturnValue(method = "updateShape", at = @At("RETURN"))
    public BlockState northstar$updateShape(BlockState state,
                                            @Local(argsOnly = true) LevelAccessor level,
                                            @Local(argsOnly = true, ordinal = 0) BlockPos pos) {
        if (state.getBlock() instanceof CandleCakeBlock && state.getValue(LIT) &&
            level instanceof Level l && !NorthstarOxygen.hasOxygen(l, pos)) {
            level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1, 1);
            return state.setValue(LIT, false);
        }
        return state;
    }

    @Override
    public void northstar$onSealUpdated(Level level, BlockPos pos, BlockState state, SealingMode mode) {
        if (mode == SealingMode.OXYGEN && !NorthstarOxygen.hasOxygen(level, pos)) {
            level.setBlockAndUpdate(pos, state.setValue(LIT, false));
        }
    }

}
