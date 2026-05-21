package com.lightning.northstar.mixin.client;

import com.lightning.northstar.accessor.NorthstarGuiGraphics;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin implements NorthstarGuiGraphics {

    // Unfortunately can't make use of RegisterItemDecorationsEvent as it's only called ONCE on startup before anything useful is loaded
    @Inject(
            method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
            )
    )
    public void northstar$onRenderItemDecorations(Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        if (NorthstarItemTags.OXYGEN_SOURCES.matches(stack)) {
            GuiGraphics self = (GuiGraphics) (Object) this;

            int oxygen = stack.getOrCreateTag().getInt("Oxygen");
            float fraction = (float) oxygen / (float) NorthstarOxygen.MAXIMUM_OXYGEN;

            int width = (int) (13 * fraction);
            int barX = x + 2;
            int barY = y + 14; // 13 by default
            self.fill(RenderType.guiOverlay(), barX, barY, barX + 13, barY + 2, 0xFF000000);
            self.fill(RenderType.guiOverlay(), barX, barY, barX + width, barY + 1, 0xFF4D98FA);
        }
    }

}
