package com.lightning.northstar.contraption;

import com.lightning.northstar.content.NorthstarRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record FuelType(
        HolderSet<Fluid> fluids,
        float gjPerMb,
        int combustionEngineEfficiency,
        float combustionEngineRpm) {

    // TODO: change Set<ResourceLocation> to HolderSet<Fluid>, use tags for optional fluids and move fuel types to data gen
    public static final Codec<FuelType> CODEC = RecordCodecBuilder.create(i -> i.group(
            RegistryCodecs.homogeneousList(Registries.FLUID).fieldOf("fluids").forGetter(FuelType::fluids),
            Codec.FLOAT.fieldOf("gj_per_mb").forGetter(FuelType::gjPerMb),
            Codec.INT.fieldOf("combustion_engine_use").forGetter(FuelType::combustionEngineEfficiency),
            Codec.FLOAT.fieldOf("combustion_engine_rpm").forGetter(FuelType::combustionEngineRpm)
    ).apply(i, FuelType::new));

    private static final Map<Fluid, FuelType> FUEL_CACHE = new ConcurrentHashMap<>();

    @ApiStatus.Internal
    public static void recacheFuels(RegistryAccess registryAccess) {
        FUEL_CACHE.clear();

        Registry<FuelType> fuels = registryAccess.registryOrThrow(NorthstarRegistries.FUEL);
        for (Fluid fluid : registryAccess.registryOrThrow(Registries.FLUID)) {
            fuels.stream()
                    .filter(fuel -> fuel.supports(fluid))
                    .findFirst()
                    .ifPresent(fuel -> FUEL_CACHE.put(fluid, fuel));
        }
    }

    public static FuelType getFuelType(Fluid fluid) {
        return FUEL_CACHE.get(fluid);
    }

    public boolean supports(Fluid fluid) {
        return fluid.defaultFluidState().is(fluids);
    }

}
