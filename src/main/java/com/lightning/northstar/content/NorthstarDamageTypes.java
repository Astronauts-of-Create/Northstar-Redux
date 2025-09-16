package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageType;

public class NorthstarDamageTypes {

    public static final ResourceKey<DamageType>
            SUFFOCATION = key("suffocation");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Northstar.asResource(name));
    }

    public static void bootstrap(BootstapContext<DamageType> context) {
        context.register(SUFFOCATION, new DamageType("suffocation", 0.0f, DamageEffects.DROWNING));
    }

}
