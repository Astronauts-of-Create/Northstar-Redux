package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.simibubi.create.foundation.damageTypes.DamageTypeBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class NorthstarDamageTypes {

    public static final ResourceKey<DamageType>
            SUFFOCATION = key("suffocation");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Northstar.asResource(name));
    }

    public static void bootstrap(BootstrapContext<DamageType> context) {
        new DamageTypeBuilder(SUFFOCATION).scaling(DamageScaling.NEVER).register(context);
    }

}
