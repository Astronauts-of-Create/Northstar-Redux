package com.lightning.northstar.world.dimension;

import com.lightning.northstar.Northstar;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class NorthstarDimensions {

    // earth orbit
    public static final ResourceKey<Level> EARTH_ORBIT_DIM_KEY = ResourceKey.create(Registries.DIMENSION, Northstar.asResource("earth_orbit"));
    public static final ResourceKey<DimensionType> EARTH_ORBIT_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, Northstar.asResource("earth_orbit"));
    // mars
    public static final ResourceKey<Level> MARS_DIM_KEY = ResourceKey.create(Registries.DIMENSION, Northstar.asResource("mars"));
    public static final ResourceKey<DimensionType> MARS_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, Northstar.asResource("mars"));
    // venus
    public static final ResourceKey<Level> VENUS_DIM_KEY = ResourceKey.create(Registries.DIMENSION, Northstar.asResource("venus"));
    public static final ResourceKey<DimensionType> VENUS_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, Northstar.asResource("venus"));
    // moon
    public static final ResourceKey<Level> MOON_DIM_KEY = ResourceKey.create(Registries.DIMENSION, Northstar.asResource("moon"));
    public static final ResourceKey<DimensionType> MOON_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, Northstar.asResource("moon"));
    // mercury
    public static final ResourceKey<Level> MERCURY_DIM_KEY = ResourceKey.create(Registries.DIMENSION, Northstar.asResource("mercury"));
    public static final ResourceKey<DimensionType> MERCURY_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, Northstar.asResource("mercury"));

    public static void register() {
    }

}
