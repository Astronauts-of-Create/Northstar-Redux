package com.lightning.northstar.compat.jei.animations;

import com.lightning.northstar.content.NorthstarPartialModels;
import com.lightning.northstar.content.NorthstarTechBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;

public class AnimatedEngraver extends AnimatedKinetics {

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack pose = graphics.pose();

        pose.pushPose();
        pose.translate(xOffset, yOffset - 16, 200);
        pose.mulPose(Axis.XP.rotationDegrees(-15.5f));
        pose.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 24;

        blockElement(shaft(Direction.Axis.Z))
                .rotateBlock(0, 0, getCurrentAngle())
                .scale(scale)
                .render(graphics);
        blockElement(NorthstarTechBlocks.CIRCUIT_ENGRAVER.getDefaultState())
                .scale(scale)
                .render(graphics);
        blockElement(NorthstarPartialModels.CIRCUIT_ENGRAVER_HEAD)
                .rotateBlock(0, getAnimatedHeadOffset(), 0)
                .scale(scale)
                .render(graphics);
        blockElement(NorthstarPartialModels.CIRCUIT_ENGRAVER_LASER)
                .rotateBlock(0, getAnimatedHeadOffset(), 0)
                .scale(scale)
                .render(graphics);
        blockElement(AllBlocks.DEPOT.getDefaultState())
                .atLocal(0, 1.65, 0)
                .scale(scale)
                .render(graphics);

        pose.popPose();
    }

    private float getAnimatedHeadOffset() {
        return (AnimationTickHolder.getRenderTime() - offset * 8) % 90 * 4;
    }

}
