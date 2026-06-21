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
            ACID = key("acid"),
            SUFFOCATION_NO_OXYGEN = key("suffocation_no_oxygen"),
            SUFFOCATION_NO_SPACESUIT = key("suffocation_no_spacesuit");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Northstar.asResource(name));
    }

    public static void bootstrap(BootstrapContext<DamageType> context) {
        new DamageTypeBuilder(ACID).scaling(DamageScaling.NEVER).register(context);
        new DamageTypeBuilder(SUFFOCATION_NO_OXYGEN).scaling(DamageScaling.NEVER).register(context);
        new DamageTypeBuilder(SUFFOCATION_NO_SPACESUIT).scaling(DamageScaling.NEVER).register(context);
    }

}
