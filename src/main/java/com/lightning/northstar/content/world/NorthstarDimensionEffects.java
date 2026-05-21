package com.lightning.northstar.content.world;

import com.lightning.northstar.Northstar;
import net.minecraft.resources.ResourceLocation;

public class NorthstarDimensionEffects {

    public static final ResourceLocation
            SPACE = key("space"),
            ORBIT = key("orbit"),
            MARS = key("mars"),
            VENUS = key("venus");

    private static ResourceLocation key(String path) {
        return Northstar.asResource(path);
    }

}
