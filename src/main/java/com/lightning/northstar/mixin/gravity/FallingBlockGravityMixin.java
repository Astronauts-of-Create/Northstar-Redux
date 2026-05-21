package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.FallingBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FallingBlock.class)
public class FallingBlockGravityMixin {

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/FallingBlock;isFree(Lnet/minecraft/world/level/block/state/BlockState;)Z"
            )
    )
    private boolean northstar$allowFall(boolean original, @Local(argsOnly = true) ServerLevel level) {
        return original && !level.northstar$isZeroGravity();
    }

}
