package com.lightning.northstar.item.armor;

import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.world.OxygenStuff;
import com.mojang.blaze3d.vertex.PoseStack;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

public class RemainingOxygenOverlay implements LayeredDraw.Layer {

    public static final RemainingOxygenOverlay INSTANCE = new RemainingOxygenOverlay();

    @Override
    public void render(GuiGraphics graphics, DeltaTracker delta) {
        PoseStack pose = graphics.pose();

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        LocalPlayer player = mc.player;
        if (player == null)
            return;
        if (player.isCreative())
            return;


        pose.pushPose();

        ItemStack oxygenTank = OxygenStuff.getOxy(player);
        if (oxygenTank.isEmpty()) {
            return;
        }

        int remainingOxygen = oxygenTank.has(NorthstarDataComponents.OXYGEN) ? oxygenTank.get(NorthstarDataComponents.OXYGEN) : 0;

        pose.translate(graphics.guiWidth() / 2f + 90, graphics.guiHeight() - 53, 0);

        Component text = Component.literal(StringUtil.formatTickDuration(Math.max(0, remainingOxygen - 1) * 20, 20));
        GuiGameElement.of(oxygenTank)
                .at(0, 0)
                .render(graphics);
        int color = 0xFF_FFFFFF;
        if (remainingOxygen < 60 && remainingOxygen % 2 == 0) {
            color = Color.mixColors(0xFF_FF0000, color, Math.max(remainingOxygen / 60f, .25f));
        }
        graphics.drawString(mc.font, text, 16, 5, color);
        pose.popPose();
    }

}
