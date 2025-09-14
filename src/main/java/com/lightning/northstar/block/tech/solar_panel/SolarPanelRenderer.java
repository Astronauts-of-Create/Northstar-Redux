package com.lightning.northstar.block.tech.solar_panel;

import com.lightning.northstar.content.NorthstarPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class SolarPanelRenderer extends ShaftRenderer<SolarPanelBlockEntity> {

    public SolarPanelRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(SolarPanelBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        BlockState state = be.getBlockState();
        boolean north = !state.getValue(SolarPanelBlock.NORTH);
        boolean south = !state.getValue(SolarPanelBlock.SOUTH);
        PartialModel model;
        if (north && south) {
            model = NorthstarPartialModels.SOLAR_PANEL_FULL;
        } else if (north) {
            model = NorthstarPartialModels.SOLAR_PANEL_NORTH;
        } else if (south) {
            model = NorthstarPartialModels.SOLAR_PANEL_SOUTH;
        } else {
            model = NorthstarPartialModels.SOLAR_PANEL_SLIM;
        }

        float angle = Mth.clamp(AngleHelper.wrapAngle180(be.getLevel().getTimeOfDay(partialTicks) * 360), -45, +45);
        be.targetAngle.chase(angle, 0.2, LerpedFloat.Chaser.EXP);

        ms.pushPose();
        TransformStack.of(ms)
                .translate(0, 4f / 16f, 0)
                .center()
                .rotateZDegrees(be.targetAngle.getValue(partialTicks))
                .uncenter();

        CachedBuffers.partial(model, state)
                .light(light)
                .overlay(overlay)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        ms.popPose();
    }

}
