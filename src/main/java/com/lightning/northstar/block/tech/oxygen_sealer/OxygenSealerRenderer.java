package com.lightning.northstar.block.tech.oxygen_sealer;

import com.lightning.northstar.content.NorthstarPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class OxygenSealerRenderer extends KineticBlockEntityRenderer<OxygenSealerBlockEntity> {

    public OxygenSealerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(OxygenSealerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        SuperByteBuffer shaft = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), Direction.DOWN);
        standardKineticRotationTransform(shaft, be, light).renderInto(ms, buffer.getBuffer(RenderType.solid()));

        SuperByteBuffer fan = CachedBuffers.partial(NorthstarPartialModels.OXYGEN_SEALER_FAN, be.getBlockState());
        standardKineticRotationTransform(fan, be, light).renderInto(ms, buffer.getBuffer(RenderType.cutout()));
    }

}
