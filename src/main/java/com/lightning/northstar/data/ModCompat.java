package com.lightning.northstar.data;

public enum ModCompat implements Mod {

    CBC("createbigcannons"),
    CDG("createdieselgenerators"),
    COPYCATS("copycats"),
    TFMG("tfmg"),
    MEK("mekanism");

    public final String modId;

    ModCompat(String modId) {
        this.modId = modId;
    }

    @Override
    public String getModId() {
        return modId;
    }

}
