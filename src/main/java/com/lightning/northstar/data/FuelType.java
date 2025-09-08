package com.lightning.northstar.data;

import com.lightning.northstar.content.NorthstarRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;

public record FuelType(
        HolderSet<Fluid> fluids,
        float rocketMultiplier,
        int combustionEngineEfficiency,
        float combustionEngineRpm) {

    //Todo:
    public static final Codec<FuelType> CODEC = RecordCodecBuilder.create(i -> i.group(
            RegistryCodecs.homogeneousList(Registries.FLUID).fieldOf("fluids").forGetter(FuelType::fluids),
            Codec.FLOAT.fieldOf("rocket_multiplier").forGetter(FuelType::rocketMultiplier),
            Codec.INT.fieldOf("combustion_engine_use").forGetter(FuelType::combustionEngineEfficiency),
            Codec.FLOAT.fieldOf("combustion_engine_rpm").forGetter(FuelType::combustionEngineRpm)
    ).apply(i, FuelType::new));

    public static FuelType getFuelType(RegistryAccess registryAccess, Fluid fluid) {
        return registryAccess.registryOrThrow(NorthstarRegistries.FUEL)
                .stream()
                .filter(fuel -> fluid.defaultFluidState().is(fuel.fluids()))
                .findFirst()
                .orElse(null);
    }

}
