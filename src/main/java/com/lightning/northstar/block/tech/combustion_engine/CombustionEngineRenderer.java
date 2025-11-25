package com.lightning.northstar.block.tech.combustion_engine;

import com.lightning.northstar.block.tech.solar_panel.SolarPanelBlock;
import com.lightning.northstar.content.NorthstarPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class CombustionEngineRenderer extends KineticBlockEntityRenderer<CombustionEngineBlockEntity> {

    public CombustionEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(CombustionEngineBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        Direction facing = be.getBlockState().getValue(SolarPanelBlock.HORIZONTAL_FACING);
        float time = AnimationTickHolder.getRenderTime(be.getLevel()) * Math.signum(be.getSpeed()) * 2f;

        SuperByteBuffer shaft = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), facing.getOpposite());
        standardKineticRotationTransform(shaft, be, light).renderInto(ms, buffer.getBuffer(RenderType.solid()));

        renderPiston(be, ms, buffer, light, facing, NorthstarPartialModels.PISTON1, time + 0);
        renderPiston(be, ms, buffer, light, facing, NorthstarPartialModels.PISTON2, time + 2);
        renderPiston(be, ms, buffer, light, facing, NorthstarPartialModels.PISTON3, time + 4);
        renderPiston(be, ms, buffer, light, facing, NorthstarPartialModels.PISTON4, time + 8);
        renderPiston(be, ms, buffer, light, facing, NorthstarPartialModels.PISTON5, time + 10);
        renderPiston(be, ms, buffer, light, facing, NorthstarPartialModels.PISTON6, time + 12);
    }

    private void renderPiston(CombustionEngineBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, Direction facing, PartialModel model, float time) {
        CachedBuffers.partialFacing(model, be.getBlockState(), facing)
                .translate(0, CombustionEngineVisual.getPistonOffset(time), 0)
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

}
