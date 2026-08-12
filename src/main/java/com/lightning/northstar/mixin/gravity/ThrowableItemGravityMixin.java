package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({
        ExperienceBottleItem.class,
        ThrowablePotionItem.class
})
public class ThrowableItemGravityMixin {

    @ModifyExpressionValue(
            method = "use",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=-20.0"
            )
    )
    private float northstar$modifyVerticalVelocityBias(float constant, @Local(argsOnly = true) Level level) {
        return level.northstar$isZeroGravity() ? 0 : constant;
    }

}
