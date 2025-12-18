package com.lightning.northstar.contraption.rocket.packet;

import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraption.rocket.RocketHandler;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record RocketControlPacket(
        int rocketContraptionId,
        UUID playerId,
        BlockPos localControlPos
) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, RocketControlPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RocketControlPacket::rocketContraptionId,
            UUIDUtil.STREAM_CODEC, RocketControlPacket::playerId,
            BlockPos.STREAM_CODEC, RocketControlPacket::localControlPos,
            RocketControlPacket::new
    );

    @Override
    public void handle(LocalPlayer player) {
        RocketHandler.CONTROL_QUEUE.put(Pair.of(playerId, localControlPos), rocketContraptionId);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.ROCKET_CONTROL;
    }

}
