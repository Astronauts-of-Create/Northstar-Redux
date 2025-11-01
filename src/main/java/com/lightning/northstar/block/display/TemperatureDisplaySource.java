package com.lightning.northstar.block.display;

import com.lightning.northstar.util.TemperatureUnit;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.LangNumberFormat;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

public class TemperatureDisplaySource extends SingleLineDisplaySource {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine)
            return;

        builder.addSelectionScrollInput(0, 137, (input, label) -> {
            input.forOptions(Arrays.stream(TemperatureUnit.ALL)
                    .map(unit -> unit.nameAndSymbol)
                    .toList());
            input.titled(Component.translatable("northstar.gui.temperature_unit"));
        }, "Unit");
    }

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        float temperature = NorthstarTemperature.getTemperatureAt(context.level(), context.getSourceBlockEntity().getBlockPos());
        TemperatureUnit unit = TemperatureUnit.ALL[context.sourceConfig().getInt("Unit")];

        return Component.literal(LangNumberFormat.format(unit.fromCelsius(temperature))).append(unit.symbol);
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

}
