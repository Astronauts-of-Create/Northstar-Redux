package com.lightning.northstar.mixin;

import com.lightning.northstar.world.NorthstarOxygen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void northstar$tick(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        NorthstarOxygen.tickEntity(self);
    }

}
