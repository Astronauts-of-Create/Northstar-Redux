package com.lightning.northstar.contraptions.packets;

import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraptions.RocketContraptionEntity;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;

public class RocketContraptionQuickSyncPacket implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, RocketContraptionQuickSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, packet -> packet.contraptionEntityId,
            ByteBufCodecs.BOOL, packet -> packet.slowing,
            RocketContraptionQuickSyncPacket::new
    );

    public int contraptionEntityId;
    public boolean slowing;

    public RocketContraptionQuickSyncPacket(int contraptionEntityId, boolean slowing) {
        this.contraptionEntityId = contraptionEntityId;
        this.slowing = slowing;
    }

    @Override
    public void handle(LocalPlayer player) {
        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RocketContraptionEntity.handleQuickSyncPacket(this));
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.ROCKET_QUICK_SYNC;
    }

}