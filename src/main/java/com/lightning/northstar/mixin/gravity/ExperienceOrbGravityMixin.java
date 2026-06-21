package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbGravityMixin extends Entity {

    protected ExperienceOrbGravityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Definition(id = "random", field = "Lnet/minecraft/world/entity/ExperienceOrb;random:Lnet/minecraft/util/RandomSource;")
    @Definition(id = "nextDouble", method = "Lnet/minecraft/util/RandomSource;nextDouble()D")
    @Expression("@(this.random.nextDouble()) * 0.2 * 2.0")
    @ModifyExpressionValue(
            method = "<init>(Lnet/minecraft/world/level/Level;DDDI)V",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private static double northstar$modifyInitialVerticalVelocity(double y, @Local(argsOnly = true) Level level) {
        return level.northstar$isZeroGravity() ? y * 2.0 - 1.0 : y;
    }

    @ModifyConstant(
            method = {
                    "tick",
                    "scanForEntities"
            },
            constant = {
                    @Constant(doubleValue = 64.0),
                    @Constant(doubleValue = 8.0)
            }
    )
    private double northstar$increaseFollowRange(double constant) {
        if (level().northstar$isZeroGravity()) {
            // Increase the follow range in zero gravity as it can be very annoying to pick up experience orbs otherwise
            return constant > 16 ? 16 * 16 : 16;
        }
        return constant;
    }

}
