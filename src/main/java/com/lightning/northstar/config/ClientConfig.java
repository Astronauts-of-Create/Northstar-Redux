package com.lightning.northstar.config;

import com.lightning.northstar.util.PressureUnit;
import com.lightning.northstar.util.TemperatureUnit;
import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class ClientConfig extends ConfigBase {

    public final ConfigEnum<TemperatureUnit> temperatureUnit = e(TemperatureUnit.CELSIUS, "temperatureUnit");
    public final ConfigEnum<PressureUnit> pressureUnit = e(PressureUnit.PASCAL, "pressureUnit");

    public final ConfigBool alwaysEnableThrusterParticles = b(false, "alwaysEnableThrusterParticles", "Whether rocket thrusters on contraptions should always produce particles shown during countdown.");

    public final ConfigEnum<StarVisibility> planetVisibility = e(StarVisibility.ALWAYS, "planetVisibility", "The visibility of other planets in the sky", StarVisibility.COMMENT);
    public final ConfigEnum<StarVisibility> starVisibility = e(StarVisibility.OUTSIDE_ATMOSPHERE, "starVisibility", "The visibility of additional stars in the sky", StarVisibility.COMMENT);

    public final ConfigGroup debug = group(1, "debug");
    public final ConfigBool debugSealerBounds = b(false, "debugSealerBounds");

    @Override
    public @NotNull String getName() {
        return "client";
    }

    public enum StarVisibility {
        ALWAYS,
        OUTSIDE_ATMOSPHERE,
        NEVER;

        public static final String COMMENT = "Always: Always visible, matching vanilla star visibility.\nOutside Atmosphere: When above the planet's atmosphere defined in the server config or on planets where stars are always visible.\nNever: never render additional stars, fully empty sky.";

        public float getBrightness(float baseBrightness, float atmosphereBlend) {
            return switch (this) {
                case ALWAYS -> Math.max(baseBrightness, atmosphereBlend);
                case OUTSIDE_ATMOSPHERE -> atmosphereBlend;
                case NEVER -> 0f;
            };
        }
    }

}
