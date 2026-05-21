package com.lightning.northstar.accessor;

import net.minecraft.world.damagesource.DamageSource;

public interface NorthstarDamageSources {

    default DamageSource northstar$suffocationNoSuit() {
        throw new MissingMixinException();
    }

    default DamageSource northstar$suffocationNoOxygen() {
        throw new MissingMixinException();
    }

    default DamageSource northstar$acid() {
        throw new MissingMixinException();
    }

}
