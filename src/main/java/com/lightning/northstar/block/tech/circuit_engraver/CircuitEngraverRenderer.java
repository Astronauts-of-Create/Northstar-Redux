package com.lightning.northstar.block.tech.circuit_engraver;

import com.lightning.northstar.content.NorthstarPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;

public class CircuitEngraverRenderer extends ShaftRenderer<CircuitEngraverBlockEntity> {

    public CircuitEngraverRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(CircuitEngraverBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        float time = AnimationTickHolder.getRenderTime(be.getLevel());
        float angle = ((time * be.getRenderedHeadRotationSpeed(partialTicks) / 10) % 360) / 180 * Mth.PI;

        SuperByteBuffer head = CachedBuffers.partial(NorthstarPartialModels.CIRCUIT_ENGRAVER_HEAD, be.getBlockState());
        kineticRotationTransform(head, be, Axis.Y, angle * 0.5f, light).renderInto(ms, buffer.getBuffer(RenderType.cutout()));

        if (be.engravingBehaviour.running) {
            ms.pushPose();
            ms.translate(0, -0.16f, 0);

            SuperByteBuffer laser = CachedBuffers.partial(NorthstarPartialModels.CIRCUIT_ENGRAVER_LASER, be.getBlockState());
            kineticRotationTransform(laser, be, Axis.Y, angle / 1.5f, light).renderInto(ms, buffer.getBuffer(RenderType.cutout()));

            ms.popPose();
        }
    }

}
