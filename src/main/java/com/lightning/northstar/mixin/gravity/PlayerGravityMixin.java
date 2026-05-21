package com.lightning.northstar.mixin.gravity;

import com.lightning.northstar.planet.ZeroGravityUtils;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Player.class)
public abstract class PlayerGravityMixin extends LivingEntity {

    protected PlayerGravityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyConstant(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            constant = @Constant(doubleValue = (double) 0.2F)
    )
    private double northstar$modifyDropVerticalVelocity1(double constant) {
        // make items spread evenly up and down when dying
        return level().northstar$isZeroGravity() ? random.nextFloat() * 0.2 - 0.1 : constant;
    }

    @ModifyConstant(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            constant = @Constant(floatValue = 0.1F, ordinal = 0)
    )
    private float northstar$modifyDropVerticalVelocity2(float constant) {
        // make items fly directly forward (without up bias) when dropping
        return level().northstar$isZeroGravity() ? 0 : constant;
    }

    @WrapWithCondition(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"
            )
    )
    private boolean northstar$applyCustomKnockback(LivingEntity instance, double strength, double x, double z) {
        return ZeroGravityUtils.shouldApplyKnockback(this, instance, strength);
    }

    @WrapWithCondition(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;push(DDD)V"
            )
    )
    private boolean northstar$applyCustomPush(Entity instance, double x, double y, double z) {
        return ZeroGravityUtils.shouldApplyKnockback(this, instance, Vector3d.length(x, y, z));
    }

    // Count the player as on the ground in zero-gravity dimensions since the player will spend most of its time
    //  floating it would just make it very annoying if all blocks took five times longer to mine
    @ModifyExpressionValue(
            method = "getDigSpeed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;onGround()Z"
            )
    )
    private boolean northstar$modifyDigSpeed(boolean onGround) {
        return onGround || level().northstar$isZeroGravity();
    }

}
