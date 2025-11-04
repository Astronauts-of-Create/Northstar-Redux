package com.lightning.northstar.mixin.entity;

import com.lightning.northstar.accessor.NorthstarPlayer;
import com.lightning.northstar.item.NorthstarEnchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Player.class)
public class PlayerMixin implements NorthstarPlayer {

    @Unique
    private Entity northstar$relativeEntity;
    @Unique
    private int northstar$relativeTicks;

    @Inject(method = "tick", at = @At("HEAD"))
    private void northstar$tick(CallbackInfo ci) {
        if (northstar$relativeEntity != null && --northstar$relativeTicks <= 0) {
            northstar$relativeEntity = null;
        }
    }

    @Override
    @Nullable
    public Entity northstar$getRelativeEntity() {
        return northstar$relativeEntity;
    }

    @Override
    public void northstar$setRelativeEntity(@Nullable Entity entity, int ticks) {
        northstar$relativeEntity = entity;
        northstar$relativeTicks = ticks;
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void attack(Entity target, CallbackInfo info) {
        Player self = (Player) (Object) this;

        float f = (float) self.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1;
        if (target instanceof LivingEntity) {
            f1 = EnchantmentHelper.getDamageBonus(self.getMainHandItem(), ((LivingEntity) target).getMobType());
        } else {
            f1 = EnchantmentHelper.getDamageBonus(self.getMainHandItem(), MobType.UNDEFINED);
        }
        float f2 = self.getAttackStrengthScale(0.5F);
        f *= 0.2F + f2 * f2 * 0.8F;
        f1 *= f2;

        if (f > 0.0F || f1 > 0.0F) {
            int j = EnchantmentHelper.getEnchantmentLevel(NorthstarEnchantments.FROSTBITE.get(), self);
            if (target instanceof LivingEntity) {
                if (j > 0) {
                    // freezing
                    target.setTicksFrozen((j * 80) + 150);
                }
            }
        }
    }

}
