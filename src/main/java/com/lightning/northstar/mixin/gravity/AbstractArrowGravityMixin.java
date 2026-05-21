package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowGravityMixin extends Projectile {

    protected AbstractArrowGravityMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyConstant(
            method = "tick",
            constant = @Constant(doubleValue = (double) 0.05f)
    )
    private double northstar$modifyGravity(double constant) {
        return constant * level().northstar$gravityScale();
    }

    @ModifyExpressionValue(
            method = "playerTouch",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;inGround:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean northstar$allowNoGravityPickup(boolean original) {
        return original || (level().northstar$isZeroGravity() && getDeltaMovement().lengthSqr() < 0.01);
    }

}
