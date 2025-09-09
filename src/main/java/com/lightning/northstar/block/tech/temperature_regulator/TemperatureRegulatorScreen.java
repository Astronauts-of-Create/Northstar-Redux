package com.lightning.northstar.block.tech.temperature_regulator;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.content.NorthstarTechBlocks;
import com.lightning.northstar.util.TemperatureUnit;
import com.lightning.northstar.world.NorthstarTemperature;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TemperatureRegulatorScreen extends AbstractSimiScreen {

    private static final ResourceLocation BACKGROUND = Northstar.asResource("textures/gui/temperature_regulator.png");

    private final TemperatureRegulatorBlockEntity be;

    public TemperatureRegulatorScreen(TemperatureRegulatorBlockEntity be) {
        super(NorthstarTechBlocks.TEMPERATURE_REGULATOR.get().getName());
        this.be = be;

        setWindowSize(204, 40);
    }

    @Override
    protected void init() {
        super.init();

        int x = guiLeft, y = guiTop;

        ScrollInput sizeX = addScrollInput(new ScrollInput(x + 29, y + 11, 25, 18), be.sizeX, "Width (X)");
        ScrollInput sizeY = addScrollInput(new ScrollInput(x + 58, y + 11, 25, 18), be.sizeY, "Height (Y)");
        ScrollInput sizeZ = addScrollInput(new ScrollInput(x + 87, y + 11, 25, 18), be.sizeZ, "Depth (Z)");

        // condition is negated because a click is simulated to sync the states of everything
        boolean[] limit = { be.bounds.minX == Integer.MIN_VALUE };

        IconButton fill = new IconButton(x + 7, y + 11, AllIcons.I_NONE);
        fill.setToolTip(Component.literal("Enable limits"));
        fill.withCallback(() -> {
            limit[0] = !limit[0];
            fill.setIcon(limit[0] ? AllIcons.I_CONFIRM : AllIcons.I_DISABLE);
            sizeX.active = limit[0];
            sizeY.active = limit[0];
            sizeZ.active = limit[0];
        });
        fill.onClick(0, 0);
        addRenderableWidget(fill);

        TemperatureUnit unit = NorthstarConfigs.client().temperatureUnit.get();

        ScrollInput temperature = addScrollInput(new ScrollInput(x + 134, y + 11, 41, 18), 0, "Temperature")
                .addHint(Component.translatable("northstar.gui.temperature_regulator.step")
                        .withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY))
                .withRange(NorthstarTemperature.MINIMUM_TEMPERATURE, NorthstarTemperature.MAXIMUM_TEMPERATURE + 1)
                .setState((int) be.targetTemperature)
                .withStepFunction(s -> s.shift ? 100 : s.control ? 20 : 1)
                .format(i -> Component.literal(LangNumberFormat.format(unit.fromCelsius(i)) + unit.symbol));
        temperature.onChanged();

        IconButton confirm = new IconButton(x + 179, y + 11, AllIcons.I_CONFIRM);
        confirm.setToolTip(Component.translatable("northstar.generic.confirm"));
        confirm.withCallback(() -> {
            NorthstarPackets.getChannel().sendToServer(new TemperatureRegulatorEditPacket(be.getBlockPos(), temperature.getState(), limit[0], sizeX.getState(), sizeY.getState(), sizeZ.getState()));
            onClose();
        });
        addRenderableWidget(confirm);
    }

    private ScrollInput addScrollInput(ScrollInput input, int value, String name) {
        Label label = new Label(0, input.getY() + 6, Component.empty())
                .withShadow();
        input.withRange(1, TemperatureRegulatorBlockEntity.MAX_LIMIT_SIZE + 1)
                .calling(i -> label.setX(input.getX() + input.getWidth() / 2 - Minecraft.getInstance().font.width(label.text) / 2))
                .writingTo(label)
                .titled(Component.literal(name))
                .setState(value)
                .onChanged();
        addRenderableWidget(label);
        addRenderableWidget(input);
        return input;
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(BACKGROUND, guiLeft, guiTop, 0, 0, windowWidth, windowHeight);
    }

}
