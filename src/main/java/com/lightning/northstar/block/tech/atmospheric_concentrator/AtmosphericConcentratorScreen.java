package com.lightning.northstar.block.tech.atmospheric_concentrator;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.client.gui.ListScrollInput;
import com.lightning.northstar.planet.data.AtmosphereFluid;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AtmosphericConcentratorScreen extends AbstractSimiScreen {

    private static final ResourceLocation BACKGROUND_LOCATION = Northstar.asResource("textures/gui/atmospheric_concentrator.png");

    private final AtmosphericConcentratorBlockEntity be;

    public AtmosphericConcentratorScreen(AtmosphericConcentratorBlockEntity be) {
        this.be = be;

        setWindowSize(176, 40);
    }

    @Override
    protected void init() {
        super.init();

        Label label = new Label(guiLeft + 10, guiTop + 10, Component.empty())
                .withShadow();

        ListScrollInput<AtmosphereFluid> dimensionSelection = new ListScrollInput<>(guiLeft + 5, guiTop + 5, 147, 18);
        dimensionSelection.formatter(fluid -> fluid.asFluidStack(1).getHoverName())
                .options(be.getLevel().northstar$dimension().atmosphere().composition())
                .writingTo(label);
        dimensionSelection.setState(be.getCollectedFluid());
        addRenderableWidget(label);
        addRenderableWidget(dimensionSelection);

        IconButton saveButton = new IconButton(guiLeft + 153, guiTop + 5, AllIcons.I_CONFIRM);
        saveButton.setToolTip(Component.translatable("northstar.gui.atmospheric_concentrator.save"));
        saveButton.withCallback(() -> {
            onClose();
            CatnipServices.NETWORK.sendToServer(new AtmosphericConcentratorEditPacket(be.getBlockPos(), dimensionSelection.get().asFluidStack(1)));
        });
        addRenderableWidget(saveButton);
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(BACKGROUND_LOCATION, guiLeft, guiTop, 0, 0, windowWidth, windowHeight, 256, 64);
    }

}
