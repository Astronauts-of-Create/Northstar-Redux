package com.lightning.northstar.mixin.client;

import com.lightning.northstar.accessor.NorthstarGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin implements NorthstarGuiGraphics {
}
