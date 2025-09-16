package com.lightning.northstar.block.tech.astronomy_table;

import com.lightning.northstar.Northstar;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AstronomyTableScreen extends AbstractContainerScreen<AstronomyTableMenu> implements ContainerListener {
    private static final ResourceLocation TABLE_LOCATION = Northstar.asResource("textures/gui/astronomy_table.png");

    public AstronomyTableScreen(AstronomyTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.menu.addSlotListener(this);
    }

    @Override
    public void removed() {
        super.removed();
        this.menu.removeSlotListener(this);
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(graphics);
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        RenderSystem.disableBlend();
        this.renderTooltip(graphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);

        Component component = menu.errorMessage;
        if (component != null) {
            graphics.drawString(font, component, 5, 15, 0xff6060, true);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(TABLE_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
        graphics.blit(TABLE_LOCATION, i + 59, j + 20, 0, this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16), 110, 16);
        if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem() || this.menu.getSlot(2).hasItem()) && !this.menu.getSlot(3).hasItem()) {
            graphics.blit(TABLE_LOCATION, i + 99, j + 45, this.imageWidth, 0, 28, 21);
        }
    }

    @Override
    public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
    }

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
     * contents of that slot.
     */
    @Override
    public void slotChanged(AbstractContainerMenu pContainerToSend, int pSlotInd, ItemStack pStack) {
    }
}
