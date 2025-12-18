package com.lightning.northstar.mixin.entity;

import com.lightning.northstar.accessor.NorthstarPlayer;
import com.lightning.northstar.accessor.NorthstarServerPlayer;
import com.lightning.northstar.network.packet.RelativeTeleportPacket;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(ServerPlayer.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ServerPlayerMixin implements NorthstarServerPlayer, NorthstarPlayer {

    @Inject(method = "dismountTo", at = @At("TAIL"))
    private void northstar$dismountRelative(double x, double y, double z, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;

        Entity relativeEntity = northstar$getRelativeEntity();
        if (relativeEntity != null) {
            Vec3 relativePosition = relativeEntity.position().subtract(x, y, z);
            CatnipServices.NETWORK.sendToClient(self, new RelativeTeleportPacket(relativeEntity.getId(), relativePosition));
        }
    }

    @Override
    public void northstar$setPositionRelativeTo(Entity other) {
        northstar$setRelativeEntity(other, 2);
    }

}
