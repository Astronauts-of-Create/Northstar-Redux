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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AstronomyTableScreen extends AbstractContainerScreen<AstronomyTableMenu> implements ContainerListener {
    private static final ResourceLocation TABLE_LOCATION = Northstar.asResource("textures/gui/astronomy_table.png");

    public AstronomyTableScreen(AstronomyTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    protected void subInit() {
    }

    @Override
    protected void init() {
        super.init();
        this.subInit();
        this.menu.addSlotListener(this);
    }

    @Override
    public void removed() {
        super.removed();
        this.menu.removeSlotListener(this);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        RenderSystem.disableBlend();
        this.renderFg(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    protected void renderFg(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int pX, int pY) {
        RenderSystem.disableBlend();
        super.renderLabels(graphics, pX, pY);

        Component component = menu.errorMessage;
        if (component != null) {
            int k = this.imageWidth - 8 - this.font.width(component) - 2;
            graphics.fill(k - 2, 67, this.imageWidth - 8, 79, 1325400064);
            graphics.drawString(font, component, k, 69, 16736352, true);
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
