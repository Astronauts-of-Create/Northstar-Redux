package com.lightning.northstar.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {

    @ModifyExpressionValue(
            method = "lambda$getFluidAboveStalactite$11",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/dimension/DimensionType;ultraWarm()Z"
            )
    )
    private static boolean northstar$isUltraWarm(boolean original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        return level.northstar$temperature().isUltraWarm(pos, 100, original);
    }

}
