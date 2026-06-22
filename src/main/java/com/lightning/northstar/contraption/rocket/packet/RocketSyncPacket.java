package com.lightning.northstar.contraption.rocket.packet;

import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraption.rocket.LaunchStatus;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record RocketSyncPacket(
        int entityId,
        int countdown,
        float velocity,
        LaunchStatus status,
        boolean thrustersEnabled
) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, RocketSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RocketSyncPacket::entityId,
            ByteBufCodecs.VAR_INT, RocketSyncPacket::countdown,
            ByteBufCodecs.FLOAT, RocketSyncPacket::velocity,
            LaunchStatus.STREAM_CODEC, RocketSyncPacket::status,
            ByteBufCodecs.BOOL, RocketSyncPacket::thrustersEnabled,
            RocketSyncPacket::new
    );

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(LocalPlayer player) {
        if (Minecraft.getInstance().level.getEntity(entityId) instanceof RocketContraptionEntity rocket) {
            rocket.onSync(this);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.ROCKET_SYNC;
    }
}
