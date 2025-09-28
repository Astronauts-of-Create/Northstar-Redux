package com.lightning.northstar.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class SpinningBlockRenderer {

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices,
                                           MultiBufferSource buffer, PartialModel model, float rpm) {
        PoseStack pose = matrices.getModel();
        pose.pushPose();

        float angle = (AnimationTickHolder.getRenderTime(renderWorld) * 0.3f * rpm) % 360;

        TransformStack.of(pose)
                .center()
                .rotateYDegrees(angle)
                .uncenter();

        CachedBuffers.partial(model, context.state)
                .light(LevelRenderer.getLightColor(renderWorld, context.localPos))
                .useLevelLight(context.world, matrices.getWorld())
                .transform(pose)
                .renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.cutout()));

        pose.popPose();
    }

}
