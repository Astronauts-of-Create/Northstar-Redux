package com.lightning.northstar.client.renderer.block;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class SpinningBlockRenderer {

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices,
                                           MultiBufferSource buffer, PartialModel model, float rpm) {
        PoseStack pose = matrices.getModel();
        pose.pushPose();

        float angle = (AnimationTickHolder.getRenderTime() * 0.3f * rpm) % 360;

        TransformStack.cast(pose)
                .centre()
                .rotateY(angle)
                .unCentre();

        CachedBufferer.partial(model, context.state)
                .light(LevelRenderer.getLightColor(renderWorld, context.localPos))
                //.useLevelLight(context.world, matrices.getWorld())
                .transform(pose)
                .renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.cutout()));

        pose.popPose();
    }

}
