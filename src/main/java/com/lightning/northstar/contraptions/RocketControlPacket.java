package com.lightning.northstar.contraptions;

import com.lightning.northstar.content.NorthstarPackets;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public class RocketControlPacket implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, RocketControlPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, packet -> packet.rce,
            UUIDUtil.STREAM_CODEC, packet -> packet.playerID,
            BlockPos.STREAM_CODEC, packet -> packet.localControlsPos,
            RocketControlPacket::new
    );

    public int rce;
    public UUID playerID;
    public BlockPos localControlsPos;

    public RocketControlPacket(int rce, UUID playerID, BlockPos localControlsPos) {
        this.rce = rce;
        this.playerID = playerID;
        this.localControlsPos = localControlsPos;
    }

    @Override
    public void handle(LocalPlayer player) {
        RocketHandler.CONTROL_QUEUE.put(Pair.of(playerID, localControlsPos), rce);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.ROCKET_CONTROL_PACKET;
    }

}
