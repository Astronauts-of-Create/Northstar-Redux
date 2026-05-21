package com.lightning.northstar.mixin.client;

import com.lightning.northstar.accessor.NorthstarMinecraft;
import com.lightning.northstar.api.client.NorthstarDimensionEffectsExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MinecraftMixin implements NorthstarMinecraft {

    @Shadow
    @Nullable
    public ClientLevel level;

    @Unique
    private boolean northstar$shouldRenderLevel;

    @Inject(method = "runTick", at = @At("HEAD"))
    private void northstar$runTick(boolean renderLevel, CallbackInfo ci) {
        northstar$shouldRenderLevel = renderLevel;

        if (renderLevel && level != null && level.effects() instanceof NorthstarDimensionEffectsExtension effects) {
            effects.northstar$onFrameStart();
        }
    }

    @Override
    public boolean northstar$shouldRenderLevel() {
        return northstar$shouldRenderLevel;
    }

}
