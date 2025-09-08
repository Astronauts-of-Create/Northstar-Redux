package com.lightning.northstar.data;

import com.lightning.northstar.content.NorthstarRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public record FuelType(
        Set<ResourceLocation> fluids,
        float rocketMultiplier,
        int combustionEngineEfficiency,
        float combustionEngineRpm) {

    // TODO: find if there is a better way than a Set<ResourceLocation> which works for optional fluids
    public static final Codec<FuelType> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.listOf().fieldOf("fluids")
                    .xmap(l -> (Set<ResourceLocation>) new HashSet<>(l), ArrayList::new)
                    .forGetter(FuelType::fluids),
            Codec.FLOAT.fieldOf("rocket_multiplier").forGetter(FuelType::rocketMultiplier),
            Codec.INT.fieldOf("combustion_engine_use").forGetter(FuelType::combustionEngineEfficiency),
            Codec.FLOAT.fieldOf("combustion_engine_rpm").forGetter(FuelType::combustionEngineRpm)
    ).apply(i, FuelType::new));

    public static FuelType getFuelType(RegistryAccess registryAccess, Fluid fluid) {
        ResourceLocation name = registryAccess.registryOrThrow(Registries.FLUID).getKey(fluid);
        return registryAccess.registryOrThrow(NorthstarRegistries.FUEL)
                .stream()
                .filter(fuel -> fuel.fluids().contains(name))
                .findFirst()
                .orElse(null);
    }

}
