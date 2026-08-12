package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Containers.class)
public class ContainersGravityMixin {

    @ModifyExpressionValue(
            method = "dropItemStack",
            at = @At(
                    value = "CONSTANT",
                    args = "doubleValue=0.2"
            )
    )
    private static double northstar$modifyVelocityY(double constant, @Local(argsOnly = true) Level level) {
        return level.northstar$isZeroGravity() ? 0.0 : constant;
    }

}
