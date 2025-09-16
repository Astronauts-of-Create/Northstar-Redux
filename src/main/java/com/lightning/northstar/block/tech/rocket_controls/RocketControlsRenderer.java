package com.lightning.northstar.block.tech.rocket_controls;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.lightning.northstar.content.NorthstarPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class RocketControlsRenderer extends SafeBlockEntityRenderer<RocketControlsBlockEntity> {

    public RocketControlsRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(RocketControlsBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        pose.pushPose();
        getBuffer(pose, be.getBlockState(), 0)
                .light(light)
                .overlay(overlay)
                .renderInto(pose, buffer.getBuffer(RenderType.solid()));
        pose.popPose();
    }

    public static void render(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices,
                              MultiBufferSource buffer, float angle) {
        PoseStack pose = matrices.getModel();
        pose.pushPose();

        getBuffer(pose, context.state, angle)
                .transform(pose)
                .light(LevelRenderer.getLightColor(renderWorld, context.localPos))
                //.useLevelLight(context.world, matrices.getWorld())
                .renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.solid()));

        pose.popPose();
    }

    private static SuperByteBuffer getBuffer(PoseStack pose, BlockState state, float angle) {
        Direction facing = state.getValue(RocketControlsBlock.FACING);
        SuperByteBuffer lever = CachedBufferer.partial(NorthstarPartialModels.CONTROL_LEVER, state);

        TransformStack.cast(pose)
                .centre()
                .rotateY(180 + AngleHelper.horizontalAngle(facing))
                .translate((10 - 8) / 16f, (12 - 8) / 16f, (10 - 8) / 16f)
                // WHY IS IT 50 IT SHOULD BE 45 THIS MAKES NO MATHEMATICAL OR GEOMETRICAL SENSE WHATSOEVER
                .rotateX(2.5f - 50f * angle)
                .unCentre();

        return lever;
    }

}
