package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class NorthstarDamageTypes {

    public static final ResourceKey<DamageType>
            SUFFOCATION = key("suffocation"),
            ACID = key("acid");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Northstar.asResource(name));
    }

    public static void bootstrap(BootstapContext<DamageType> context) {
        context.register(SUFFOCATION, new DamageType("suffocation", DamageScaling.NEVER, 0.0f));
        context.register(ACID, new DamageType("acid", DamageScaling.NEVER, 0.0f));
    }


}
