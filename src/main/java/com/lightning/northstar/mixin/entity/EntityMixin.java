package com.lightning.northstar.mixin.entity;

import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {

    @ModifyReturnValue(method = "canCollideWith", at = @At("RETURN"))
    private boolean northstar$disableCollisionInRocket(boolean original, @Local(argsOnly = true) Entity other) {
        Entity self = (Entity) (Object) this;

        if (original &&
                self instanceof Player player &&
                player.northstar$getRelativeEntity() instanceof RocketContraptionEntity rocket &&
                rocket.isInFlight &&
                other.getId() != rocket.getId()) {
            return false;
        }
        return original;
    }

}
