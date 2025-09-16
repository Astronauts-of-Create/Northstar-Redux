package com.lightning.northstar.accessor;

import net.minecraft.world.damagesource.DamageSource;

public interface NorthstarDamageSources {

    default DamageSource northstar$suffocation() {
        throw new RuntimeException("This should be implemented by a mixin!");
    }

}
