package com.lightning.northstar.config;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class CommonConfig extends ConfigBase {

    public final ConfigBool dismountRideableEntityWhenInRocket = b(true, "dismountRideableEntityWhenInRocket");

    @Override
    public @NotNull String getName() {
        return "common";
    }

}
