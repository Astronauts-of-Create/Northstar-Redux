package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.contraption.FuelType;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.lightning.northstar.planet.data.PlanetProperties;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class NorthstarRegistries {


    public static final ResourceKey<Registry<FuelType>> FUEL = key("fuel_type");
    public static final ResourceKey<Registry<PlanetProperties>> PLANET = key("planet");
    public static final ResourceKey<Registry<PlanetDimension>> PLANET_DIMENSION = key("planet_dimension");

    public static final Codec<ResourceKey<PlanetProperties>> PLANET_KEY_CODEC = ResourceKey.codec(PLANET);

    private static <T> ResourceKey<Registry<T>> key(String path) {
        return ResourceKey.createRegistryKey(Northstar.asResource(path));
    }

}
