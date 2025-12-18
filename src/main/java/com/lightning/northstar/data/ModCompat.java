package com.lightning.northstar.data;

public enum ModCompat implements Mod {

    CBC("createbigcannons"),
    CDG("createdieselgenerators"),
    COPYCATS("copycats"),
    KJS("kubejs"),
    KJS_CREATE("kubejs_create"),
    MEK("mekanism"),
    TFMG("tfmg");

    public final String modId;

    ModCompat(String modId) {
        this.modId = modId;
    }

    @Override
    public String getModId() {
        return modId;
    }

}
