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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(CampfireBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class CampfireBlockMixin extends BaseEntityBlock implements SealReactiveBlock {

    public CampfireBlockMixin(Properties properties) {
        super(properties);
    }

    @Nullable
    @ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
    public BlockState northstar$updatePlacementLit(@Nullable BlockState state,
                                                   @Local(argsOnly = true) BlockPlaceContext context) {
        if (state == null || NorthstarOxygen.hasOxygen(context.getLevel(), context.getClickedPos()))
            return state;
        return state.setValue(CampfireBlock.LIT, false);
    }

    @ModifyReturnValue(method = "updateShape", at = @At("RETURN"))
    public BlockState northstar$updateShape(BlockState state,
                                            @Local(argsOnly = true) LevelAccessor level,
                                            @Local(argsOnly = true, ordinal = 0) BlockPos pos) {
        if (state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.LIT) &&
            level instanceof Level l && !NorthstarOxygen.hasOxygen(l, pos)) {
            level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1, 1);
            return state.setValue(CampfireBlock.LIT, false);
        }
        return state;
    }

    @Override
    public void northstar$onSealUpdated(Level level, BlockPos pos, BlockState state, SealingMode mode) {
        if (mode == SealingMode.OXYGEN && !NorthstarOxygen.hasOxygen(level, pos)) {
            level.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, false));
        }
    }

}
