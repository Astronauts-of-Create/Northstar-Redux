package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.world.NorthstarDimensions;
import com.lightning.northstar.planet.data.PlanetProperties;
import com.lightning.northstar.planet.data.orbit.FixedOrbitProvider;
import com.lightning.northstar.planet.data.orbit.SimpleOrbitProvider;
import com.lightning.northstar.planet.data.render.ConditionalPlanetRenderer;
import com.lightning.northstar.planet.data.render.PhasedPlanetRenderer;
import com.lightning.northstar.planet.data.render.SimplePlanetRenderer;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import org.joml.Vector3f;

public class NorthstarPlanets {

    // @formatter:off
    public static final ResourceKey<PlanetProperties>
            SOL               = key("sol"),
                CERES         = key("ceres"),
                EARTH         = key("earth"),
                    THE_MOON  = key("the_moon"),
                ERIS          = key("eris"),
                JUPITER       = key("jupiter"),
                    CALLISTO  = key("callisto"),
                    EUROPA    = key("europa"),
                    GANYMEDE  = key("ganymede"),
                    IO        = key("io"),
                MARS          = key("mars"),
                    DEIMOS    = key("deimos"),
                    PHOBOS    = key("phobos"),
                MERCURY       = key("mercury"),
                NEPTUNE       = key("neptune"),
                    NEREID    = key("nereid"),
                    PROTEUS   = key("proteus"),
                    TRITON    = key("triton"),
                PLUTO         = key("pluto"),
                    CHARON    = key("charon"),
                SATURN        = key("saturn"),
                    DIONE     = key("dione"),
                    ENCELADUS = key("enceladus"),
                    HYPERION  = key("hyperion"),
                    IAPETUS   = key("iapetus"),
                    MIMAS     = key("mimas"),
                    PHOEBE    = key("phoebe"),
                    RHEA      = key("rhea"),
                    TITAN     = key("titan"),
                URANUS        = key("uranus"),
                    ARIEL     = key("ariel"),
                    OBERON    = key("oberon"),
                    UMBRIEL   = key("umbriel"),
                    TITANIA   = key("titania"),
                VENUS         = key("venus");
    // @formatter:on

    public static void bootstrap(BootstrapContext<PlanetProperties> context) {
        context.register(
                SOL,
                PlanetProperties.builder()
                        .orbit(new FixedOrbitProvider(new Vector3f()))
                        .type("star")
                        .rotationPeriodDays(24.47)
                        .diameter(1391400)
                        .renderer(NorthstarTextures.SOL)
                        .build()
        );

        context.register(
                CERES,
                PlanetProperties.builder()
                        .centralBody(SOL)
                        .orbit(SimpleOrbitProvider.create(1680, 2.77, 10.6, 80.3, 73.3))
                        .type("dwarf_planet")
                        .rotationPeriodHours(9)
                        .diameter(946)
                        .renderer(NorthstarTextures.CERES_SKY)
                        .build()
        );

        context.register(
                EARTH,
                PlanetProperties.builder()
                        .centralBody(SOL)
                        .orbit(SimpleOrbitProvider.create(365.256, 1, 0, 0, 357.51716))
                        .type("planet")
                        .requiredScience(1)
                        .rotationPeriodDays(1)
                        .diameter(12742)
                        .texture(
                                new PlanetProperties.TextureLayer(NorthstarTextures.EARTH),
                                new PlanetProperties.TextureLayer(NorthstarTextures.EARTH_CLOUDS, 1, false)
                        )
                        .renderer(new ConditionalPlanetRenderer(
                                NorthstarDimensions.THE_MOON,
                                new SimplePlanetRenderer(NorthstarTextures.EARTH),
                                new SimplePlanetRenderer(NorthstarTextures.EARTH_SKY)
                        ))
                        .build()
        );

        context.register(
                THE_MOON,
                PlanetProperties.builder()
                        .centralBody(EARTH)
                        .orbit(SimpleOrbitProvider.create(27.322, 0.00257, 5.145, 125.08, 0)) // real moon
                        //.orbit(SimpleOrbitProvider.create(365.256, 0.00257, 5.145, 125.08, 357.51716)) // Minecraft moon (copies parameters from earth to be on the opposite side of the sun)
                        .type("moon")
                        .requiredScience(2) // 3 readings from Earth
                        .rotationPeriodTidalLock()
                        .diameter(3474.8)
                        .texture(NorthstarTextures.MOON)
                        .renderer(new PhasedPlanetRenderer(NorthstarTextures.MOON_PHASES, 24000, 8, -6000, 4, 2))
                        .build()
        );

        context.register(
                ERIS,
                PlanetProperties.builder()
                        .centralBody(SOL)
                        .orbit(SimpleOrbitProvider.create(204056, 67.864, 44.04, 35.951, 0))
                        .type("dwarf_planet")
                        .rotationPeriodHours(25.9)
                        .diameter(2326)
                        .renderer(NorthstarTextures.ERIS_SKY)
                        .build()
        );

        context.register(
                JUPITER,
                PlanetProperties.builder()
                        .centralBody(SOL)
                        .orbit(SimpleOrbitProvider.create(4333, 5, 1.305, 100.55615, 19.65053))
                        .type("gas_giant")
                        .rotationPeriodDays(0.41354)
                        .diameter(139820)
                        .renderer(NorthstarTextures.JUPITER_SKY)
                        .build()
        );

        context.register(
                CALLISTO,
                PlanetProperties.builder()
                        .centralBody(JUPITER)
                        .orbit(SimpleOrbitProvider.create(16.6890184, 0.01258507218, 2.017, 0, 0))
                        .type("moon")
                        .rotationPeriodTidalLock()
                        .diameter(4806)
                        .renderer(NorthstarTextures.CALLISTO_SKY)
                        .build()
        );

        context.register(
                EUROPA,
                PlanetProperties.builder()
                        .centralBody(JUPITER)
                        .orbit(SimpleOrbitProvider.create(3.551181, 0.00448602642, 3.596, 0, 0))
                        .type("moon")
                        .rotationPeriodTidalLock()
                        .diameter(3121.6)
                        .renderer(NorthstarTextures.EUROPA_SKY)
                        .build()
        );

        context.register(
                GANYMEDE,
                PlanetProperties.builder()
                        .centralBody(JUPITER)
                        .orbit(SimpleOrbitProvider.create(7.155, 0.0071551820557, 3.307, 0, 0))
                        .type("moon")
                        .rotationPeriodTidalLock()
                        .diameter(5268.2)
                        .renderer(NorthstarTextures.GANYMEDE_SKY)
                        .build()
        );

        context.register(
                IO,
                PlanetProperties.builder()
                        .centralBody(JUPITER)
                        .orbit(SimpleOrbitProvider.create(1.77, 0.00281955885, 3.18, 0, 0))
                        .type("moon")
                        .rotationPeriodTidalLock()
                        .diameter(3643.2)
                        .renderer(NorthstarTextures.IO_SKY)
                        .build()
        );

        context.register(
                MARS,
                PlanetProperties.builder()
                        .centralBody(SOL)
                        .orbit(SimpleOrbitProvider.create(686.93, 1.52366231, 1.85061, 49.57854, 19.39020))
                        .type("planet")
                        .requiredScience(5) // 3 readings from Earth and The Moon
                        .rotationPeriodDays(1.0259541)
                        .diameter(6779)
                        .texture(NorthstarTextures.MARS)
                        .renderer(NorthstarTextures.MARS_SKY)
                        .build()
        );

        context.register(
                DEIMOS,
                PlanetProperties.builder()
                        .centralBody(MARS)
                        .orbit(SimpleOrbitProvider.create(1.262, 0.000156807045, 26.12, 0, 0))
                        .type("moon")
                        .rotationPeriodDays(1.263)
                        .diameter(12.4)
                        .renderer(NorthstarTextures.DEIMOS_SKY)
                        .build()
        );

        context.register(
                PHOBOS,
                PlanetProperties.builder()
                        .centralBody(MARS)
                        .orbit(SimpleOrbitProvider.create(0.31875, 0.00006267469, 26.283, 0, 0))
                        .type("moon")
                        .rotationPeriodHours(7.653)
                        .diameter(22.533)
                        .renderer(NorthstarTextures.PHOBOS_SKY)
                        .build()
        );

        context.register(
                MERCURY,
                PlanetProperties.builder()
                        .centralBody(SOL)
                        .orbit(SimpleOrbitProvider.create(88, 0.38709893, 7.0487, 48.33167, 174.79253))
                        .type("planet")
                        .requiredScience(6)
                        .rotationPeriodDays(58.64583)
                        .diameter(4879.4)
                        .texture(NorthstarTextures.MERCURY)
                        .renderer(NorthstarTextures.MERCURY_SKY)
                        .build()
        );

        context.register(
                NEPTUNE,
                PlanetProperties.builder()
                        .centralBody(SOL)
                        .orbit(SimpleOrbitProvider.create(60190, 30.07, 1.77, 131.72, 256.228))
                        .type("planet")
                        .rotationPeriodDays(0.6713)
                        .diameter(49244)
                        .renderer(NorthstarTextures.NEPTUNE_SKY)
                        .build()
        );

        context.register(
                NEREID,
                PlanetProperties.builder()
                        .centralBody(NEPTUNE)
                        .orbit(SimpleOrbitProvider.create(359.18, 0.037, 5.8, 326, 0))
                        .type("moon")
                        .rotationPeriodHours(13.6)
                        .diameter(340)
                        .renderer(NorthstarTextures.NEREID_SKY)
                        .build()
        );

        context.register(
                PROTEUS,
                PlanetProperties.builder()
                        .centralBody(NEPTUNE)
                        .orbit(SimpleOrbitProvider.create(1.122, 0.00078641493659, 28.844, 0, 0))
                        .type("moon")
                        .rotationPeriodHours(26.9)
                        .diameter(416)
                        .renderer(NorthstarTextures.PROTEUS_SKY)
                        .build()
        );

        context.register(
                TRITON,
                PlanetProperties.builder()
                        .centralBody(NEPTUNE)
                        .orbit(SimpleOrbitProvider.create(5.877, 0.0023714174429, 129.608, 177.709, 0))
                        .type("moon")
                        .rotationPeriodDays(5.877)
                        .diameter(2710)
                        .renderer(NorthstarTextures.TRITON_SKY)
                        .build()
        );

        context.register(
                PLUTO,
                PlanetProperties.builder()
                        .centralBody(SOL)
                        .orbit(SimpleOrbitProvider.create(90560, 39.48, 17.16, 110.3, 14.53))
                        .type("dwarf_planet")
                        .rotationPeriodDays(6.387)
                        .diameter(2376.6)
                        .renderer(NorthstarTextures.PLUTO_SKY)
                        .build()
        );

        context.register(
                CHARON,
                PlanetProperties.builder()
                        .centralBody(PLUTO)
                        .orbit(SimpleOrbitProvider.create(6.387, 0.000130984485, 0.08, 223.046, 0))
                        .type("moon")
                        .rotationPeriodDays(6.387)
                        .diameter(1212)
                        .renderer(NorthstarTextures.CHARON_SKY)
                        .build()
        );

        context.register(
                SATURN,
                PlanetProperties.builder()
                        .centralBody(SOL)
                        .orbit(SimpleOrbitProvider.create(10755.699, 5.95, 2.486, 113.71504, 49.94435))
                        .type("gas_giant")
                        .rotationPeriodHours(10.656)
                        .diameter(120500)
                        .renderer(NorthstarTextures.SATURN_SKY)
                        .build()
        );

        context.register(
                DIONE,
                PlanetProperties.builder()
                        .centralBody(SATURN)
                        .orbit(SimpleOrbitProvider.create(2.738, 0.0025228968717, 26.758, 0, 0))
                        .type("moon")
                        .rotationPeriodTidalLock()
                        .diameter(1122.8)
                        .renderer(NorthstarTextures.DIONE_SKY)
                        .build()
        );

        context.register(
                ENCELADUS,
                PlanetProperties.builder()
                        .centralBody(SATURN)
                        .orbit(SimpleOrbitProvider.create(1.371, 0.00159119912, 26.739, 0, 0))
                        .type("moon")
                        .rotationPeriodTidalLock()
                        .diameter(504.2)
                        .renderer(NorthstarTextures.ENCELADUS_SKY)
                        .build()
        );

        context.register(
                HYPERION,
                PlanetProperties.builder()
                        .centralBody(SATURN)
                        .orbit(SimpleOrbitProvider.create(21.281, 0.00989993368936, 27.16, 0, 0))
                        .type("moon")
                        .rotationPeriodHours(21.276)
                        .diameter(270)
                        .renderer(NorthstarTextures.HYPERION_SKY)
                        .build()
        );

        context.register(
                IAPETUS,
                PlanetProperties.builder()
                        .centralBody(SATURN)
                        .orbit(SimpleOrbitProvider.create(79.339, 0.024, 42.2, 0, 0))
                        .type("moon")
                        .rotationPeriodDays(79.33)
                        .diameter(1469)
                        .renderer(NorthstarTextures.IAPETUS_SKY)
                        .build()
        );

        context.register(
                MIMAS,
                PlanetProperties.builder()
                        .centralBody(SATURN)
                        .orbit(SimpleOrbitProvider.create(0.94366, 0.00124025829, 28.304, 0, 0))
                        .type("moon")
                        .rotationPeriodHours(22.6166)
                        .diameter(396.4)
                        .renderer(NorthstarTextures.MIMAS_SKY)
                        .build()
        );

        context.register(
                PHOEBE,
                PlanetProperties.builder()
                        .centralBody(SATURN)
                        .orbit(SimpleOrbitProvider.create(546.04, 0.086, 178.51, 269.351, 180))
                        .type("moon")
                        .rotationPeriodHours(9.274)
                        .diameter(216.6)
                        .renderer(NorthstarTextures.PHOEBE_SKY)
                        .build()
        );

        context.register(
                RHEA,
                PlanetProperties.builder()
                        .centralBody(SATURN)
                        .orbit(SimpleOrbitProvider.create(4.518, 0.0035232453345, 27.075, 0, 0))
                        .type("moon")
                        .rotationPeriodDays(4.52)
                        .diameter(1529)
                        .renderer(NorthstarTextures.RHEA_SKY)
                        .build()
        );

        context.register(
                TITAN,
                PlanetProperties.builder()
                        .centralBody(SATURN)
                        .orbit(SimpleOrbitProvider.create(15.948, 0.008167696467, 27.079, 0, 0))
                        .type("moon")
                        .rotationPeriodDays(15.94)
                        .diameter(5149.5)
                        .renderer(NorthstarTextures.TITAN_SKY)
                        .build()
        );

        context.register(
                URANUS,
                PlanetProperties.builder()
                        .centralBody(SOL)
                        .orbit(SimpleOrbitProvider.create(30687, 19.22, 0.77, 74.006, 142.2386))
                        .type("planet")
                        .rotationPeriodDays(-0.718)
                        .diameter(50724)
                        .renderer(NorthstarTextures.URANUS_SKY)
                        .build()
        );

        context.register(
                ARIEL,
                PlanetProperties.builder()
                        .centralBody(URANUS)
                        .orbit(SimpleOrbitProvider.create(2.52, 0.00127608768, 82.49, 0, 0))
                        .type("moon")
                        .rotationPeriodTidalLock()
                        .diameter(1157.8)
                        .renderer(NorthstarTextures.ARIEL_SKY)
                        .build()
        );

        context.register(
                OBERON,
                PlanetProperties.builder()
                        .centralBody(URANUS)
                        .orbit(SimpleOrbitProvider.create(13.467, 0.00390059028, 82.288, 0, 0))
                        .type("moon")
                        .rotationPeriodTidalLock()
                        .diameter(1521)
                        .renderer(NorthstarTextures.OBERON_SKY)
                        .build()
        );

        context.register(
                UMBRIEL,
                PlanetProperties.builder()
                        .centralBody(URANUS)
                        .orbit(SimpleOrbitProvider.create(4.145, 0.00177810017, 82.358, 0, 0))
                        .type("moon")
                        .rotationPeriodTidalLock()
                        .diameter(1169.4)
                        .renderer(NorthstarTextures.UMBRIEL_SKY)
                        .build()
        );

        context.register(
                TITANIA,
                PlanetProperties.builder()
                        .centralBody(URANUS)
                        .orbit(SimpleOrbitProvider.create(8.654, 0.00291387837, 82.57, 0, 0))
                        .type("moon")
                        .rotationPeriodTidalLock()
                        .diameter(1576.8)
                        .renderer(NorthstarTextures.TITANIA_SKY)
                        .build()
        );

        context.register(
                VENUS,
                PlanetProperties.builder()
                        .centralBody(SOL)
                        .orbit(SimpleOrbitProvider.create(224.7, 0.72333199, 3.39471, 76.68069, 50.37663))
                        .type("planet")
                        .requiredScience(6)
                        .rotationPeriodDays(-224.701)
                        .diameter(12104)
                        .texture(NorthstarTextures.VENUS)
                        .renderer(NorthstarTextures.VENUS_SKY)
                        .build()
        );
    }

    private static ResourceKey<PlanetProperties> key(String path) {
        return ResourceKey.create(NorthstarRegistries.PLANET, Northstar.asResource(path));
    }

}
