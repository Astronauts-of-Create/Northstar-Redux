package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrowableProjectile.class)
public abstract class ThrowableProjectileGravityMixin extends Projectile {

    public ThrowableProjectileGravityMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/ThrowableProjectile;getGravity()F"
            )
    )
    private float northstar$modifyGravity(float gravity) {
        return gravity * level().northstar$gravityScale();
    }

}
