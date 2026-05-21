package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import net.minecraft.resources.ResourceLocation;

public class NorthstarTextures {

    // @formatter:off
    /** Direct texture to be used in a shader. */
    public static final ResourceLocation
            EMPTY             = texture("block/empty"),
            ACID_RAIN         = texture("environment/acid_rain"),
            MARS_DUST         = texture("environment/mars_dust"),
            SUN               = texture("planet/solar_system/sun");

    /** The dynamic texture for the planet atlas */
    public static final ResourceLocation
            PLANET_ATLAS      = key("textures/atlas/planets.png");

    /** Packed inside {@link NorthstarTextures#PLANET_ATLAS}. */
    public static final ResourceLocation
            POLARIS           = key("polaris"),
            SOL               = planet("sun"),
            CERES_SKY         = planet("ceres_sky"),
            EARTH             = planet("earth"),
            EARTH_CLOUDS      = planet("earth_clouds"),
            EARTH_SKY         = planet("earth_sky"),
                MOON          = planet("earth/moon"),
                MOON_PHASES   = planet("earth/moon_phases"),
                MOON_SKY      = planet("earth/moon_sky"),
            ERIS_SKY          = planet("eris_sky"),
            JUPITER_SKY       = planet("jupiter_sky"),
                CALLISTO_SKY  = planet("jupiter/callisto_sky"),
                EUROPA_SKY    = planet("jupiter/europa_sky"),
                GANYMEDE_SKY  = planet("jupiter/ganymede_sky"),
                IO_SKY        = planet("jupiter/io_sky"),
            MARS              = planet("mars"),
            MARS_SKY          = planet("mars_sky"),
                DEIMOS_SKY    = planet("mars/deimos_sky"),
                PHOBOS_SKY    = planet("mars/phobos_sky"),
            MERCURY           = planet("mercury"),
            MERCURY_SKY       = planet("mercury_sky"),
            NEPTUNE_SKY       = planet("neptune_sky"),
                NEREID_SKY    = planet("neptune/nereid_sky"),
                PROTEUS_SKY   = planet("neptune/proteus_sky"),
                TRITON_SKY    = planet("neptune/triton_sky"),
            PLUTO_SKY         = planet("pluto_sky"),
                CHARON_SKY    = planet("pluto/charon_sky"),
            SATURN_SKY        = planet("saturn_sky"),
                DIONE_SKY     = planet("saturn/dione_sky"),
                ENCELADUS_SKY = planet("saturn/enceladus_sky"),
                HYPERION_SKY  = planet("saturn/hyperion_sky"),
                IAPETUS_SKY   = planet("saturn/iapetus_sky"),
                MIMAS_SKY     = planet("saturn/mimas_sky"),
                PHOEBE_SKY    = planet("saturn/phoebe_sky"),
                RHEA_SKY      = planet("saturn/rhea_sky"),
                TITAN_SKY     = planet("saturn/titan_sky"),
            URANUS_SKY        = planet("uranus_sky"),
                ARIEL_SKY     = planet("uranus/ariel_sky"),
                OBERON_SKY    = planet("uranus/oberon_sky"),
                UMBRIEL_SKY   = planet("uranus/umbriel_sky"),
                TITANIA_SKY   = planet("uranus/titania_sky"),
            VENUS             = planet("venus"),
            VENUS_SKY         = planet("venus_sky");
    // @formatter:on

    /** Key for a planet texture stored in the {@linkplain NorthstarTextures#PLANET_ATLAS planet atlas} */
    private static ResourceLocation planet(String path) {
        return key("solar_system/" + path);
    }

    private static ResourceLocation environment(String path) {
        return texture("environment/" + path);
    }

    private static ResourceLocation texture(String path) {
        return key("textures/" + path + ".png");
    }

    private static ResourceLocation key(String path) {
        return Northstar.asResource(path);
    }

}
