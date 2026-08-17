package com.lightning.northstar.planet.data;

import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.util.NorthstarCodecs;
import com.lightning.northstar.util.PressureUnit;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraftforge.common.util.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @param composition           the atmosphere composition
 * @param pressure              the atmosphere pressure in Pascals (Pa)
 * @param daytimeStarBrightness the minimum brightness of stars during the day
 */
public record Atmosphere(
        List<AtmosphereFluid> composition,
        float pressure,
        float daytimeStarBrightness
) {

    public Atmosphere {
        if (Mth.equal(pressure, 0) || composition.isEmpty()) {
            composition = List.of();
        }
        if (Float.isNaN(daytimeStarBrightness)) {
            daytimeStarBrightness = composition.isEmpty() ? 1f : 0f;
        }
    }

    public static final Codec<Atmosphere> CODEC = RecordCodecBuilder.create(i -> i.group(
            NorthstarCodecs.listOrSingle(AtmosphereFluid.CODEC)
                    .optionalFieldOf("composition")
                    .xmap(
                            optional -> optional.orElse(Atmosphere.DEFAULT.get().composition()),
                            composition -> composition.equals(Atmosphere.DEFAULT.get().composition()) ? Optional.empty() : Optional.of(composition)
                    )
                    .forGetter(Atmosphere::composition),
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

    public boolean isVacuum() {
        return composition.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder asBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        private final List<AtmosphereFluid> composition = new ArrayList<>();
        private float pressure = PlanetDimension.EARTH_ATMOSPHERE_PRESSURE;
        private float daytimeStarBrightness = Float.NaN;

        private Builder() {
            composition.add(new AtmosphereFluid(NorthstarFluids.OXYGEN));
        }

        private Builder(Atmosphere atmosphere) {
            this.composition.addAll(atmosphere.composition);
            this.pressure = atmosphere.pressure;
            this.daytimeStarBrightness = atmosphere.daytimeStarBrightness;
        }

        public Builder composition(AtmosphereFluid... composition) {
            return composition(List.of(composition));
        }

        public Builder composition(List<AtmosphereFluid> composition) {
            this.composition.clear();
            this.composition.addAll(composition);
            return this;
        }

        public Builder addComposition(AtmosphereFluid... composition) {
            this.composition.addAll(List.of(composition));
            return this;
        }

        public Builder removeComposition(AtmosphereFluid... composition) {
            this.composition.removeAll(List.of(composition));
            return this;
        }

        public List<AtmosphereFluid> getComposition() {
            return composition;
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
            return new Atmosphere(composition, pressure, daytimeStarBrightness);
        }
    }

}
