package com.lightning.northstar.planet.data;

import com.lightning.northstar.content.NorthstarRegistries;
import com.lightning.northstar.planet.Planet;
import com.lightning.northstar.planet.data.orbit.OrbitProvider;
import com.lightning.northstar.planet.data.orbit.SimpleOrbitProvider;
import com.lightning.northstar.planet.data.render.PlanetSpriteRenderer;
import com.lightning.northstar.util.NorthstarCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @param centralBody            the body around which this body orbits
 * @param orbit                  the planet's orbit
 * @param type                   the type key, planet, star, gas_giant, etc...
 * @param scienceWeightExp       the weight exponent
 * @param requiredScience        the total needed science to unlock this planet_
 * @param rotationPeriodDays     the amount of days it takes for the planet to complete a full cycle on itself
 * @param diameter               the planet diameter, in kilometers.
 * @param obliquity              the axial tilt, in radians.
 * @param axialPrecession        the axial precession, in radians per year
 * @param initialAxialPrecession the initial axial precession at T=0
 * @param texture                the texture of the planet when rendering during takeoffs/landings and orbits
 * @param renderer               the renderer for the planet
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record PlanetProperties(
        @Nullable ResourceKey<PlanetProperties> centralBody,
        OrbitProvider orbit,
        String type,
        float scienceWeightExp,
        float requiredScience,
        double rotationPeriodDays,
        double diameter,
        double obliquity,
        double axialPrecession,
        double initialAxialPrecession,
        PlanetSpriteRenderer renderer,
        List<TextureLayer> texture,
        @Nullable Component notes
) {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public PlanetProperties(
            Optional<ResourceKey<PlanetProperties>> centralBody, OrbitProvider orbit,
            String type, float scienceWeightExp, float requiredScience,
            double rotationPeriodDays, double diameter,
            double axialTilt, double axialPrecession, double initialAxialPrecession,
            PlanetSpriteRenderer renderer, List<TextureLayer> texture, Optional<Component> notes
    ) {
        this(centralBody.orElse(null), orbit, type, scienceWeightExp, requiredScience, rotationPeriodDays, diameter,
                axialTilt, axialPrecession, initialAxialPrecession, renderer, texture, notes.orElse(null));
    }

    public double circumference() {
        return diameter * Math.PI;
    }

    public static final Codec<PlanetProperties> CODEC = RecordCodecBuilder.create(i -> i.group(
            NorthstarRegistries.PLANET_KEY_CODEC.optionalFieldOf("central_body").forGetter(planet -> Optional.ofNullable(planet.centralBody())),
            OrbitProvider.CODEC.fieldOf("orbit").forGetter(PlanetProperties::orbit),
            Codec.STRING.optionalFieldOf("class", "planet").forGetter(PlanetProperties::type),
            Codec.floatRange(0, 1).optionalFieldOf("science_weight_exp", 0.9f).forGetter(PlanetProperties::scienceWeightExp),
            Codec.FLOAT.fieldOf("required_science").forGetter(PlanetProperties::requiredScience),
            Codec.DOUBLE.optionalFieldOf("rotation_period_days", 1.0).forGetter(PlanetProperties::rotationPeriodDays),
            Codec.DOUBLE.fieldOf("diameter").forGetter(PlanetProperties::diameter),
            Codec.DOUBLE.optionalFieldOf("axial_tilt", 0.0).forGetter(PlanetProperties::obliquity),
            Codec.DOUBLE.optionalFieldOf("axial_precession", 0.0).forGetter(PlanetProperties::axialPrecession),
            Codec.DOUBLE.optionalFieldOf("initial_axial_precession", 0.0).forGetter(PlanetProperties::initialAxialPrecession),
            PlanetSpriteRenderer.CODEC_OR_INLINE.fieldOf("renderer").forGetter(PlanetProperties::renderer),
            NorthstarCodecs.listOrSingle(TextureLayer.CODEC).optionalFieldOf("texture", List.of()).forGetter(PlanetProperties::texture),
            ComponentSerialization.CODEC.optionalFieldOf("notes").forGetter(planet -> Optional.ofNullable(planet.notes()))
    ).apply(i, PlanetProperties::new));

    public static boolean isInterplanetary(RegistryAccess registry, ResourceKey<PlanetProperties> origin, ResourceKey<PlanetProperties> dest) {
        Registry<PlanetProperties> planets = registry.registryOrThrow(NorthstarRegistries.PLANET);
        PlanetProperties originPlanet = planets.getOrThrow(origin);
        PlanetProperties destPlanet = planets.getOrThrow(dest);
        return !origin.equals(dest) && !origin.equals(destPlanet.centralBody) && !dest.equals(originPlanet.centralBody);
    }

    public static boolean isInterplanetary(Planet origin, Planet dest) {
        return !origin.equals(dest) && !origin.equals(dest.centralBody) && !dest.equals(origin.centralBody);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ResourceKey<PlanetProperties> centralBody;
        private OrbitProvider orbit;
        private String type;
        private float scienceWeightExponent = 0.9f;
        private Float requiredScience;
        private Double rotationPeriodDays;
        private Double diameter;
        private double axialTilt;
        private double axialPrecession;
        private double initialAxialPrecession;
        private PlanetSpriteRenderer renderer;
        private List<TextureLayer> texture = new ArrayList<>();
        private Component notes;

        public Builder centralBody(ResourceKey<PlanetProperties> centralBody) {
            this.centralBody = centralBody;
            return this;
        }

        public Builder orbit(OrbitProvider orbit) {
            this.orbit = orbit;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder scienceWeightExponent(float scienceWeightExponent) {
            this.scienceWeightExponent = scienceWeightExponent;
            return this;
        }

        public Builder requiredScience(float requiredScience) {
            this.requiredScience = requiredScience;
            return this;
        }

        public Builder rotationPeriodHours(double rotationPeriodHours) {
            return rotationPeriodDays(rotationPeriodHours / 24.0);
        }

        public Builder rotationPeriodDays(double rotationPeriodDays) {
            this.rotationPeriodDays = rotationPeriodDays;
            return this;
        }

        public Builder rotationPeriodTidalLock() {
            // FIXME: This is pretty cursed and will fail for other orbit providers
            return rotationPeriodDays(-((SimpleOrbitProvider) orbit).durationDays());
        }

        /** Defines the diameter, in KM */
        public Builder diameter(double diameterKm) {
            this.diameter = diameterKm;
            return this;
        }

        public Builder axialTilt(double axialTilt) {
            this.axialTilt = axialTilt;
            return this;
        }

        public Builder axialTiltDeg(double axialTiltDeg) {
            return axialTilt(Math.toRadians(axialTiltDeg));
        }

        public Builder axialPrecession(double axialPrecession) {
            this.axialPrecession = axialPrecession;
            return this;
        }

        public Builder axialPrecessionDeg(double axialPrecessionDeg) {
            return axialPrecession(Math.toRadians(axialPrecessionDeg));
        }

        public Builder initialAxialPrecession(double initialAxialPrecession) {
            this.initialAxialPrecession = initialAxialPrecession;
            return this;
        }

        public Builder initialAxialPrecessionDeg(double initialAxialPrecession) {
            return initialAxialPrecession(Math.toRadians(initialAxialPrecession));
        }

        public Builder renderer(ResourceLocation texture) {
            return renderer(PlanetSpriteRenderer.simple(texture));
        }

        public Builder renderer(PlanetSpriteRenderer renderer) {
            this.renderer = renderer;
            return this;
        }

        public Builder texture(ResourceLocation texture) {
            return texture(new TextureLayer(texture));
        }

        public Builder texture(TextureLayer... texture) {
            this.texture = List.of(texture);
            return this;
        }

        public Builder notes(Component notes) {
            this.notes = notes;
            return this;
        }

        public PlanetProperties build() {
            return new PlanetProperties(
                    centralBody,
                    Objects.requireNonNull(orbit, "orbit"),
                    Objects.requireNonNull(type, "type"),
                    scienceWeightExponent,
                    requiredScience == null ? -1 : requiredScience,
                    Objects.requireNonNull(rotationPeriodDays, "rotation period"),
                    Objects.requireNonNull(diameter, "diameter"),
                    axialTilt,
                    axialPrecession,
                    initialAxialPrecession,
                    Objects.requireNonNull(renderer, "renderer"),
                    texture,
                    notes
            );
        }
    }

    public record TextureLayer(
            ResourceLocation texture,
            float speed,
            boolean snap
    ) {
        public TextureLayer(ResourceLocation texture) {
            this(texture, 0, false);
        }

        public static final Codec<TextureLayer> CODEC = Codec.either(
                ResourceLocation.CODEC.xmap(TextureLayer::new, TextureLayer::texture),
                RecordCodecBuilder.<TextureLayer>create(i -> i.group(
                        ResourceLocation.CODEC.fieldOf("texture").forGetter(TextureLayer::texture),
                        Codec.FLOAT.optionalFieldOf("speed", 0f).forGetter(TextureLayer::speed),
                        Codec.BOOL.optionalFieldOf("snap", false).forGetter(TextureLayer::snap)
                ).apply(i, TextureLayer::new))
        ).xmap(
                either -> either.left().orElseGet(() -> either.right().orElseThrow()),
                layer -> layer.speed() == 0 && !layer.snap() ? Either.left(layer) : Either.right(layer)
        );
    }

}
