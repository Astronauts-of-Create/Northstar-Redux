package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplGravityMixin {

    @Shadow
    public ServerPlayer player;

    @ModifyExpressionValue(
            method = {
                    "handleMovePlayer",
                    "handleMoveVehicle"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;isFlightAllowed()Z"
            )
    )
    private boolean northstar$modifyIsFlying(boolean original) {
        return original || player.level().northstar$isZeroGravity();
    }

}
