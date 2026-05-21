package com.lightning.northstar.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    protected LivingEntityRendererMixin(Context context) {
        super(context);
    }

    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/WalkAnimationState;speed(F)F"
            )
    )
    private float changeWalkAnimationSpeed(float original, @Local(argsOnly = true) T entity) {
        float scale = entity.onGround() ? 1 : Mth.clamp(entity.level().northstar$gravityScale(), 0.25f, 1f);
        if (scale < 0.7 && entity.isInWater() && !entity.isVisuallySwimming() && !entity.isFallFlying()) {
            scale *= 1.2f;
        }
        return original / scale;
    }

}
