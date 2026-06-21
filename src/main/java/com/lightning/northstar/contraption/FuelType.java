package com.lightning.northstar.contraption;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarRegistries;
import com.lightning.northstar.data.Mod;
import com.lightning.northstar.data.TagHelper;
import com.lightning.northstar.util.NorthstarCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.tterrag.registrate.util.entry.FluidEntry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record FuelType(
        List<String> fluids,
        float gjPerMb,
        float combustionEngineUse,
        float combustionEngineRpm) {

    public static final Codec<FuelType> CODEC = RecordCodecBuilder.create(i -> i.group(
            NorthstarCodecs.listOrSingle(Codec.STRING).optionalFieldOf("fluids", List.of()).forGetter(FuelType::fluids),
            Codec.FLOAT.optionalFieldOf("gj_per_mb", 0f).forGetter(FuelType::gjPerMb),
            Codec.FLOAT.optionalFieldOf("combustion_engine_use", 0f).forGetter(FuelType::combustionEngineUse),
            Codec.FLOAT.optionalFieldOf("combustion_engine_rpm", 0f).forGetter(FuelType::combustionEngineRpm)
    ).apply(i, FuelType::new));

    private static final Map<Fluid, FuelType> FUEL_CACHE = new ConcurrentHashMap<>();

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> fluids = new ArrayList<>();
        private float gjPerMb;
        private float combustionEngineUse;
        private float combustionEngineRpm;

        public Builder tag(TagHelper.Tag<Fluid> tag) {
            return tag(tag.tag());
        }

        public Builder tag(TagKey<Fluid> tag) {
            return tag(tag.location());
        }

        public Builder tag(Mod mod, String tag) {
            return tag(mod.loc(tag));
        }

        public Builder tag(ResourceLocation tag) {
            fluids.add("#" + tag.toString());
            return this;
        }

        public Builder fluid(FluidEntry<?> fluid) {
            return rawFluid(fluid.getKey().location());
        }

        public Builder fluid(Fluid fluid) {
            return rawFluid(RegisteredObjectsHelper.getKeyOrThrow(fluid));
        }

        public Builder fluid(Mod mod, String tag) {
            return rawFluid(mod.loc(tag));
        }

        public Builder rawFluid(ResourceLocation fluid) {
            fluids.add(fluid.toString());
            return this;
        }

        public Builder gjPerMb(float gjPerMb) {
            this.gjPerMb = gjPerMb;
            return this;
        }

        public Builder combustionEngine(float usePerTick, float rpm) {
            this.combustionEngineUse = usePerTick;
            this.combustionEngineRpm = rpm;
            return this;
        }

        public FuelType build() {
            return new FuelType(fluids, gjPerMb, combustionEngineUse, combustionEngineRpm);
        }
    }

    @ApiStatus.Internal
    public static void recacheFuels(RegistryAccess registryAccess) {
        FUEL_CACHE.clear();

        Registry<Fluid> fluids = registryAccess.registryOrThrow(Registries.FLUID);
        Registry<FuelType> fuels = registryAccess.registryOrThrow(NorthstarRegistries.FUEL);

        Object2IntMap<Fluid> scores = new Object2IntOpenHashMap<>();
        TriConsumer<FuelType, Fluid, Integer> cache = (fuel, fluid, score) -> {
            fluid = FluidHelper.convertToStill(fluid);
            if (scores.getInt(fluid) >= score)
                return;
            scores.put(fluid, score);
            FuelType previous = FUEL_CACHE.put(fluid, fuel);
            if (previous == null)
                return;
            ResourceLocation oldKey = fuels.getKey(previous);
            ResourceLocation newKey = fuels.getKey(fuel);
            ResourceLocation fluidKey = fluids.getKey(fluid);
            Northstar.LOGGER.warn("Fuels '{}' and '{}' both use fluid '{}', '{}' will be used.", oldKey, newKey, fluidKey, newKey);
        };

        for (Map.Entry<ResourceKey<FuelType>, FuelType> entry : fuels.entrySet()) {
            ResourceLocation key = entry.getKey().location();
            FuelType fuel = entry.getValue();

            if (fuel.fluids.isEmpty()) {
                fluids.getOptional(key).ifPresentOrElse(
                        fluid -> cache.accept(fuel, fluid, 3),
                        () -> Northstar.LOGGER.warn("Fuel '{}' has no fluids declared and no fluid named '{}' could be found.", key, key));
                continue;
            }

            for (String rawFluid : fuel.fluids) {
                if (rawFluid.startsWith("#")) {
                    ResourceLocation loc = ResourceLocation.parse(rawFluid.substring(1));
                    TagKey<Fluid> tag = fluids.getTagNames()
                            .filter(t -> t.location().equals(loc))
                            .findFirst()
                            .orElse(null);
                    if (tag == null) {
                        Northstar.LOGGER.warn("Fuel '{}' tried to reference tag '{}' which doesn't exist.", key, loc);
                        continue;
                    }

                    fluids.getTag(tag).ifPresent(holders -> holders.forEach(holder -> cache.accept(fuel, holder.value(), 1)));
                    continue;
                }

                ResourceLocation fluidLoc = ResourceLocation.parse(rawFluid);
                fluids.getOptional(fluidLoc).ifPresentOrElse(
                        fluid -> cache.accept(fuel, fluid, 2),
                        () -> Northstar.LOGGER.warn("Fuel '{}' tried to reference fluid '{}' which could not be found.", key, fluidLoc));
            }
        }
    }

    public static FuelType getFuelType(Fluid fluid) {
        return FUEL_CACHE.get(fluid);
    }

    public boolean supports(Fluid fluid) {
        return equals(FUEL_CACHE.get(fluid));
    }

}
