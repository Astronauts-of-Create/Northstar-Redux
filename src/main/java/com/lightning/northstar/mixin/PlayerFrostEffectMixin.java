package com.lightning.northstar.mixin;

import com.lightning.northstar.item.NorthstarEnchantments;
import com.lightning.northstar.particle.SnowflakeParticleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerFrostEffectMixin extends LivingEntity {

    protected PlayerFrostEffectMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void attack(Entity pTarget, CallbackInfo info) {
        int j = EnchantmentHelper.getEnchantmentLevel(level().holderOrThrow(NorthstarEnchantments.FROSTBITE), this);
        if (pTarget instanceof LivingEntity && j > 0) {
            frost(pTarget);
        }
    }

    public void frost(Entity pEntityHit) {
        if ((Object) this instanceof LocalPlayer) {
            Minecraft.getInstance().particleEngine.createTrackingEmitter(pEntityHit, new SnowflakeParticleData());
        }
    }

}
