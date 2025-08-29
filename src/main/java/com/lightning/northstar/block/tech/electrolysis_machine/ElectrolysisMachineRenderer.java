package com.lightning.northstar.block.tech.electrolysis_machine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.platform.ForgeCatnipServices;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
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
        if (!VisualizationManager.supportsVisualization(be.getLevel())) {
            //Render a single shaft
            SuperByteBuffer shaft = CachedBuffers.partialFacingVertical(AllPartialModels.SHAFT_HALF, be.getBlockState(), Direction.SOUTH);
            standardKineticRotationTransform(shaft, be, light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }

        //Dont render fluid for a block that is opaque
//        TankSegment tank = be.getBehaviour(SmartFluidTankBehaviour.INPUT).getTanks()[0];
//        float level = tank.getFluidLevel().getValue(partialTicks);
//        ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(tank.getRenderedFluid(),
//                2f / 16f,
//                2f / 16f,
//                2f / 16f,
//                2f / 16f,
//                2f / 16f + 12f / 16f * level,
//                14f / 16f,
//                buffer, ms, light, false, false);
    }

}
