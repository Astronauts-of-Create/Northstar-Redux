package com.lightning.northstar.item.atlas;

import com.lightning.northstar.contraption.rocket.RocketDestination;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record SpaceAtlasContent(
        @Unmodifiable Map<ResourceLocation, Planet> planets,
        @Unmodifiable Map<RocketDestination, Component> destinations
) {

    public static final SpaceAtlasContent EMPTY = new SpaceAtlasContent(Map.of(), Map.of());
    public static final Codec<SpaceAtlasContent> CODEC;

    static {
        Codec<Map<RocketDestination, Component>> theInnerThingOrElseItExplodes = RecordCodecBuilder.<Map.Entry<RocketDestination, Component>>create(d -> d.group(
                        RocketDestination.CODEC.fieldOf("destination").forGetter(Map.Entry::getKey),
                        ComponentSerialization.CODEC.fieldOf("label").forGetter(Map.Entry::getValue)
                ).apply(d, Map::entry))
                .listOf()
                .xmap(entries -> Map.ofEntries(entries.toArray(Map.Entry[]::new)), map -> List.copyOf(map.entrySet()));

        CODEC = RecordCodecBuilder.create(i -> i.group(
                Planet.CODEC.listOf()
                        .xmap(l -> l.stream().collect(Collectors.toMap(Planet::planetId, Function.identity())), m -> List.copyOf(m.values()))
                        .fieldOf("planets")
                        .forGetter(SpaceAtlasContent::planets),
                theInnerThingOrElseItExplodes.fieldOf("destinations").forGetter(SpaceAtlasContent::destinations)
        ).apply(i, SpaceAtlasContent::new));
    }

    public Builder asBuilder() {
        return new Builder(this);
    }

    public static Builder builder() {
        return EMPTY.asBuilder();
    }

    public static class Builder {
        private Map<ResourceLocation, Planet> planets = new HashMap<>();
        private Map<RocketDestination, Component> destinations = new HashMap<>();

        private Builder(SpaceAtlasContent content) {
            planets.putAll(content.planets);
            destinations.putAll(content.destinations);
        }

        public Builder addPlanet(Planet planet) {
            planets.put(planet.planetId, planet);
            return this;
        }

        public Builder addDestination(RocketDestination destination, Component label) {
            destinations.put(destination, label);
            return this;
        }

        public Map<ResourceLocation, Planet> getPlanets() {
            return planets;
        }

        public Map<RocketDestination, Component> getDestinations() {
            return destinations;
        }

        public SpaceAtlasContent build() {
            return new SpaceAtlasContent(Map.copyOf(planets), Map.copyOf(destinations));
        }
    }

    public record Planet(
            ResourceLocation planetId,
            @Unmodifiable List<AtlasReading> readings,
            float science
    ) {
        public static final Codec<Planet> CODEC = RecordCodecBuilder.create(i -> i.group(
                ResourceLocation.CODEC.fieldOf("planet").forGetter(Planet::planetId),
                AtlasReading.CODEC.listOf().fieldOf("readings").forGetter(Planet::readings),
                Codec.FLOAT.fieldOf("science").forGetter(Planet::science)
        ).apply(i, Planet::new));

        public Planet.Builder toBuilder() {
            return new Planet.Builder(this);
        }

        public static Planet.Builder builder() {
            return new Planet.Builder();
        }

        public static class Builder {
            private ResourceLocation planetId;
            private List<AtlasReading> readings = new ArrayList<>();
            private float science;

            private Builder() {
            }

            private Builder(Planet planet) {
                this.planetId = planet.planetId;
                this.readings.addAll(planet.readings);
                this.science = planet.science;
            }

            public Builder planetId(ResourceLocation planetId) {
                this.planetId = planetId;
                return this;
            }

            public Builder addReading(AtlasReading reading) {
                readings.add(reading);
                return this;
            }

            public Builder science(float science) {
                this.science = science;
                return this;
            }

            public Builder calculateScience(float weightExp) {
                ensureSorted();
                float science = 0;
                Object2IntMap<ResourceLocation> counts = new Object2IntOpenHashMap<>();
                for (SpaceAtlasContent.AtlasReading reading : readings) {
                    science += reading.science() * (float) Math.pow(weightExp, counts.mergeInt(reading.origin(), 0, (a, b) -> a + b + 1));
                }
                return science(science);
            }

            public ResourceLocation getPlanetId() {
                return planetId;
            }

            public List<AtlasReading> getReadings() {
                return readings;
            }

            public float getScience() {
                return science;
            }

            public Planet build() {
                ensureSorted();
                return new Planet(Objects.requireNonNull(planetId, "planetId"), List.copyOf(readings), science);
            }

            private void ensureSorted() {
                readings.sort(Comparator.comparing(reading -> -reading.science));
            }
        }
    }

    public record AtlasReading(
            ResourceLocation origin,
            float science,
            int day
    ) {
        public static final Codec<AtlasReading> CODEC = RecordCodecBuilder.create(i -> i.group(
                ResourceLocation.CODEC.fieldOf("origin").forGetter(AtlasReading::origin),
                Codec.FLOAT.fieldOf("science").forGetter(AtlasReading::science),
                Codec.INT.fieldOf("day").forGetter(AtlasReading::day)
        ).apply(i, AtlasReading::new));
    }

    @Contract("_, _ -> new")
    public static MutableComponent getDefaultLabel(BlockPos pos, Direction dir) {
        return Component.literal(
                RegistrateLangProvider.toEnglishName(dir.getSerializedName()) + " of " +
                LangNumberFormat.format(pos.getX()) + ", " +
                LangNumberFormat.format(pos.getY()) + ", " +
                LangNumberFormat.format(pos.getZ())
        );
    }

}
