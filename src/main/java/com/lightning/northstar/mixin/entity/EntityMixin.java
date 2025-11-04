package com.lightning.northstar.mixin.entity;

import com.lightning.northstar.accessor.NorthstarPlayer;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Entity.class)
public class EntityMixin {

    // Disable collisions with entities
    @Inject(method = "canCollideWith", at = @At("HEAD"), cancellable = true)
    private void disableEntityCollision(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if ((Entity) (Object) this instanceof Player player) {
            if (!(entity instanceof RocketContraptionEntity) &&
                    player.northstar$getRelativeEntity() != null
                    && player.northstar$getRelativeEntity() instanceof RocketContraptionEntity rce) {
                if (rce.isInFlight) cir.setReturnValue(false);
            }
        }
    }
}
