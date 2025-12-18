package com.lightning.northstar.contraption.rocket.packet;

import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;

public record RocketContraptionQuickSyncPacket(
        int contraptionEntityId,
        boolean slowing
) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, RocketContraptionQuickSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RocketContraptionQuickSyncPacket::contraptionEntityId,
            ByteBufCodecs.BOOL, RocketContraptionQuickSyncPacket::slowing,
            RocketContraptionQuickSyncPacket::new
    );

    @Override
    public void handle(LocalPlayer player) {
        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RocketContraptionEntity.handleQuickSyncPacket(this));
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.ROCKET_QUICK_SYNC;
    }

}