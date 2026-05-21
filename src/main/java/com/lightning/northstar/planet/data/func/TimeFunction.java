package com.lightning.northstar.planet.data.func;

import com.lightning.northstar.Northstar;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record TimeFunction(boolean wrap, boolean days) implements LevelFunction {

    public static final ResourceLocation TYPE = Northstar.asResource("time");
    public static final Codec<TimeFunction> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.optionalFieldOf("wrap", true).forGetter(TimeFunction::wrap),
            Codec.BOOL.optionalFieldOf("days", false).forGetter(TimeFunction::days)
    ).apply(i, TimeFunction::new));

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public float get(Level level, BlockPos pos) {
        long value = wrap ? level.getDayTime() % 24000L : level.getDayTime();
        return days ? value / 24000f : value;
    }

}
