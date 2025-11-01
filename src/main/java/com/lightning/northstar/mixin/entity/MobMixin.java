package com.lightning.northstar.mixin.entity;

import com.lightning.northstar.item.NorthstarEnchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {

    @Inject(method = "doHurtTarget", at = @At("HEAD"))
    private void doHurtTarget(Entity entity, CallbackInfoReturnable<Boolean> info) {
        int i = EnchantmentHelper.getEnchantmentLevel(NorthstarEnchantments.FROSTBITE.get(), (Mob) (Object) this);
        if (i > 0) {
            entity.setTicksFrozen((i * 80) + 150);
        }
    }

}
