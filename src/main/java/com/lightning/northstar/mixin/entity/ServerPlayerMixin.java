package com.lightning.northstar.mixin.entity;

import com.lightning.northstar.accessor.NorthstarPlayer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(ServerPlayer.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ServerPlayerMixin implements NorthstarPlayer {

    @Shadow
    public ServerGamePacketListenerImpl connection;

    @Override
    public void northstar$showTitle(Component title, Component subtitle, int fadeInTime, int displayTime, int fadeOutTime) {
        connection.send(new ClientboundSetTitlesAnimationPacket(fadeInTime, displayTime, fadeOutTime));
        connection.send(new ClientboundSetSubtitleTextPacket(subtitle));
        connection.send(new ClientboundSetTitleTextPacket(title));
    }

}
