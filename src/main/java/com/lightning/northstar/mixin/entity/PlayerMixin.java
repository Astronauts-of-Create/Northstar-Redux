package com.lightning.northstar.mixin.entity;

import com.lightning.northstar.accessor.NorthstarPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
}
