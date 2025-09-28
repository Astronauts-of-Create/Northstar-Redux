package com.lightning.northstar.mixin;

import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    protected LivingEntityRendererMixin(Context context) {
        super(context);
    }

    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/WalkAnimationState;speed(F)F"))
    private float changeWalkAnimationSpeed(WalkAnimationState instance, float partialTick, @Local(argsOnly = true) T entity) {
        float gravityMultiplier = entity.onGround() ? 1 :
                Mth.clamp((float) NorthstarPlanets.getGravMultiplier(entity.level().dimension()), 0.25f, 1f);

        if (!entity.onGround() && gravityMultiplier < 0.7 && entity.isInWater() && !entity.isVisuallySwimming() && !entity.isFallFlying()) {
            gravityMultiplier *= 1.2f;
        }

        return instance.speed(partialTick) * gravityMultiplier;
    }

}
