package com.lightning.northstar.planet.data.func;

import com.lightning.northstar.Northstar;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatBinaryOperator;
import net.createmod.catnip.lang.Lang;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record ArithmeticFunction(Mode mode, LevelFunction a, LevelFunction b) implements LevelFunction {

    public static final ResourceLocation TYPE = Northstar.asResource("arithmetic");
    public static final MapCodec<ArithmeticFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Mode.CODEC.fieldOf("mode").forGetter(ArithmeticFunction::mode),
            LevelFunction.CODEC.fieldOf("a").forGetter(ArithmeticFunction::a),
            LevelFunction.CODEC.fieldOf("b").forGetter(ArithmeticFunction::b)
    ).apply(i, ArithmeticFunction::new));

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public float get(Level level, BlockPos pos) {
        return mode.operator.apply(a.get(level, pos), b.get(level, pos));
    }

    public enum Mode implements StringRepresentable {
        ADD((a, b) -> a + b),
        SUB((a, b) -> a - b),
        MUL((a, b) -> a * b),
        DIV((a, b) -> a / b),
        MOD((a, b) -> a % b),
        EMOD((a, b) -> ((a % b) + b) % b);

        public final FloatBinaryOperator operator;

        Mode(FloatBinaryOperator operator) {
            this.operator = operator;
        }

        public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);

        @Override
        public String getSerializedName() {
            return Lang.asId(name());
        }
    }

}
