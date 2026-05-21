package com.lightning.northstar.planet.data.func;

import com.lightning.northstar.Northstar;
import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record ConstantFunction(float value) implements LevelFunction {

    public static final ResourceLocation TYPE = Northstar.asResource("constant");
    public static final Codec<ConstantFunction> CODEC = Codec.FLOAT.xmap(ConstantFunction::new, ConstantFunction::value);

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public float get(Level level, BlockPos pos) {
        return value;
    }

}
