package com.lightning.northstar.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.widget.Label;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public class ScrollingLabel extends Label {

    public ScrollingLabel(int x, int y, int w, int h, Component text) {
        super(x, y, text);
        this.width = w;
        this.height = h;
    }

    @Override
    protected void doRender(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (text == null || text.getString().isEmpty()) {
            return;
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);
        MutableComponent copy = text.plainCopy();
        if (suffix != null && !suffix.isEmpty())
            copy.append(suffix);

        // Use the minimum of the width and our width because this method renders it centered by default
        renderScrollingString(graphics, font, copy, getX(), getY(), getX() + Math.min(getWidth(), font.width(copy)), getY() + getHeight(), color);
    }
}
