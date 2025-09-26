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

        float time = AnimationTickHolder.getRenderTime(be.getLevel()) / 20;
        float angle = be.isRunning() ? ((time * be.getSpeed()) % 360) / 180 * Mth.PI : 0;

        SuperByteBuffer head = CachedBuffers.partial(NorthstarPartialModels.CIRCUIT_ENGRAVER_HEAD, be.getBlockState());
        kineticRotationTransform(head, be, Axis.Y, angle, light).renderInto(ms, buffer.getBuffer(RenderType.cutout()));

        if (be.isRunning()) {
            ms.pushPose();
            ms.translate(0, -0.16f, 0);

            SuperByteBuffer laser = CachedBuffers.partial(NorthstarPartialModels.CIRCUIT_ENGRAVER_LASER, be.getBlockState());
            kineticRotationTransform(laser, be, Axis.Y, angle * 2, light).renderInto(ms, buffer.getBuffer(RenderType.cutout()));

            ms.popPose();
        }
    }

}
