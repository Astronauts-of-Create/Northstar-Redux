package com.lightning.northstar.mixin.entity;

import com.lightning.northstar.accessor.NorthstarServerPlayer;
import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.network.packet.RelativeTeleportPacket;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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

    @Inject(method = "dismountTo", at = @At("TAIL"))
    private void northstar$dismountRelative(double x, double y, double z, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;

        if (northstar$relativeEntity != null) {
            Vec3 relativePosition = northstar$relativeEntity.position().subtract(x, y, z);
            NorthstarPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> self), new RelativeTeleportPacket(northstar$relativeEntity.getId(), relativePosition));
        }
    }

    @Override
    @Nullable
    public Entity northstar$getRelativeEntity() {
        return northstar$relativeEntity;
    }

    @Override
    public void northstar$setPositionRelativeTo(Entity other) {
        this.northstar$relativeEntity = other;
        this.northstar$relativeTicks = 2;
    }

}
