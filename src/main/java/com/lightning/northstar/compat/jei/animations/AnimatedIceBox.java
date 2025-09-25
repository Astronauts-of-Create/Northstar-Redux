package com.lightning.northstar.compat.jei.animations;

import com.lightning.northstar.content.NorthstarBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;

public class AnimatedIceBox extends AnimatedKinetics {

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack pose = graphics.pose();

        pose.pushPose();
        pose.translate(xOffset, yOffset, 200);
        pose.mulPose(Axis.XP.rotationDegrees(-15.5f));
        pose.mulPose(Axis.YP.rotationDegrees(22.5f));

        blockElement(NorthstarBlocks.ICE_BOX.getDefaultState())
                .scale(24)
                .render(graphics);

        pose.popPose();
    }

}
