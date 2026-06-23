package com.lightning.northstar.mixin.gravity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class EntityFallDamageGravityMixin extends Entity {

    public EntityFallDamageGravityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyVariable(
            method = "calculateFallDamage",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private float northstar$modifyFallDamageGravity(float fallDistance) {
        return fallDistance * level().northstar$gravityScale();
    }

}
