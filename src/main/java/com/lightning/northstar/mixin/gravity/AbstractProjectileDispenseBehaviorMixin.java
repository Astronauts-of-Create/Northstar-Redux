package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractProjectileDispenseBehavior.class)
public class AbstractProjectileDispenseBehaviorMixin {

    @ModifyConstant(
            method = "execute",
            constant = @Constant(floatValue = 0.1F)
    )
    private float northstar$modifyVerticalVelocity(float constant, @Local Level level) {
        return level.northstar$isZeroGravity() ? 0 : constant;
    }

}
