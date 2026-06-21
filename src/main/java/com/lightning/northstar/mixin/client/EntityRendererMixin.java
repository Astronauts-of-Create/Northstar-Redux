package com.lightning.northstar.mixin.client;

import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    // Disable culling of entities aboard rockets to prevent them from flickering
    @ModifyExpressionValue(
            method = "shouldRender",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/Entity;noCulling:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean northstar$shouldDisableEntityCulling(boolean original, @Local(argsOnly = true) T entity) {
        return original || entity.getVehicle() instanceof RocketContraptionEntity;
    }

}
