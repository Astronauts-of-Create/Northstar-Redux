package com.lightning.northstar.planet.data;

import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.util.PressureUnit;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

/**
 * @param fluid                 a fluid that represents the atmosphere, used to know if the planet is breathable and what the atmospheric concentrator collect
 * @param fluidNbt              the nbt compound associated with the fluid
 * @param collectionRate        the collection rate of atmospheric concentrators in mB/t at 256 RPM
 * @param pressure              the atmosphere pressure in Pascals (Pa)
 * @param daytimeStarBrightness the minimum brightness of stars during the day
 */
public record Atmosphere(
        Fluid fluid,
        DataComponentPatch fluidNbt,
        float collectionRate,
        float pressure,
        float daytimeStarBrightness
) {

    public Atmosphere {
        fluid = Mth.equal(pressure, 0) ? Fluids.EMPTY : FluidHelper.convertToStill(fluid);
        if (Float.isNaN(daytimeStarBrightness)) {
            daytimeStarBrightness = fluid == Fluids.EMPTY ? 1f : 0f;
        }
    }

    public static final Codec<Atmosphere> CODEC = RecordCodecBuilder.create(i -> i.group(
            BuiltInRegistries.FLUID.byNameCodec()
                    .optionalFieldOf("fluid")
                    .xmap(
                            optional -> optional.orElse(NorthstarFluids.OXYGEN.get()),
                            fluid -> NorthstarFluids.OXYGEN.is(fluid) ? Optional.empty() : Optional.of(fluid)
                    )
                    .forGetter(Atmosphere::fluid),
            DataComponentPatch.CODEC.fieldOf("fluid_nbt").forGetter(Atmosphere::fluidNbt),
            Codec.FLOAT.optionalFieldOf("collection_rate", 10f).forGetter(Atmosphere::collectionRate),
            Codec.mapEither(
                            Codec.FLOAT.fieldOf("pressure_atm"),
                            Codec.FLOAT.fieldOf("pressure_pa")
                    ).xmap(
                            e -> e.map(atm -> atm * PlanetDimension.EARTH_ATMOSPHERE_PRESSURE, pa -> pa),
                            Either::right
                    )
                    .orElse(PlanetDimension.EARTH_ATMOSPHERE_PRESSURE)
                    .forGetter(Atmosphere::pressure),
            Codec.floatRange(0f, 1f).optionalFieldOf("daytime_star_brightness", Float.NaN).forGetter(Atmosphere::daytimeStarBrightness)
    ).apply(i, Atmosphere::new));

    public static final Lazy<Atmosphere> DEFAULT = Lazy.of(() -> builder().build());

    public static Builder builder() {
        return new Builder();
    }

    public boolean isVacuum() {
        return fluid == Fluids.EMPTY;
    }

    public FluidStack asFluidStack(int amount) {
        return new FluidStack(fluid.builtInRegistryHolder(), amount, fluidNbt);
    }

    public static class Builder {
        private Fluid fluid = NorthstarFluids.OXYGEN.get();
        private DataComponentPatch fluidNbt = DataComponentPatch.EMPTY;
        private float collectionRate = 10;
        private float pressure = PlanetDimension.EARTH_ATMOSPHERE_PRESSURE;
        private float daytimeStarBrightness = Float.NaN;

        public Builder fluid(Fluid fluid) {
            this.fluid = fluid;
            return this;
        }

        public Builder fluid(RegistryEntry<? extends Fluid, ? extends Fluid> fluid) {
            return fluid(fluid.get());
        }

        public Builder fluidNbt(DataComponentPatch fluidNbt) {
            this.fluidNbt = fluidNbt;
            return this;
        }

        public Builder fluid(Fluid fluid, DataComponentPatch fluidNbt) {
            this.fluid = fluid;
            this.fluidNbt = fluidNbt;
            return this;
        }

        public Builder fluid(RegistryEntry<? extends Fluid, ? extends Fluid> fluid, DataComponentPatch fluidNbt) {
            this.fluid = fluid.get();
            this.fluidNbt = fluidNbt;
            return this;
        }

        public Builder collectionRate(float collectionRate) {
            this.collectionRate = collectionRate;
            return this;
        }

        /** Defines the pressure in Pascals */
        public Builder pressurePa(float pressurePa) {
            this.pressure = pressurePa;
            return this;
        }

        /** Defines the pressure in standard atmospheres */
        public Builder pressureAtm(float pressureAtm) {
            return pressurePa(PressureUnit.ATMOSPHERE.toPascal(pressureAtm));
        }

        public Builder daytimeStarBrightness(float daytimeStarBrightness) {
            this.daytimeStarBrightness = daytimeStarBrightness;
            return this;
        }

        public Atmosphere build() {
            return new Atmosphere(fluid, fluidNbt, collectionRate, pressure, daytimeStarBrightness);
        }
    }

}
