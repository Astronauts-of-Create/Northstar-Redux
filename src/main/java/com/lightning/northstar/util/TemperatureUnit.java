package com.lightning.northstar.util;

import net.createmod.catnip.lang.Lang;
import com.simibubi.create.foundation.utility.LangNumberFormat;
import net.minecraft.network.chat.Component;

import java.util.function.DoubleUnaryOperator;

public enum TemperatureUnit {

    CELSIUS("°C", d -> d, d -> d),
    FAHRENHEIT("°F", d -> d * 9 / 5 + 32, d -> (d - 32) * 5 / 9),
    // Yes it should be 273.5 but since we usually uses integers for Celsius use 273 to avoid the .5
    KELVIN("K", d -> d + 273, d -> d - 273);

    public static final TemperatureUnit[] ALL = values();

    public final Component name;
    public final Component nameAndSymbol;
    public final String symbol;
    public final DoubleUnaryOperator fromCelsius;
    public final DoubleUnaryOperator toCelsius;

    TemperatureUnit(String symbol, DoubleUnaryOperator fromCelsius, DoubleUnaryOperator toCelsius) {
        this.name = Component.translatable("northstar.temperature_unit." + Lang.asId(name()));
        this.nameAndSymbol = name.copy().append(" (" + symbol + ")");
        this.symbol = symbol;
        this.fromCelsius = fromCelsius;
        this.toCelsius = toCelsius;
    }

    public float fromCelsius(float celsius) {
        return (float) fromCelsius.applyAsDouble(celsius);
    }

    public double fromCelsius(double celsius) {
        return fromCelsius.applyAsDouble(celsius);
    }

    public float toCelsius(float celsius) {
        return (float) toCelsius.applyAsDouble(celsius);
    }

    public double toCelsius(double celsius) {
        return toCelsius.applyAsDouble(celsius);
    }

    public String format(float temperature) {
        return LangNumberFormat.format(fromCelsius(temperature)) + symbol;
    }

    public String format(double temperature) {
        return LangNumberFormat.format(fromCelsius(temperature)) + symbol;
    }

}
