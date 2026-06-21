package com.lightning.northstar.planet.data.func.curve;

import com.lightning.northstar.planet.data.func.LevelFunction;
import com.lightning.northstar.util.LinearCurve;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LinearCurveFactory implements CurveFunction.CurveDataFactory {

    public static final String NAME = "linear";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public CurveFunction.LevelCurveData createConstantCurve(float[] x, float[] y) {
        LinearCurve curve = new LinearCurve(x, y);
        return (t, l, p) -> curve.get(t);
    }

    @Override
    public CurveFunction.LevelCurveData createDynamicCurve(float[] x, LevelFunction[] y) {
        return new DynamicLinearCurve(x, y);
    }

    public record DynamicLinearCurve(float minX, float maxX, LevelFunction y0, LevelFunction y1,
                                     float[] xs, LevelFunction[] ys) implements CurveFunction.LevelCurveData {
        public DynamicLinearCurve(float[] xs, LevelFunction[] ys) {
            this(xs[0], xs[xs.length - 1], ys[0], ys[ys.length - 1], xs, ys);
        }

        @Override
        public float get(float x, Level level, BlockPos pos) {
            if (x <= minX)
                return y0.get(level, pos);
            if (x >= maxX)
                return y1.get(level, pos);

            int index = Arrays.binarySearch(xs, x);
            if (index >= 0)
                return ys[index].get(level, pos);
            index = -index - 2;
            return Mth.map(x, xs[index], xs[index + 1], ys[index].get(level, pos), ys[index + 1].get(level, pos));
        }
    }

}
