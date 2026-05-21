package com.lightning.northstar.util;

import net.createmod.catnip.lang.LangNumberFormat;

import java.util.function.DoubleUnaryOperator;

public enum PressureUnit {

    PASCAL("Pa", 1),
    BAR("Bar", 100000),
    ATMOSPHERE("atm", 101325),
    PSI("psi", 6895),
    MM_OF_MERCURY("mmHg", 133.3),
    INCH_OF_MERCURY("inHg", 3386);

    public final String symbol;
    public final DoubleUnaryOperator fromPascal;
    public final DoubleUnaryOperator toPascal;

    PressureUnit(String symbol, double pascalScale) {
        this(symbol, d -> d / pascalScale, d -> d * pascalScale);
    }

    PressureUnit(String symbol, DoubleUnaryOperator fromPascal, DoubleUnaryOperator toPascal) {
        this.symbol = symbol;
        this.fromPascal = fromPascal;
        this.toPascal = toPascal;
    }

    public float fromPascal(float pascal) {
        return (float) fromPascal.applyAsDouble(pascal);
    }

    public double fromPascal(double pascal) {
        return fromPascal.applyAsDouble(pascal);
    }

    public float toPascal(float pascal) {
        return (float) toPascal.applyAsDouble(pascal);
    }

    public double toPascal(double pascal) {
        return toPascal.applyAsDouble(pascal);
    }

    public String format(float pressure) {
        return LangNumberFormat.format(fromPascal(pressure)) + symbol;
    }

    public String format(double pressure) {
        return LangNumberFormat.format(fromPascal(pressure)) + symbol;
    }

}
