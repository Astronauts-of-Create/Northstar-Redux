package com.lightning.northstar.block.tech.temperature_regulator;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.util.TemperatureUnit;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TemperatureRegulatorScreen extends AbstractSimiScreen {

    private static final ResourceLocation BACKGROUND = Northstar.asResource("textures/gui/temperature_regulator.png");

    private final BaseTemperatureRegulator regulator;
    private final int entityId;
    private final BlockPos pos;

    public TemperatureRegulatorScreen(BaseTemperatureRegulator regulator, int entityId, BlockPos pos) {
        super(NorthstarBlocks.TEMPERATURE_REGULATOR.get().getName());
        this.regulator = regulator;
        this.entityId = entityId;
        this.pos = pos;

        setWindowSize(204, entityId == -1 ? 40 : 60);
    }

    @Override
    protected void init() {
        super.init();

        int x = guiLeft, y = guiTop;

        ScrollInput sizeX = addScrollInput(new ScrollInput(x + 29, y + 11, 25, 18), regulator.sizeX + 1, "Width (X)");
        ScrollInput sizeY = addScrollInput(new ScrollInput(x + 58, y + 11, 25, 18), regulator.sizeY + 1, "Height (Y)");
        ScrollInput sizeZ = addScrollInput(new ScrollInput(x + 87, y + 11, 25, 18), regulator.sizeZ + 1, "Depth (Z)");

        // condition is negated because a click is simulated to sync the states of everything
        boolean[] limit = { regulator.bounds.minX == Integer.MIN_VALUE };

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

        ScrollInput temperature = addScrollInput(new ScrollInput(x + 129, y + 11, 46, 18), 0, "Temperature")
                .addHint(Component.translatable("northstar.gui.temperature_regulator.step")
                        .withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY))
                .withRange((int) unit.fromCelsius(NorthstarTemperature.MINIMUM_TEMPERATURE), (int) unit.fromCelsius(NorthstarTemperature.MAXIMUM_TEMPERATURE + 1))
                .setState((int) unit.fromCelsius(regulator.temperature))
                .withStepFunction(s -> s.shift ? 100 : s.control ? 20 : 1)
                .format(i -> Component.literal(LangNumberFormat.format(i) + unit.symbol));
        temperature.onChanged();

        IconButton confirm = new IconButton(x + 179, y + 11, AllIcons.I_CONFIRM);
        confirm.setToolTip(Component.translatable("northstar.generic.confirm"));
        confirm.withCallback(() -> {
            NorthstarPackets.getChannel().sendToServer(new TemperatureRegulatorEditPacket(entityId, pos,
                    (int) unit.toCelsius(temperature.getState()),
                    limit[0], sizeX.getState() - 1, sizeY.getState() - 1, sizeZ.getState() - 1));
            onClose();
        });
        addRenderableWidget(confirm);

        if (entityId != -1) {
            IconButton showLeak = new IconButton(x + 179, y + 31, AllIcons.I_ACTIVE);
            showLeak.setToolTip(Component.translatable("northstar.gui.sealer.toggle_leak"));
            showLeak.withCallback(() -> regulator.showLeak = !regulator.showLeak);
            addRenderableWidget(showLeak);
        }
    }

    private ScrollInput addScrollInput(ScrollInput input, int value, String name) {
        Label label = new Label(0, input.getY() + 6, Component.empty())
                .withShadow();
        input.withRange(1, TemperatureRegulatorBlockEntity.MAX_LIMIT_SIZE + 2)
                .calling(i -> label.setX(input.getX() + input.getWidth() / 2 - font.width(label.text) / 2))
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
        if (entityId == -1) {
            graphics.blit(BACKGROUND, guiLeft, guiTop, 0, 0, windowWidth, windowHeight, 256, 64);
        } else {
            graphics.blit(BACKGROUND, guiLeft, guiTop, 0, 0, windowWidth, 36, 256, 64);
            graphics.blit(BACKGROUND, guiLeft, guiTop + 36, 0, 40, windowWidth, 24, 256, 64);

            Component status = regulator.sealer.hasLeak() ?
                    Component.translatable("northstar.gui.goggles.sealer.area_too_big").withStyle(ChatFormatting.RED) :
                    Component.translatable("northstar.gui.oxygen_sealer.sealed").withStyle(ChatFormatting.GREEN);
            MutableComponent line1 = Component.translatable("northstar.generic.status").append(status);
            MutableComponent line2 = regulator.sealer.hasLeak() ?
                    NorthstarLang.translate("gui.goggles.sealer.max_sealed_contraption")
                            .add(CreateLang.number(NorthstarConfigs.server().temperatureRegulatorMaxContraptionSealed.get())
                                    .style(ChatFormatting.BLUE))
                            .text(" blocks")
                            .component() :
                    NorthstarLang.translate("gui.goggles.sealer.blocks_filled")
                            .add(CreateLang.number(regulator.sealer.getSealedBlockCount())
                                    .style(ChatFormatting.BLUE))
                            .component();

            graphics.drawString(font, line1, guiLeft + 5, guiTop + 34, 0xFFFFFFFF);
            graphics.drawString(font, line2, guiLeft + 5, guiTop + 44, 0xFFFFFFFF);
        }
    }

}
