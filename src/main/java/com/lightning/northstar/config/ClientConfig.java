package com.lightning.northstar.config;

import com.lightning.northstar.util.TemperatureUnit;
import com.simibubi.create.foundation.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class ClientConfig extends ConfigBase {

    public ConfigEnum<TemperatureUnit> temperatureUnit = e(TemperatureUnit.CELSIUS, "temperatureUnit");

    public ConfigGroup debug = group(1, "debug");
    public ConfigBool debugSealerBounds = b(false, "debugSealerBounds");

    @Override
    public @NotNull String getName() {
        return "client";
    }

}
