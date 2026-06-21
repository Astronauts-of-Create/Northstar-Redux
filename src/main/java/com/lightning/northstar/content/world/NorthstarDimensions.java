package com.lightning.northstar.content.world;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarPlanets;
import com.lightning.northstar.content.NorthstarRegistries;
import com.lightning.northstar.content.NorthstarWeathers;
import com.lightning.northstar.content.world.planet.core.NorthstarBiomes;
import com.lightning.northstar.planet.data.Atmosphere;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.lightning.northstar.planet.data.func.DispatchableFunction;
import com.lightning.northstar.planet.data.func.LevelFunction;
import com.lightning.northstar.planet.data.func.MercuryTemperatureFunction;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.joml.Vector2f;

import java.util.Map;

public class NorthstarDimensions {

    // @formatter:off
    public static final ResourceKey<Level>
            EARTH         = Level.OVERWORLD,
            EARTH_ORBIT   = key("earth_orbit"),
                THE_MOON  = key("moon"),
            MARS          = key("mars"),
            MARS_ORBIT    = key("mars_orbit"),
            MERCURY       = key("mercury"),
            MERCURY_ORBIT = key("mercury_orbit"),
            VENUS         = key("venus"),
            VENUS_ORBIT   = key("venus_orbit");
    // @formatter:on

    private static ResourceKey<Level> key(String path) {
        return ResourceKey.create(Registries.DIMENSION, Northstar.asResource(path));
    }

    private static ResourceKey<LevelStem> stem(ResourceKey<Level> key) {
        return key.cast(Registries.LEVEL_STEM).orElseThrow();
    }

    private static ResourceKey<PlanetDimension> dim(ResourceKey<?> key) {
        return ResourceKey.create(NorthstarRegistries.PLANET_DIMENSION, key.location());
    }

    public static void bootstrapDimensions(BootstrapContext<PlanetDimension> context) {
        context.register(
                dim(NorthstarPlanets.EARTH),
                PlanetDimension.builder()
                        .planet(NorthstarPlanets.EARTH)
                        .name("surface")
                        .dimension(EARTH)
                        .dimensionAbove(EARTH_ORBIT)
                        .temperature(NorthstarTemperature.DEFAULT)
                        .heatRequirement(100, 0.4f)
                        .build()
        );

        context.register(
                dim(EARTH_ORBIT),
                PlanetDimension.orbit(NorthstarPlanets.EARTH, EARTH_ORBIT, EARTH)
        );

        context.register(
                dim(THE_MOON),
                PlanetDimension.builder()
                        .planet(NorthstarPlanets.THE_MOON)
                        .name("surface")
                        .dimension(THE_MOON)
                        .noAtmosphere()
                        // Rounded down from 1.625 to 1.6 to allow the player to jump 5 blocks instead of the frustrating 4.9375
                        .gravity(1.6f)
                        .temperature(-183)
                        .sun(1.5f)
                        .longitudeOffsetDeg(135)
                        .build()
        );

        context.register(
                dim(MARS),
                PlanetDimension.builder()
                        .planet(NorthstarPlanets.MARS)
                        .name("surface")
                        .dimension(MARS)
                        .dimensionAbove(MARS_ORBIT)
                        .atmosphere(Atmosphere.builder()
                                .fluid(NorthstarFluids.CARBON)
                                .pressurePa(610)
                                .daytimeStarBrightness(0.4f)
                                .build())
                        .gravity(3.71f)
                        .temperature(-100)
                        .wind(DispatchableFunction.WEATHER.create(
                                LevelFunction.constant(1),
                                Map.of(
                                        NorthstarWeathers.CLEAR, LevelFunction.constant(0)
                                )
                        ))
                        .sun(1.2f)
                        .heatRequirement(50, 0.05f)
                        .build()
        );
        context.register(
                dim(MARS_ORBIT),
                PlanetDimension.orbit(NorthstarPlanets.MARS, MARS_ORBIT, MARS)
        );

        context.register(
                dim(MERCURY),
                PlanetDimension.builder()
                        .planet(NorthstarPlanets.MERCURY)
                        .name("surface")
                        .dimension(MERCURY)
                        .dimensionAbove(MERCURY_ORBIT)
                        .noAtmosphere()
                        .gravity(3.7f)
                        .temperature(new MercuryTemperatureFunction(), new Vector2f(-200, 434))
                        //.temperature(LevelFunction.expression("IF(CAN_SEE_SKY(pos) && !IS_NIGHT(), 434, -200)"), new Vector2f(-200, 434))
                        .sun(8f)
                        .heatRequirement(100, 2)
                        .build()
        );
        context.register(
                dim(MERCURY_ORBIT),
                PlanetDimension.orbit(NorthstarPlanets.MERCURY, MERCURY_ORBIT, MERCURY)
        );

        context.register(
                dim(VENUS),
                PlanetDimension.builder()
                        .planet(NorthstarPlanets.VENUS)
                        .name("surface")
                        .dimension(VENUS)
                        .dimensionAbove(VENUS_ORBIT)
                        .atmosphere(Atmosphere.builder()
                                .fluid(NorthstarFluids.CARBON)
                                .pressureAtm(90.7969f)
                                .build())
                        .gravity(8.87f)
                        .temperature(464)
                        .wind(DispatchableFunction.WEATHER.create(
                                LevelFunction.constant(0.7f),
                                Map.of(
                                        NorthstarWeathers.CLEAR, LevelFunction.constant(0.4f)
                                )
                        ))
                        .sun(0.6f)
                        .heatRequirement(250, 5)
                        .build()
        );
        context.register(
                dim(VENUS_ORBIT),
                PlanetDimension.orbit(NorthstarPlanets.VENUS, VENUS_ORBIT, VENUS)
        );
    }

    public static void bootstrapLevelStems(BootstrapContext<LevelStem> context) {
        registerOrbit(context, EARTH_ORBIT);
        registerOrbit(context, MARS_ORBIT);
        registerOrbit(context, MERCURY_ORBIT);
        registerOrbit(context, VENUS_ORBIT);
    }

    private static void registerOrbit(BootstrapContext<LevelStem> context, ResourceKey<Level> key) {
        HolderGetter<DimensionType> dimension = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<Biome> biome = context.lookup(Registries.BIOME);
        HolderGetter<NoiseGeneratorSettings> noise = context.lookup(Registries.NOISE_SETTINGS);

        context.register(
                stem(key),
                new LevelStem(
                        dimension.getOrThrow(NorthstarDimensionTypes.ORBIT),
                        new NoiseBasedChunkGenerator(
                                new FixedBiomeSource(biome.getOrThrow(NorthstarBiomes.VOID)),
                                noise.getOrThrow(NorthstarNoiseGeneratorSettings.VOID)
                        )
                )
        );
    }

}
