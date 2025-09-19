package com.lightning.northstar.block.tech.solar_panel;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.lightning.northstar.content.NorthstarPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class SolarPanelRenderer extends KineticBlockEntityRenderer<SolarPanelBlockEntity> {

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
        TransformStack.cast(ms)
                .translate(0, 4f / 16f, 0)
                .centre()
                .rotateZ(be.targetAngle.getValue(partialTicks))
                .unCentre();

        CachedBufferer.partial(model, state)
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        ms.popPose();
    }

}
