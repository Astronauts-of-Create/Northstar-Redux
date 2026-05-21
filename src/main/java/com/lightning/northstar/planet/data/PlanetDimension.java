package com.lightning.northstar.planet.data;

import com.lightning.northstar.content.NorthstarRegistries;
import com.lightning.northstar.planet.data.func.LevelFunction;
import com.lightning.northstar.planet.data.func.LightFunction;
import com.lightning.northstar.util.NorthstarCodecs;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @param planet             the planet that this dimension is attached to
 * @param name               the name of that dimension
 * @param dimensionId        the id of the dimension
 * @param isOrbit            if the dimension is an orbit and the rocket should stop in the air if no blocks are present
 * @param atmosphere         the atmosphere of this dimension
 * @param averageTemperature the average temperature to be displayed on the telescope
 * @param gravity            the gravity in m/s^2
 * @param temperature        the surface temperature in Celsius
 * @param wind               the surface wind multiplier
 * @param heatIntercept      the first parameter of the heat function
 * @param heatGradient       the second parameter of the heat function
 * @param longitudeOffset    the offset in the camera longitude, in radians
 * @param latitudeOffset     the offset in the camera latitude, in radians
 * @see #heatShieldingRequirement(int)
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record PlanetDimension(
        @Nullable ResourceKey<PlanetProperties> planet,
        String name,
        ResourceKey<Level> dimensionId,
        @Nullable ResourceKey<Level> dimensionAbove,
        @Nullable ResourceKey<Level> dimensionBelow,
        boolean isOrbit,
        Atmosphere atmosphere,
        float gravity,
        Vector2f averageTemperature,
        LevelFunction temperature,
        LevelFunction wind,
        LevelFunction sun,
        float heatIntercept,
        float heatGradient,
        double longitudeOffset,
        double latitudeOffset
) {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private PlanetDimension(Optional<ResourceKey<PlanetProperties>> planet, String name, ResourceKey<Level> dimensionId,
                            Optional<ResourceKey<Level>> dimensionAbove, Optional<ResourceKey<Level>> dimensionBelow,
                            boolean isOrbit, Atmosphere atmosphere, float gravity, Vector2f averageTemperature,
                            LevelFunction temperature, LevelFunction wind, LevelFunction sun,
                            float heatIntercept, float heatGradient, double longitudeOffset, double latitudeOffset) {
        this(planet.orElse(null), name, dimensionId, dimensionAbove.orElse(null), dimensionBelow.orElse(null), isOrbit, atmosphere,
                gravity, averageTemperature, temperature, wind, sun, heatIntercept, heatGradient, longitudeOffset, latitudeOffset);
    }

    /** Overworld gravity, used as a reference when calculating gravity on other dimensions */
    public static final float EARTH_GRAVITY = 9.807f;
    public static final float EARTH_ATMOSPHERE_PRESSURE = 101325;

    public static final LevelFunction DEFAULT_SUN = LevelFunction.light(LightFunction.Mode.SKY_DARKEN, true);

    public static final Codec<PlanetDimension> CODEC = RecordCodecBuilder.create(i -> i.group(
            NorthstarRegistries.PLANET_KEY_CODEC.optionalFieldOf("planet").forGetter(dim -> Optional.ofNullable(dim.planet())),
            Codec.STRING.fieldOf("name").forGetter(PlanetDimension::name),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(PlanetDimension::dimensionId),
            ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("dimension_above").forGetter(dim -> Optional.ofNullable(dim.dimensionAbove())),
            ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("dimension_below").forGetter(dim -> Optional.ofNullable(dim.dimensionBelow())),
            Codec.BOOL.optionalFieldOf("is_orbit", false).forGetter(PlanetDimension::isOrbit),
            Atmosphere.CODEC.optionalFieldOf("atmosphere")
                    .xmap(
                            optional -> optional.orElse(Atmosphere.DEFAULT.get()),
                            value -> Atmosphere.DEFAULT.get().equals(value) ? Optional.empty() : Optional.of(value)
                    )
                    .forGetter(PlanetDimension::atmosphere),
            Codec.floatRange(0, Float.POSITIVE_INFINITY).optionalFieldOf("gravity", EARTH_GRAVITY).forGetter(PlanetDimension::gravity),
            Codec.either(Codec.FLOAT, NorthstarCodecs.VECTOR2F)
                    .xmap(
                            either -> either.map(Vector2f::new, v -> v),
                            vector -> Mth.equal(vector.x, vector.y) ? Either.left(vector.x) : Either.right(vector)
                    )
                    .fieldOf("averageTemperature")
                    .forGetter(PlanetDimension::averageTemperature),
            LevelFunction.CODEC.optionalFieldOf("temperature", LevelFunction.constant(NorthstarTemperature.DEFAULT)).forGetter(PlanetDimension::temperature),
            LevelFunction.CODEC.optionalFieldOf("wind_multiplier", LevelFunction.constant(1)).forGetter(PlanetDimension::wind),
            LevelFunction.CODEC.optionalFieldOf("sun_multiplier", DEFAULT_SUN).forGetter(PlanetDimension::sun),
            Codec.FLOAT.optionalFieldOf("heat_intercept", 0f).forGetter(PlanetDimension::heatIntercept),
            Codec.FLOAT.optionalFieldOf("heat_gradient", 1f).forGetter(PlanetDimension::heatGradient),
            NorthstarCodecs.DOUBLE_DEG_RAD.optionalFieldOf("longitude_offset", 0.0).forGetter(PlanetDimension::longitudeOffset),
            NorthstarCodecs.DOUBLE_DEG_RAD.optionalFieldOf("latitude_offset", 0.0).forGetter(PlanetDimension::latitudeOffset)
    ).apply(i, PlanetDimension::new));

    public float gravityScale() {
        return gravity / EARTH_GRAVITY;
    }

    public boolean hasAtmosphere() {
        return !atmosphere.isVacuum();
    }

    @Contract("-> new")
    public MutableComponent formattedName() {
        return Component.translatableWithFallback("northstar.planet.dimension." + name, RegistrateLangProvider.toEnglishName(name));
    }

    /** Calculates the heat shielding requirement based on the block count using the linear function {@link #heatIntercept} + {@link #heatGradient} * blockCount */
    public float heatShieldingRequirement(int blockCount) {
        return heatIntercept + heatGradient * blockCount;
    }

    public static PlanetDimension orbit(@Nullable ResourceKey<PlanetProperties> planet, ResourceKey<Level> dimension, @Nullable ResourceKey<Level> dimensionBelow) {
        return orbitBuilder(planet, dimension, dimensionBelow).build();
    }

    public static Builder orbitBuilder(@Nullable ResourceKey<PlanetProperties> planet, ResourceKey<Level> dimension, @Nullable ResourceKey<Level> dimensionBelow) {
        return builder()
                .planet(planet)
                .name("orbit")
                .dimension(dimension)
                .dimensionBelow(dimensionBelow)
                .orbit(true)
                .noAtmosphere()
                .gravity(0)
                .temperature(NorthstarTemperature.SPACE);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ResourceKey<PlanetProperties> planet;
        private String name;
        private ResourceKey<Level> dimension;
        private ResourceKey<Level> dimensionAbove;
        private ResourceKey<Level> dimensionBelow;
        private boolean isOrbit;
        private Atmosphere atmosphere = Atmosphere.DEFAULT.get();
        private float gravity = EARTH_GRAVITY;
        private Vector2f averageTemperature = new Vector2f(20);
        private LevelFunction temperature = LevelFunction.constant(NorthstarTemperature.DEFAULT);
        private LevelFunction wind = LevelFunction.constant(1);
        private LevelFunction sun = DEFAULT_SUN;
        private float heatIntercept = 0;
        private float heatGradient = 1;
        private double longitudeOffset;
        private double latitudeOffset;

        public Builder planet(@Nullable ResourceKey<PlanetProperties> planet) {
            this.planet = planet;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder dimension(ResourceKey<Level> dimension) {
            this.dimension = dimension;
            return this;
        }

        public Builder dimensionAbove(@Nullable ResourceKey<Level> dimension) {
            this.dimensionAbove = dimension;
            return this;
        }

        public Builder dimensionBelow(@Nullable ResourceKey<Level> dimension) {
            this.dimensionBelow = dimension;
            return this;
        }

        public Builder orbit(boolean isOrbit) {
            this.isOrbit = isOrbit;
            return this;
        }

        public Builder atmosphere(Atmosphere atmosphere) {
            this.atmosphere = atmosphere;
            return this;
        }

        public Builder atmosphere(Consumer<Atmosphere.Builder> atmosphere) {
            Atmosphere.Builder builder = Atmosphere.builder();
            atmosphere.accept(builder);
            return atmosphere(builder.build());
        }

        public Builder gravity(float gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder temperature(float temperature) {
            return temperature(LevelFunction.constant(temperature), temperature);
        }

        public Builder temperature(LevelFunction temperature, float average) {
            return temperature(temperature, new Vector2f(average));
        }

        public Builder temperature(LevelFunction temperature, Vector2f average) {
            this.temperature = temperature;
            this.averageTemperature = average;
            return this;
        }

        public Builder wind(float wind) {
            return wind(LevelFunction.constant(wind));
        }

        public Builder wind(LevelFunction wind) {
            this.wind = wind;
            return this;
        }

        public Builder sun(float multiplier) {
            return sun(DEFAULT_SUN.mul(LevelFunction.constant(multiplier)));
        }

        public Builder sun(LevelFunction sun) {
            this.sun = sun;
            return this;
        }

        public Builder heatRequirement(float intercept, float gradient) {
            this.heatIntercept = intercept;
            this.heatGradient = gradient;
            return this;
        }

        public Builder longitudeOffset(double longitudeOffset) {
            this.longitudeOffset = longitudeOffset;
            return this;
        }

        public Builder longitudeOffsetDeg(double longitudeOffsetDeg) {
            return longitudeOffset(Math.toRadians(longitudeOffsetDeg));
        }

        public Builder latitudeOffset(double latitudeOffset) {
            this.latitudeOffset = latitudeOffset;
            return this;
        }

        public Builder latitudeOffsetDeg(double latitudeOffsetDeg) {
            return latitudeOffset(Math.toRadians(latitudeOffsetDeg));
        }

        /** Sets the atmosphere to empty, disables wind and heat requirement */
        public Builder noAtmosphere() {
            return atmosphere(atm -> atm
                    .fluid(Fluids.EMPTY)
                    .collectionRate(0)
                    .pressurePa(0))
                    .wind(0)
                    .heatRequirement(0, 0);
        }

        public PlanetDimension build() {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(dimension, "dimension");
            return new PlanetDimension(planet, name, dimension, dimensionAbove, dimensionBelow, isOrbit, atmosphere, gravity, averageTemperature, temperature, wind, sun, heatIntercept, heatGradient, longitudeOffset, latitudeOffset);
        }
    }

}
