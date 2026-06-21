package com.lightning.northstar.planet.data.func;

import com.lightning.northstar.planet.data.func.curve.CurveFunction;
import com.lightning.northstar.util.SimpleRegistry;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface LevelFunction {

    SimpleRegistry<ResourceLocation, MapCodec<? extends LevelFunction>> REGISTRY = new SimpleRegistry<>();
    Codec<LevelFunction> CODEC_NO_INLINE = ResourceLocation.CODEC.dispatch(LevelFunction::type, REGISTRY.lookup("level function"));
    Codec<LevelFunction> CODEC = Codec.either(Codec.FLOAT, CODEC_NO_INLINE)
            .xmap(
                    either -> either.map(LevelFunction::constant, Function.identity()),
                    func -> func instanceof ConstantFunction(float value) ? Either.left(value) : Either.right(func)
            );

    ResourceLocation type();

    float get(Level level, BlockPos pos);

    @ApiStatus.Internal
    static void register() {
        REGISTRY.register(ArithmeticFunction.TYPE, ArithmeticFunction.CODEC);
        REGISTRY.register(ConstantFunction.TYPE, ConstantFunction.CODEC);
        REGISTRY.register(CurveFunction.TYPE, CurveFunction.CODEC);
        REGISTRY.register(LightFunction.TYPE, LightFunction.CODEC);
        REGISTRY.register(MercuryTemperatureFunction.TYPE, MercuryTemperatureFunction.CODEC);
        REGISTRY.register(TimeFunction.TYPE, TimeFunction.CODEC);
        DispatchableFunction.BIOME.register(REGISTRY);
        DispatchableFunction.BLOCK.register(REGISTRY);
        DispatchableFunction.WEATHER.register(REGISTRY);
    }

    @Contract(value = "_ -> new", pure = true)
    default LevelFunction add(LevelFunction other) {
        return new ArithmeticFunction(ArithmeticFunction.Mode.ADD, this, other);
    }

    @Contract(value = "_ -> new", pure = true)
    default LevelFunction sub(LevelFunction other) {
        return new ArithmeticFunction(ArithmeticFunction.Mode.SUB, this, other);
    }

    @Contract(value = "_ -> new", pure = true)
    default LevelFunction mul(LevelFunction other) {
        return new ArithmeticFunction(ArithmeticFunction.Mode.MUL, this, other);
    }

    @Contract(value = "_ -> new", pure = true)
    default LevelFunction div(LevelFunction other) {
        return new ArithmeticFunction(ArithmeticFunction.Mode.DIV, this, other);
    }

    @Contract(value = "_ -> new", pure = true)
    static LevelFunction constant(float value) {
        return new ConstantFunction(value);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static LevelFunction light(LightFunction.Mode mode, boolean normalize) {
        return new LightFunction(mode, normalize);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static LevelFunction time(boolean wrap, boolean normalize) {
        return new TimeFunction(wrap, normalize);
    }

}
