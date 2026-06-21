package com.lightning.northstar.planet.data.func;

import com.lightning.northstar.Northstar;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record ConstantFunction(float value) implements LevelFunction {

    public static final ResourceLocation TYPE = Northstar.asResource("constant");
    public static final MapCodec<ConstantFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.fieldOf("value").forGetter(ConstantFunction::value)
    ).apply(i, ConstantFunction::new));

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public float get(Level level, BlockPos pos) {
        return value;
    }

}
