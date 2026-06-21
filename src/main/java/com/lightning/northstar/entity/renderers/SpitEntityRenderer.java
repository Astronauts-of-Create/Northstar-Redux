package com.lightning.northstar.entity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SpitEntityRenderer<T extends Entity> extends EntityRenderer<T> {

    public static <T extends Entity> EntityRendererProvider<T> factory(ResourceLocation spitTexture) {
        return context -> new SpitEntityRenderer<>(context, spitTexture);
    }

    private final ResourceLocation textureLocation;
    private final LlamaSpitModel<T> model;

    public SpitEntityRenderer(EntityRendererProvider.Context context, ResourceLocation textureLocation) {
        super(context);
        this.textureLocation = textureLocation;
        this.model = new LlamaSpitModel<>(context.bakeLayer(ModelLayers.LLAMA_SPIT));
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int packedLight) {
        pose.pushPose();
        pose.translate(0.0D, 0.15F, 0.0D);
        pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        pose.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        model.setupAnim(entity, partialTick, 0.0F, -0.1F, 0.0F, 0.0F);
        model.renderToBuffer(pose, buffers.getBuffer(model.renderType(textureLocation)), packedLight, OverlayTexture.NO_OVERLAY);
        pose.popPose();

        super.render(entity, entityYaw, partialTick, pose, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return textureLocation;
    }

}
