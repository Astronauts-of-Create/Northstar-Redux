package com.lightning.northstar.block.tech.electrolysis_machine;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;

public class ElectrolysisMachineRenderer extends KineticBlockEntityRenderer<ElectrolysisMachineBlockEntity> {

    public ElectrolysisMachineRenderer(Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(ElectrolysisMachineBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (!Backend.canUseInstancing(be.getLevel())) {
            SuperByteBuffer shaft = CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), Direction.DOWN);
            standardKineticRotationTransform(shaft, be, light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
    }

}
