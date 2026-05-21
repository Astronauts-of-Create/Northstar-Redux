package com.lightning.northstar.mixin.oxygen;

import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(LivingEntity.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(
            method = "updateFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getSharedFlag(I)Z"
            )
    )
    private boolean northstar$updateElytraFlight(boolean original) {
        return original && NorthstarOxygen.hasOxygen(level(), position());
    }

}
