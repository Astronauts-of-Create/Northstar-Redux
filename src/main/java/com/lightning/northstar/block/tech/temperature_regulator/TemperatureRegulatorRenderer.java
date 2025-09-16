package com.lightning.northstar.block.tech.temperature_regulator;

import com.jozufozu.flywheel.backend.Backend;
import com.lightning.northstar.content.NorthstarPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class TemperatureRegulatorRenderer extends KineticBlockEntityRenderer<TemperatureRegulatorBlockEntity> {

    public TemperatureRegulatorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TemperatureRegulatorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (Backend.canUseInstancing(be.getLevel()))
            return;

        boolean warm = be.isCurrentlyWarm();
        float time = AnimationTickHolder.getRenderTime();
        float angle = ((time * be.getSpeed() / 60) % 360) / 180 * Mth.PI;

        SuperByteBuffer shaft = CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), Direction.DOWN);
        standardKineticRotationTransform(shaft, be, light).renderInto(ms, buffer.getBuffer(RenderType.solid()));

        SuperByteBuffer spinner = CachedBufferer.partial(warm ? NorthstarPartialModels.WARM_SPINNY : NorthstarPartialModels.COLD_SPINNY, be.getBlockState());
        kineticRotationTransform(spinner, be, Direction.Axis.Y, angle, light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

}
