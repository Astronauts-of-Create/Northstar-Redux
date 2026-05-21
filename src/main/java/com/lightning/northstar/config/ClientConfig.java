package com.lightning.northstar.config;

import com.lightning.northstar.util.PressureUnit;
import com.lightning.northstar.util.TemperatureUnit;
import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class ClientConfig extends ConfigBase {

    public final ConfigEnum<TemperatureUnit> temperatureUnit = e(TemperatureUnit.CELSIUS, "temperatureUnit");
    public final ConfigEnum<PressureUnit> pressureUnit = e(PressureUnit.PASCAL, "pressureUnit");

    public final ConfigGroup debug = group(1, "debug");
    public final ConfigBool debugSealerBounds = b(false, "debugSealerBounds");

    @Override
    public @NotNull String getName() {
        return "client";
    }

}
