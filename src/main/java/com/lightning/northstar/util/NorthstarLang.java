package com.lightning.northstar.util;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.config.NorthstarConfigs;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class NorthstarLang {

    public static final LangBuilder MB = CreateLang.translate("generic.unit.millibuckets");
    public static final LangBuilder MB_PER_TICK = translate("generic.unit.millibuckets_per_tick");
    public static final LangBuilder GJ_PER_MB = translate("generic.unit.gigajoules_per_millibuckets");

    public static LangBuilder builder() {
        return new LangBuilder(Northstar.MOD_ID);
    }

    public static LangBuilder text(String text) {
        return builder().text(text);
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }

    public static LangBuilder number(double d) {
        return builder().text(LangNumberFormat.format(d));
    }

    public static LangBuilder temperature(double temperature) {
        TemperatureUnit unit = NorthstarConfigs.client().temperatureUnit.get();
        return number(unit.fromCelsius(temperature))
                .text(ChatFormatting.GRAY, unit.symbol)
                .style(ChatFormatting.DARK_GREEN);
    }

    public static LangBuilder blocks(int count) {
        return translate(count == 1 ? "generic.unit.block" : "generic.unit.blocks");
    }

    public static void addTankTooltip(List<Component> tooltip, SmartFluidTank tank) {
        addTankTooltip(tooltip, tank.getFluid(), tank.getCapacity());
    }

    public static void addTankTooltip(List<Component> tooltip, FluidStack fluid, int capacity) {
        if (!fluid.isEmpty()) {
            CreateLang.fluidName(fluid)
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
        } else {
            CreateLang.translate("gui.goggles.empty")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
        }
        CreateLang.builder()
                .add(CreateLang.number(fluid.getAmount())
                        .add(NorthstarLang.MB)
                        .style(ChatFormatting.GOLD))
                .text(ChatFormatting.GRAY, " / ")
                .add(CreateLang.number(capacity)
                        .add(NorthstarLang.MB)
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);
    }

}
