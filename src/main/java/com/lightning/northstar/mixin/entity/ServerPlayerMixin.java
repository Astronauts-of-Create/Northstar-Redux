package com.lightning.northstar.mixin.entity;

import com.lightning.northstar.accessor.NorthstarServerPlayer;
import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.network.packet.RelativeTeleportPacket;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(ServerPlayer.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ServerPlayerMixin implements NorthstarServerPlayer {

    @Inject(method = "dismountTo", at = @At("TAIL"))
    private void northstar$dismountRelative(double x, double y, double z, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        Player player = (Player) (Object) this;
        if (player.northstar$getRelativeEntity() != null) {
            Vec3 relativePosition = player.northstar$getRelativeEntity().position().subtract(x, y, z);
            NorthstarPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> self), new RelativeTeleportPacket(player.northstar$getRelativeEntity().getId(), relativePosition));
        }
    }

    @Override
    public void northstar$setPositionRelativeTo(Entity other) {
        Player player = (Player) (Object) this;
        player.northstar$setRelativeEntity(other, 2);
    }
}
