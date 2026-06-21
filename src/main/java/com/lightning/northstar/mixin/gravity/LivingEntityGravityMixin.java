package com.lightning.northstar.mixin.gravity;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.planet.ZeroGravityUtils;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityGravityMixin extends Entity {

    @Shadow
    protected boolean jumping;

    public LivingEntityGravityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getGravity()D",
                    ordinal = 0
            )
    )
    private double northstar$modifyGravity(double value) {
        return level().northstar$isZeroGravity() ?
                jumping ?
                -0.2f :
                isCrouching() || new BlockCollisions<>(level(), this, getBoundingBox().expandTowards(0, -0.1f, 0), false, (pos, shape) -> pos).hasNext() ?
                0.2f :
                0.0f :
                value;
    }

    @ModifyReturnValue(
            method = "getJumpPower(F)F",
            at = @At("RETURN")
    )
    private float northstar$modifyJumpPower(float original) {
        return level().northstar$isZeroGravity() ? original * NorthstarConfigs.server().zeroGravityJumpStrength.getF() : original;
    }

    @ModifyReturnValue(
            method = "isSuppressingSlidingDownLadder",
            at = @At("RETURN")
    )
    private boolean northstar$modifySuppressingSlidingDownLadder(boolean original) {
        return original && !level().northstar$isZeroGravity();
    }

    @WrapWithCondition(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"
            )
    )
    private boolean northstar$replaceZeroGravityKnockback(LivingEntity entity, double strength, double x, double z, @Local(argsOnly = true) DamageSource source) {
        assert source.getEntity() != null;
        return ZeroGravityUtils.shouldApplyKnockback(source.getEntity(), this, strength);
    }

}
