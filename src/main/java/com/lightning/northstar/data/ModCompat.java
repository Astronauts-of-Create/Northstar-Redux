package com.lightning.northstar.data;

import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.ApiStatus;

public enum ModCompat implements Mod {

    CBC("createbigcannons"),
    CDG("createdieselgenerators"),
    COPYCATS("copycats"),
    JEI("justenoughitems"),
    KJS("kubejs"),
    KJS_CREATE("kubejs_create"),
    MEK("mekanism"),
    OCULUS("iris"),
    SABLE("sable"),
    TFMG("tfmg"),
    TFMG_CE("tfmg");

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
        return switch (this) {
            case JEI -> Mod.super.isLoaded() || HAS_JEI_RUNTIME;
            case TFMG -> checkTFMG(false);
            case TFMG_CE -> checkTFMG(true);
            default -> Mod.super.isLoaded();
        };
    }

    private static boolean checkTFMG(boolean community) {
        return FMLLoader.getLoadingModList()
                .getMods()
                .stream()
                .anyMatch(mod -> mod.getModId().equals("tfmg") && mod.getVersion().toString().contains("community") == community);
    }

}
