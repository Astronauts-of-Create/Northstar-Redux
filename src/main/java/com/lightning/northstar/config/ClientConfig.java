package com.lightning.northstar.config;

import com.lightning.northstar.util.TemperatureUnit;
import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class ClientConfig extends ConfigBase {

    public ConfigEnum<TemperatureUnit> temperatureUnit = e(TemperatureUnit.CELSIUS, "temperatureUnit");

    public ConfigGroup debug = group(1, "debug");
    public ConfigBool debugSealerBounds = b(false, "debugSealerBounds");

    public ConfigFloat largeFanChainSize = f(1f,1f,3f, "largeFanChainSize");
    public ConfigBool largeFanConstantShaftWidth = b(false, "largeFanConstantShaftWidth");

    @Override
    public @NotNull String getName() {
        return "client";
    }

}
