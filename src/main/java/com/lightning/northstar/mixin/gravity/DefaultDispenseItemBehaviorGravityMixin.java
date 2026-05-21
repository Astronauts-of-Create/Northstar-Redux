package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DefaultDispenseItemBehavior.class)
public class DefaultDispenseItemBehaviorGravityMixin {

    @ModifyExpressionValue(
            method = "spawnItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;triangle(DD)D",
                    ordinal = 1
            )
    )
    private static double northstar$modifyInitialVerticalVelocity(
            double original,
            @Local(argsOnly = true) Level level,
            @Local(argsOnly = true) Direction facing,
            @Local(argsOnly = true) int speed
    ) {
        return level.northstar$isZeroGravity() && facing.getAxis() != Direction.Axis.Y ?
                level.random.triangle(-0.0172275 * speed, 0.0172275 * speed) :
                original;
    }

}
