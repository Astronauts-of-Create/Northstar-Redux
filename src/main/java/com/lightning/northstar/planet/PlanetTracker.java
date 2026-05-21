package com.lightning.northstar.planet;

import com.lightning.northstar.content.NorthstarRegistries;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.lightning.northstar.planet.data.PlanetProperties;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlanetTracker {

    /** Minecraft level to Northstar planet info */
    private final Map<ResourceLocation, Planet> levelToPlanet = new HashMap<>();
    /** Minecraft level to Northstar dimension info */
    private final Map<ResourceLocation, PlanetDimension> levelToDimension = new HashMap<>();
    /** Northstar planet to gravitational system */
    private final Map<ResourceLocation, GravitationalSystem> systems = new HashMap<>();
    /** Northstar planet to Northstar planet info */
    private final Map<ResourceLocation, Planet> planetsById = new HashMap<>();
    /** Root planets of all gravitational systems */
    private final List<Planet> roots = new ArrayList<>();
    /** Flattened planet trees, used internally when updating planets */
    private final List<Planet> updateOrder = new ArrayList<>();

    private double deltaDays;

    public void tick(Level level, float partialTick) {
        boolean advance = level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT);
        long baseDays = level.getDayTime() / 24000L;
        double days = baseDays + (level.getDayTime() % 24000L + 6000L + (advance ? partialTick : 0)) / 24000.0;

        updateOrbits(days);
    }

    public void updateOrbits(double days) {
        this.deltaDays = days;

        for (Planet planet : updateOrder) {
            Vector3d localPosition = planet.properties.orbit().calculatePosition(days, planet.localPosition);
            Vector3d position = planet.position.set(localPosition);

            if (planet.centralBody != null) {
                position.add(planet.centralBody.position);
            }
        }
    }

    public void reloadPlanets(RegistryAccess registryAccess) {
        Registry<PlanetDimension> dimensionRegistry = registryAccess.registryOrThrow(NorthstarRegistries.PLANET_DIMENSION);
        Registry<PlanetProperties> planetRegistry = registryAccess.registryOrThrow(NorthstarRegistries.PLANET);

        Map<ResourceLocation, List<PlanetDimension>> planetToDimensions = new HashMap<>();
        for (PlanetDimension dimension : dimensionRegistry) {
            if (dimension.planet() != null) {
                planetToDimensions.computeIfAbsent(dimension.planet().location(), l -> new ArrayList<>()).add(dimension);
            }
        }

        Map<ResourceLocation, Planet> builtPlanets = new HashMap<>();
        Stack<ResourceLocation> building = new Stack<>();
        AtomicReference<Function<ResourceLocation, Planet>> buildPlanet = new AtomicReference<>();
        buildPlanet.set(key -> {
            if (building.contains(key)) {
                throw new IllegalStateException("Found cyclic planet dependency through chain " + building.stream().map(Object::toString).collect(Collectors.joining(" -> ")));
            }
            building.push(key);

            PlanetProperties properties = planetRegistry.get(key);
            if (properties == null) {
                building.pop();
                return null;
            }

            Planet parent = null;
            if (properties.centralBody() != null) {
                parent = computeIfAbsentSafe(builtPlanets, properties.centralBody().location(), buildPlanet.get());
                if (parent == null) {
                    throw new IllegalStateException("Planet '%s' is trying to reference central body '%s' which doesn't exist.".formatted(key, properties.centralBody().location()));
                }
            }

            List<PlanetDimension> dimensions = Objects.requireNonNullElse(planetToDimensions.remove(key), List.of());

            Planet planet = new Planet(ResourceKey.create(NorthstarRegistries.PLANET, key), properties, parent, dimensions);
            if (parent != null) {
                parent.satellites.add(planet);
            }

            building.pop();
            return planet;
        });
        for (ResourceLocation key : planetRegistry.keySet()) {
            computeIfAbsentSafe(builtPlanets, key, buildPlanet.get());
        }

        if (!planetToDimensions.isEmpty()) {
            String message = planetToDimensions.values()
                    .stream()
                    .flatMap(List::stream)
                    .map(dimension -> "Dimension \"" + dimensionRegistry.getKey(dimension) + "\" tried to reference planet \"" + dimension.planet().location() + "\" which doesn't exist.")
                    .collect(Collectors.joining("\n"));
            throw new IllegalStateException(message);
        }

        for (Planet node : builtPlanets.values()) {
            node.postBuild();
        }

        planetsById.clear();
        planetsById.putAll(builtPlanets);

        levelToPlanet.clear();
        levelToDimension.clear();
        for (var entry : dimensionRegistry.entrySet()) {
            PlanetDimension dimension = entry.getValue();

            ResourceLocation loc = dimension.dimensionId().location();
            PlanetDimension previousDim = levelToDimension.put(loc, dimension);
            if (previousDim != null && !dimension.equals(previousDim)) {
                throw new IllegalStateException("Dimension \"" + loc + "\" is referenced by planet dimensions \"" + dimensionRegistry.getKey(dimension) + "\" and \"" + dimensionRegistry.getKey(previousDim) + "\"");
            }

            if (dimension.planet() != null) {
                levelToPlanet.put(dimension.dimensionId().location(), planetsById.get(dimension.planet().location()));
            }
        }

        roots.clear();
        roots.addAll(builtPlanets
                .values()
                .stream()
                .filter(planet -> planet.centralBody == null)
                .toList());

        updateOrder.clear();
        systems.clear();
        for (Planet root : roots) {
            List<Planet> planets = new ArrayList<>();
            root.walkPreOrder(planets::add);

            GravitationalSystem system = new GravitationalSystem(root, planets);
            for (Planet planet : planets) {
                systems.put(planet.key.location(), system);
                planet.system = system;
            }

            updateOrder.addAll(planets);
        }
    }

    private static <K, V> V computeIfAbsentSafe(Map<K, V> map, K key, Function<K, V> mappingFunction) {
        V value;
        if ((value = map.get(key)) == null) {
            if ((value = mappingFunction.apply(key)) != null) {
                map.put(key, value);
            }
        }
        return value;
    }

    public double getDeltaDays() {
        return deltaDays;
    }

    @Nullable
    public Planet getPlanetById(ResourceKey<PlanetProperties> key) {
        return key == null ? null : planetsById.get(key.location());
    }

    @Nullable
    public Planet getPlanetById(ResourceLocation key) {
        return planetsById.get(key);
    }

    @Nullable
    public Planet getPlanetByLevel(Level level) {
        ResourceKey<Level> key = level.dimension();
        //noinspection ConstantValue : the dimension should be non-null but in specific case (like Create's schematic rendering dimension) this is null anyways
        return key == null ? null : levelToPlanet.get(key.location());
    }

    @Nullable
    public Planet getPlanetByLevel(ResourceKey<Level> key) {
        return key == null ? null : levelToPlanet.get(key.location());
    }

    @Nullable
    public Planet getPlanetByLevel(ResourceLocation key) {
        return levelToPlanet.get(key);
    }

    @Nullable
    public PlanetDimension getDimensionByLevel(Level level) {
        ResourceKey<Level> key = level.dimension();
        //noinspection ConstantValue : see getPlanetByLevel(Level)
        return key == null ? null : levelToDimension.get(key.location());
    }

    @Nullable
    public PlanetDimension getDimensionByLevel(ResourceKey<Level> key) {
        return key == null ? null : levelToDimension.get(key.location());
    }

    @Nullable
    public PlanetDimension getDimensionByLevel(ResourceLocation key) {
        return levelToDimension.get(key);
    }

    @UnmodifiableView
    public Map<ResourceLocation, Planet> getPlanets() {
        return planetsById;
    }

    @UnmodifiableView
    public List<Planet> getRoots() {
        return roots;
    }

    @UnmodifiableView
    public Map<ResourceLocation, GravitationalSystem> getSystems() {
        return systems;
    }

}
