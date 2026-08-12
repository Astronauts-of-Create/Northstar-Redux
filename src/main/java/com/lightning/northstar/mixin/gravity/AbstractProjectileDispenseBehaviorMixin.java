package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractProjectileDispenseBehavior.class)
public class AbstractProjectileDispenseBehaviorMixin {

    @ModifyExpressionValue(
            method = "execute",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=0.1"
            )
    )
    private float northstar$modifyVerticalVelocity(float constant, @Local Level level) {
        return level.northstar$isZeroGravity() ? 0 : constant;
    }

}
