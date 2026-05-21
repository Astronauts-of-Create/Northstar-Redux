package com.lightning.northstar.planet.data.func;

import com.lightning.northstar.Northstar;
import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.ParametersAreNonnullByDefault;

/** Temporary implementation of Mercury's temperature function until the expression functions are fully ready */
@ApiStatus.Internal
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MercuryTemperatureFunction implements LevelFunction {

    public static final ResourceLocation TYPE = Northstar.asResource("internal/mercury_temperature");
    public static final Codec<MercuryTemperatureFunction> CODEC = Codec.unit(new MercuryTemperatureFunction());

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public float get(Level level, BlockPos pos) {
        return level.canSeeSky(pos) && !level.isNight() ? 434 : -200;
    }
}
