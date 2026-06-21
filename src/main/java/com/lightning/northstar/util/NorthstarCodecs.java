package com.lightning.northstar.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector2f;
import org.joml.Vector2i;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NorthstarCodecs {

    public static final Codec<FluidStack> FLUID_STACK_OR_FLUID = Codec.either(BuiltInRegistries.FLUID.byNameCodec(), FluidStack.CODEC).xmap(
            either -> either.map(fluid -> new FluidStack(fluid, 1), Function.identity()),
            stack -> stack.getComponents().isEmpty() ? Either.left(stack.getFluid()) : Either.right(stack)
    );

    /** Serializes angles to degrees and deserializes back in radians */
    public static final Codec<Double> DOUBLE_DEG_RAD = Codec.DOUBLE.xmap(Math::toRadians, Math::toDegrees);

    public static final Codec<Vector2i> VECTOR2I = Codec.INT
            .listOf()
            .comapFlatMap(
                    l -> Util.fixedSize(l, 2).map(v -> new Vector2i(v.get(0), v.get(1))),
                    v -> List.of(v.x(), v.y()));

    public static final Codec<Vector2f> VECTOR2F = Codec.FLOAT
            .listOf()
            .comapFlatMap(
                    l -> Util.fixedSize(l, 2).map(v -> new Vector2f(v.get(0), v.get(1))),
                    v -> List.of(v.x(), v.y()));

    public static final Codec<float[]> FLOAT_ARRAY = Codec.FLOAT
            .listOf()
            .xmap(NorthstarCodecs::floatListToArray,
                    NorthstarCodecs::floatArrayToList);

    public static <T> Codec<List<T>> listOrSingle(Codec<T> codec) {
        return Codec.either(codec, codec.listOf())
                .xmap(either -> either.map(List::of, Function.identity()),
                        list -> list.size() == 1 ? Either.left(list.get(0)) : Either.right(list));
    }

    public static <T> Codec<Set<T>> setOrSingle(Codec<T> codec) {
        return listOrSingle(codec).xmap(HashSet::new, ArrayList::new);
    }

    public static <K, V> Codec<Pair<K, V>> keyValuePair(Codec<K> key, Codec<V> value) {
        return RecordCodecBuilder.create(i -> i.group(
                key.fieldOf("key").forGetter(Pair::left),
                value.fieldOf("value").forGetter(Pair::right)
        ).apply(i, Pair::of));
    }

    public static float[] floatListToArray(Collection<Float> list) {
        float[] array = new float[list.size()];
        int i = 0;
        for (Float f : list) {
            array[i++] = f;
        }
        return array;
    }

    public static List<Float> floatArrayToList(float[] array) {
        List<Float> list = new ArrayList<>(array.length);
        for (float f : array) {
            list.add(f);
        }
        return list;
    }

    public static <B extends ByteBuf, T> StreamCodec<B, T> nullableStream(StreamCodec<B, T> delegate) {
        return new StreamCodec<>() {
            @Override
            public T decode(B buffer) {
                if (buffer.readBoolean()) {
                    return delegate.decode(buffer);
                }
                return null;
            }

            @Override
            public void encode(B buffer, T value) {
                if (value != null) {
                    buffer.writeBoolean(true);
                    delegate.encode(buffer, value);
                } else {
                    buffer.writeBoolean(false);
                }
            }
        };
    }

}
