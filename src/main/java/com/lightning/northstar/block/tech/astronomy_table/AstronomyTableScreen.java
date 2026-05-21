package com.lightning.northstar.block.tech.astronomy_table;

import com.lightning.northstar.Northstar;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AstronomyTableScreen extends AbstractSimiContainerScreen<AstronomyTableMenu> {

    private static final ResourceLocation TABLE_LOCATION = Northstar.asResource("textures/gui/astronomy_table.png");

    private int lastMessages = 0;
    private List<FormattedCharSequence> splitMessages = new ArrayList<>();

    public AstronomyTableScreen(AstronomyTableMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TABLE_LOCATION, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        if (menu.hasError) {
            graphics.blit(TABLE_LOCATION, leftPos + 101, topPos + 54, 176, 0, 28, 21);
        }

        int hashCode = menu.messages.hashCode();
        if (hashCode != lastMessages) {
            lastMessages = hashCode;

            splitMessages.clear();
            for (Component message : menu.messages) {
                splitMessages.addAll(font.split(message, imageWidth - 2 * 6));
            }
        }

        int y = topPos + 7;
        for (FormattedCharSequence message : splitMessages) {
            graphics.drawString(font, message, leftPos + 6, y, 0xFFFFFF, true);
            y += 10;
        }
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderForeground(graphics, mouseX, mouseY, partialTicks);

        graphics.renderComponentHoverEffect(font, getComponentStyleAt(mouseX, mouseY), mouseX, mouseY);
    }

    @Nullable
    private Style getComponentStyleAt(int mouseX, int mouseY) {
        int line = (mouseY - topPos - 7) / 10;
        int x = mouseX - leftPos - 6;
        if (line < 0 || line >= splitMessages.size() || x < 0) {
            return null;
        }
        return font.getSplitter().componentStyleAtWidth(splitMessages.get(line), x);
    }

}
