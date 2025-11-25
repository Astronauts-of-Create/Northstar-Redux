package com.lightning.northstar.client.renderer;

import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;

public class RemainingOxygenOverlay implements LayeredDraw.Layer {

    public static final RemainingOxygenOverlay INSTANCE = new RemainingOxygenOverlay();

    @Override
    public void render(GuiGraphics graphics, DeltaTracker delta) {
        PoseStack pose = graphics.pose();

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (mc.options.hideGui ||
                player == null ||
                player.isSpectator() ||
                player.isCreative() ||
                (NorthstarOxygen.hasOxygen(player.level(), player.getEyePosition())) && !player.canDrownInFluidType(player.getEyeInFluidType()))
            return;

        ItemStack tank = NorthstarOxygen.getOxygenTank(player);
        if (tank.isEmpty())
            return;
        int remainingTime = tank.has(NorthstarDataComponents.OXYGEN) ? tank.get(NorthstarDataComponents.OXYGEN) : 0;

        pose.pushPose();

        pose.translate(graphics.guiWidth() / 2f + 95, graphics.guiHeight() - 40, 0);
        int color = 0xFF_FFFFFF;
        if (remainingTime <= 60 && remainingTime % 2 == 0)
            color = Color.mixColors(0xFF_FF0000, color, Math.max(remainingTime / 60f, .25f));
        GuiGameElement.of(tank)
                .at(0, 0)
                .render(graphics);
        graphics.drawString(mc.font, StringUtil.formatTickDuration(Math.max(0, remainingTime - 1) * 20, 20), 18, 5, color);

        pose.popPose();
    }

}
