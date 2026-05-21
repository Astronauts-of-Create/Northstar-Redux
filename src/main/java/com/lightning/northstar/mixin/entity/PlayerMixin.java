package com.lightning.northstar.mixin.entity;

import com.lightning.northstar.accessor.NorthstarPlayer;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(Player.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class PlayerMixin extends LivingEntity implements NorthstarPlayer {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void northstar$showTitle(Component title, Component subtitle, int fadeInTime, int displayTime, int fadeOutTime) {
    }

    @ModifyExpressionValue(
            method = "tryToStartFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;canElytraFly(Lnet/minecraft/world/entity/LivingEntity;)Z",
                    remap = false
            )
    )
    private boolean northstar$updateElytraFlight(boolean original) {
        return original && NorthstarOxygen.hasOxygen(level(), position());
    }

}
