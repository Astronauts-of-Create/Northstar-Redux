package com.lightning.northstar.mixin.create;

import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ContraptionCollider.class, remap = false)
public class ContraptionColliderMixin {

    @Redirect(method = "collideEntities",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;subtract(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 0,
                    remap = false))
    private static Vec3 northstar$changeBounds(Vec3 instance, Vec3 vec, @Local(argsOnly = true) AbstractContraptionEntity entity) {
        if (entity instanceof RocketContraptionEntity) {
            return Vec3.ZERO; // pretend that we haven't moved, entities are moved manually in RocketContraptionEntity
        }
        return instance.subtract(vec);
    }

}
