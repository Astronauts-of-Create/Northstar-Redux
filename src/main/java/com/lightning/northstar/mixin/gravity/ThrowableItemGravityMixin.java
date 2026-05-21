package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({
        ExperienceBottleItem.class,
        ThrowablePotionItem.class
})
public class ThrowableItemGravityMixin {

    @ModifyConstant(
            method = "use",
            constant = @Constant(floatValue = -20.0F)
    )
    private float northstar$modifyVerticalVelocityBias(float constant, @Local(argsOnly = true) Level level) {
        return level.northstar$isZeroGravity() ? 0 : constant;
    }

}
