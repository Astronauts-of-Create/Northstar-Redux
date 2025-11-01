package com.lightning.northstar.block.tech.atmospheric_concentrator;

import com.jozufozu.flywheel.backend.Backend;
import com.lightning.northstar.content.NorthstarPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class AtmosphericConcentratorRenderer extends KineticBlockEntityRenderer<AtmosphericConcentratorBlockEntity> {

    public AtmosphericConcentratorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(AtmosphericConcentratorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (Backend.canUseInstancing(be.getLevel()))
            return;

        SuperByteBuffer shaft = CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), Direction.DOWN);
        standardKineticRotationTransform(shaft, be, light).renderInto(ms, buffer.getBuffer(RenderType.solid()));

        SuperByteBuffer fan = CachedBufferer.partial(NorthstarPartialModels.ATMOSPHERIC_CONCENTRATOR_FAN, be.getBlockState());
        standardKineticRotationTransform(fan, be, light).renderInto(ms, buffer.getBuffer(RenderType.cutout()));
    }

}
