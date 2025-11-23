package com.lightning.northstar.api.data.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record DimensionInfo(
        double gravity,
        int temperature,
        int atmosphereCost,
        int computingCost,
        boolean hasOxygen,
        boolean hasSky,
        boolean canSeeSkyAtDay,
        boolean hasWeather,
        double engineConstant,
        long seedOffset,
        float sunMultiplier
) {
    public static final Codec<DimensionInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("gravity", 1D).forGetter(DimensionInfo::gravity),
            Codec.INT.optionalFieldOf("temperature", 15).forGetter(DimensionInfo::temperature),
            Codec.INT.optionalFieldOf("atmosphere_cost", 0).forGetter(DimensionInfo::atmosphereCost),
            Codec.INT.optionalFieldOf("computing_cost", 0).forGetter(DimensionInfo::computingCost),
            Codec.BOOL.optionalFieldOf("has_oxygen", true).forGetter(DimensionInfo::hasOxygen),
            Codec.BOOL.optionalFieldOf("has_sky", false).forGetter(DimensionInfo::hasSky),
            Codec.BOOL.optionalFieldOf("can_see_sky_at_day", false).forGetter(DimensionInfo::canSeeSkyAtDay),
            Codec.BOOL.optionalFieldOf("has_weather", true).forGetter(DimensionInfo::hasWeather),
            Codec.DOUBLE.optionalFieldOf("engine_constant", 1D).forGetter(DimensionInfo::engineConstant),
            Codec.LONG.optionalFieldOf("seed_offset", 0L).forGetter(DimensionInfo::seedOffset),
            Codec.FLOAT.optionalFieldOf("sun_multiplier", 1F).forGetter(DimensionInfo::sunMultiplier)
    ).apply(instance, DimensionInfo::new));

    public boolean hasNormalGravity() {
        return this.gravity == 1;
    }

    @Contract(" -> new")
    public static @NotNull DimensionInfo getDefault() {
        return new DimensionInfo(
                1,
                15,
                0,
                0,
                true,
                false,
                false,
                true,
                1,
                0,
                1
        );
    }
}
