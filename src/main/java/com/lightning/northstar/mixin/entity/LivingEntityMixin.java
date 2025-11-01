package com.lightning.northstar.mixin.entity;

import com.lightning.northstar.world.temperature.NorthstarTemperature;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(LivingEntity.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void northstar$tick(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        NorthstarTemperature.tickEntity(self);
    }

}
