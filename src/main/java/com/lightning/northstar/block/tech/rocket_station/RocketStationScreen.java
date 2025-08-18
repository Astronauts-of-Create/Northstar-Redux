package com.lightning.northstar.block.tech.rocket_station;

import com.google.common.collect.Lists;
import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarPackets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
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

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RocketStationScreen extends AbstractContainerScreen<RocketStationMenu> implements ContainerListener {

    private final ResourceLocation TABLE_LOCATION = Northstar.asResource("textures/gui/rocket_station.png");

    public RocketStationScreen(RocketStationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(graphics);
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        RenderSystem.disableBlend();
        this.renderFg(graphics, pMouseX, pMouseY, pPartialTick);
        this.renderButtons(graphics, pMouseX, pMouseY, pPartialTick);
        this.renderCost(graphics, pPartialTick);
        this.renderTooltip(graphics, pMouseX, pMouseY);
    }

    protected void renderCost(GuiGraphics pPoseStack, float delta) {
        this.menu.slotsChanged(this.menu.container);
        int x = ((width - (imageWidth + (imageWidth / 2))) / 2);
        int y = (height - (imageHeight + (imageHeight / 2))) / 2;
        if (this.menu.blockEntity == null) {
            System.out.println("Ruh roh");
        }
        if (this.menu.target != null)
            pPoseStack.drawString(font, Component.literal("Estimated Fuel Cost: " + this.menu.fuelCost + " gJ"), x + imageWidth - 110, y + imageWidth - 100, 0x313a54);
        if (this.menu.target == null)
            pPoseStack.drawString(font, Component.literal("Invalid Target"), x + imageWidth - 40, y + imageWidth - 100, 0x313a54);
    }

    protected void renderButtons(GuiGraphics pPoseStack, int mouseX, int mouseY, float delta) {
        int x = ((width - (imageWidth + (imageWidth / 2))) / 2);
        int y = (height - (imageHeight + (imageHeight / 2))) / 2;

        IconButton assemble = new IconButton(x + imageWidth - 10, y + imageHeight - 79, AllIcons.I_ADD);
        assemble.setToolTip(CreateLang.translateDirect("station.assemble_train"));
        assemble.withCallback(() -> {
            NorthstarPackets.getChannel().sendToServer(RocketStationEditPacket.tryAssemble(menu.blockEntity.getBlockPos()));
            removed();
            onClose();
        });
        assemble.render(pPoseStack, mouseX, mouseY, delta);
        addRenderableWidget(assemble);

        if ((Math.abs(x + imageWidth - mouseX) < 9 && Math.abs(y + imageHeight - 79 + 9 - mouseY) < 9)) {
            List<Component> list = Lists.newArrayList();
            RenderSystem.colorMask(true, true, true, true);
            list.add(CreateLang.translateDirect("northstar.gui.rocket_station.assemble").withStyle(ChatFormatting.WHITE));

            pPoseStack.renderComponentTooltip(font, list, mouseX, mouseY);
        }

    }

    protected void assemble() {
        if (this.menu != null) {
            this.menu.assemble();
        } else {
            System.out.println("no block entity :(");
        }
        if (this.menu == null) {
            System.out.println("no menu?? :(");
        }
    }

    protected void renderFg(GuiGraphics pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {

    }

    @Override
    protected void renderLabels(GuiGraphics pPoseStack, int pX, int pY) {
        RenderSystem.disableBlend();
        super.renderLabels(pPoseStack, pX, pY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(TABLE_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
        graphics.blit(TABLE_LOCATION, i + 59, j + 20, 0, this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16), 110, 16);
        if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(2).hasItem()) {
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
