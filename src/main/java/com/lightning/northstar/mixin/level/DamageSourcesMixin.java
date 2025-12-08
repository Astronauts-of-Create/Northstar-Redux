package com.lightning.northstar.mixin.level;

import com.lightning.northstar.accessor.NorthstarDamageSources;
import com.lightning.northstar.content.NorthstarDamageTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DamageSources.class)
public abstract class DamageSourcesMixin implements NorthstarDamageSources {

    @Shadow
    protected abstract DamageSource source(ResourceKey<DamageType> damageTypeKey);

    @Override
    public DamageSource northstar$suffocation() {
        return source(NorthstarDamageTypes.SUFFOCATION);
    }

}
