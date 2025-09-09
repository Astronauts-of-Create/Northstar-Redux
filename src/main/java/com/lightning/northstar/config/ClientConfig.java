package com.lightning.northstar.config;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class ClientConfig extends ConfigBase {

    public ConfigGroup debug = group(1, "debug");
    public ConfigBool debugSealerBounds = b(false, "debugSealerBounds");

    @Override
    public @NotNull String getName() {
        return "client";
    }

}
