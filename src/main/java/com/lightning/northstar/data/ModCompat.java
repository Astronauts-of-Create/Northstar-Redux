package com.lightning.northstar.data;

public enum ModCompat implements Tags.Mod {

    CBC("createbigcannons"),
    CDG("createdieselgenerators");

    public final String name;

    ModCompat(String name) {
        this.name = name;
    }

    @Override
    public String getModId() {
        return name;
    }

}
