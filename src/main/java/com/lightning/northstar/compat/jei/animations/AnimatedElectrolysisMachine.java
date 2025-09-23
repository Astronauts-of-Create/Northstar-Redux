package com.lightning.northstar.compat.jei.animations;

import com.lightning.northstar.content.NorthstarBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;

public class AnimatedElectrolysisMachine extends AnimatedKinetics {

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack pose = graphics.pose();

        pose.pushPose();
        pose.translate(xOffset, yOffset + 22, 200);
        int scale = 24;

        blockElement(NorthstarBlocks.ELECTROLYSIS_MACHINE.getDefaultState())
                .rotateBlock(22.5, 22.5, 0)
                .scale(scale)
                .render(graphics);

        pose.popPose();
    }

}