package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.ScaffoldingBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScaffoldingBlock.class)
public class ScaffoldingBlockGravityMixin {

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=7",
                    ordinal = 1
            )
    )
    private int northstar$modifyBreakingDistance(int constant, @Local(argsOnly = true) ServerLevel level) {
        // if we are on a zero gravity dimension, force to use the second path to break the block rather
        // than dropping it as the falling block entity which will get stuck without gravity
        return level.northstar$isZeroGravity() ? -1 : constant;
    }

}
