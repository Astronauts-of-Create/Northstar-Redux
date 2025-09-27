package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.contraption.FuelType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class NorthstarRegistries {

    public static final ResourceKey<Registry<FuelType>> FUEL = ResourceKey.createRegistryKey(Northstar.asResource("fuel_type"));

}
