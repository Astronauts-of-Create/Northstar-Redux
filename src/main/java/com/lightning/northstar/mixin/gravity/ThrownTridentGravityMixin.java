package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownTrident.class)
public abstract class ThrownTridentGravityMixin extends AbstractArrow {

    protected ThrownTridentGravityMixin(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    @Definition(id = "inGroundTime", field = "Lnet/minecraft/world/entity/projectile/ThrownTrident;inGroundTime:I")
    @Expression("this.inGroundTime > 4")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean northstar$allowNoGravityPickup(boolean original) {
        return original || (level().northstar$isZeroGravity() && getDeltaMovement().lengthSqr() < 0.01);
    }

}
