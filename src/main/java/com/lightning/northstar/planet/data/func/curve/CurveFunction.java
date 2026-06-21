package com.lightning.northstar.planet.data.func.curve;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.planet.data.func.ConstantFunction;
import com.lightning.northstar.planet.data.func.LevelFunction;
import com.lightning.northstar.util.NorthstarCodecs;
import com.lightning.northstar.util.SimpleRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record CurveFunction(
        LevelFunction key,
        CurveDataFactory interpolation,
        Map<Float, LevelFunction> points,
        LevelCurveData data
) implements LevelFunction {

    public static final SimpleRegistry<String, CurveDataFactory> FACTORIES = new SimpleRegistry<>();
    public static final ResourceLocation TYPE = Northstar.asResource("curve");
    public static final MapCodec<CurveFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            LevelFunction.CODEC.fieldOf("key").forGetter(CurveFunction::key),
            Codec.STRING.fieldOf("interpolation").xmap(FACTORIES.lookup("interpolation type"), CurveDataFactory::name).forGetter(CurveFunction::interpolation),
            Codec.unboundedMap(Codec.FLOAT, LevelFunction.CODEC).fieldOf("points").forGetter(CurveFunction::points)
    ).apply(i, CurveFunction::create));

    static {
        FACTORIES.register("linear", new LinearCurveFactory());
    }

    private static CurveFunction create(LevelFunction key, CurveDataFactory interpolation, Map<Float, LevelFunction> points) {
        boolean canBeConstant = points
                .values()
                .stream()
                .allMatch(f -> f instanceof ConstantFunction);

        LevelCurveData curve;

        Map<Float, LevelFunction> orderedPoints = new TreeMap<>(Comparator.naturalOrder());
        orderedPoints.putAll(points);

        float[] x = NorthstarCodecs.floatListToArray(orderedPoints.keySet());
        if (canBeConstant) {
            float[] y = new float[x.length];
            for (int i = 0; i < y.length; i++)
                y[i] = ((ConstantFunction) orderedPoints.get(x[i])).value();
            curve = interpolation.createConstantCurve(x, y);
        } else {
            curve = interpolation.createDynamicCurve(x, orderedPoints.values().toArray(new LevelFunction[0]));
        }

        return new CurveFunction(key, interpolation, orderedPoints, curve);
    }

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public float get(Level level, BlockPos pos) {
        return data.get(key.get(level, pos), level, pos);
    }

    public interface CurveDataFactory {
        String name();

        LevelCurveData createConstantCurve(float[] x, float[] y);

        LevelCurveData createDynamicCurve(float[] x, LevelFunction[] y);
    }

    public interface LevelCurveData {
        float get(float x, Level level, BlockPos pos);
    }

}
