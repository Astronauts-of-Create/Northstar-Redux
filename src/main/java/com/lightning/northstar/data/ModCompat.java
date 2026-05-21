package com.lightning.northstar.data;

import org.jetbrains.annotations.ApiStatus;

public enum ModCompat implements Mod {

    CBC("createbigcannons"),
    CDG("createdieselgenerators"),
    COPYCATS("copycats"),
    JEI("justenoughitems"),
    KJS("kubejs"),
    MEK("mekanism"),
    OCULUS("oculus"),
    TFMG("tfmg"),
    VS2("valkyrienskies");

    @ApiStatus.Internal
    public static boolean HAS_JEI_RUNTIME;

    public final String modId;

    ModCompat(String modId) {
        this.modId = modId;
    }

    @Override
    public String getModId() {
        return modId;
    }

    @Override
    public boolean isLoaded() {
        return Mod.super.isLoaded() || (this == JEI && HAS_JEI_RUNTIME);
    }

}
