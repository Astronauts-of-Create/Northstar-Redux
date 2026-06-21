package com.lightning.northstar.planet.data.func;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarWeathers;
import com.lightning.northstar.util.NorthstarCodecs;
import com.lightning.northstar.util.SimpleRegistry;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record DispatchableFunction<T>(
        ResourceLocation type,
        BiFunction<Level, BlockPos, T> key,
        Map<T, LevelFunction> entries,
        LevelFunction fallback
) implements LevelFunction {

    /** The key used to mark fallback entries. */
    public static final ResourceLocation FALLBACK_KEY = ResourceLocation.withDefaultNamespace("fallback");
    /** A codec that only accepts {@link #FALLBACK_KEY} or fails. */
    public static final Codec<ResourceLocation> FALLBACK_KEY_CODEC;

    static {
        Function<ResourceLocation, DataResult<ResourceLocation>> checker = l -> l.equals(FALLBACK_KEY) ?
                DataResult.success(l) :
                DataResult.error(() -> "Expected \"" + FALLBACK_KEY + "\"");
        FALLBACK_KEY_CODEC = ResourceLocation.CODEC.flatXmap(checker, checker);
    }

    public static final Factory<ResourceLocation> BIOME = new Factory<>(Northstar.asResource("biome"), ResourceLocation.CODEC,
            (level, pos) -> level.getBiome(pos).getKey().location());
    public static final Factory<Block> BLOCK = new Factory<>(Northstar.asResource("block"), BuiltInRegistries.BLOCK.byNameCodec(),
            (level, pos) -> level.getBlockState(pos).getBlock());
    public static final Factory<ResourceLocation> WEATHER = new Factory<>(Northstar.asResource("weather"), ResourceLocation.CODEC,
            (level, pos) -> level.isRainingAt(pos) ? level.isThundering() ? NorthstarWeathers.THUNDERING : NorthstarWeathers.RAINING : NorthstarWeathers.CLEAR);

    public static <T> MapCodec<DispatchableFunction<T>> codec(Codec<T> codec, Function<Pair<LevelFunction, Map<T, LevelFunction>>, DispatchableFunction<T>> constructor) {
        // this is... special (and used to be worse as a one-liner)
        // generics go brrr

        Function<List<Pair<List<Either<ResourceLocation, T>>, LevelFunction>>, DataResult<Pair<LevelFunction, Map<T, LevelFunction>>>> decode = entries -> {
            LevelFunction fallback = null;
            Map<T, LevelFunction> values = new HashMap<>();

            for (Pair<List<Either<ResourceLocation, T>>, LevelFunction> pair : entries) {
                for (Either<ResourceLocation, T> key : pair.left()) {
                    if (key.left().isPresent()) {
                        if (fallback != null)
                            return DataResult.error(() -> "Found multiple fallback entries");
                        fallback = pair.right();
                    } else {
                        T k = key.right().orElseThrow();
                        if (values.put(k, pair.right()) != null) {
                            return DataResult.error(() -> "Found multiple entries referencing \"" + k + "\"");
                        }
                    }
                }
            }

            if (fallback == null)
                return DataResult.error(() -> "No fallback entry");

            return DataResult.success(Pair.of(fallback, values));
        };

        Function<Pair<LevelFunction, Map<T, LevelFunction>>, DataResult<List<Pair<List<Either<ResourceLocation, T>>, LevelFunction>>>> encode = pair -> {
            Map<LevelFunction, Pair<List<Either<ResourceLocation, T>>, LevelFunction>> values = new HashMap<>();
            values.put(pair.left(), Pair.of(List.of(Either.left(FALLBACK_KEY)), pair.left()));

            for (Map.Entry<T, LevelFunction> entry : pair.right().entrySet()) {
                values.computeIfAbsent(entry.getValue(), f -> Pair.of(new ArrayList<>(), f)).left().add(Either.right(entry.getKey()));
            }

            return DataResult.success(List.copyOf(values.values()));
        };

        return RecordCodecBuilder.mapCodec(i -> i.group(
                NorthstarCodecs.listOrSingle(
                                NorthstarCodecs.keyValuePair(
                                        NorthstarCodecs.listOrSingle(Codec.either(
                                                FALLBACK_KEY_CODEC,
                                                codec)
                                        ),
                                        LevelFunction.CODEC
                                )
                        )
                        .flatXmap(decode, encode)
                        .fieldOf("entries")
                        .forGetter(f -> Pair.of(f.fallback(), f.entries()))
        ).apply(i, constructor));
    }

    @Override
    public float get(Level level, BlockPos pos) {
        return entries.getOrDefault(key.apply(level, pos), fallback).get(level, pos);
    }

    public static class Factory<T> {
        private final ResourceLocation type;
        private final BiFunction<Level, BlockPos, T> key;
        private final MapCodec<DispatchableFunction<T>> codec;

        public Factory(ResourceLocation type, Codec<T> keyCodec, BiFunction<Level, BlockPos, T> key) {
            this.type = type;
            this.key = key;
            this.codec = DispatchableFunction.codec(keyCodec, pair -> create(pair.left(), pair.right()));
        }

        public void register(SimpleRegistry<ResourceLocation, MapCodec<? extends LevelFunction>> registry) {
            registry.register(type, codec);
        }

        public DispatchableFunction<T> create(LevelFunction fallback, Map<T, LevelFunction> entries) {
            return new DispatchableFunction<>(type, key, entries, fallback);
        }
    }

}
