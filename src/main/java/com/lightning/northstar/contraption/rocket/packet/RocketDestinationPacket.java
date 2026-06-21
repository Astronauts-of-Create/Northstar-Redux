package com.lightning.northstar.contraption.rocket.packet;

import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.lightning.northstar.contraption.rocket.RocketDestination;
import com.lightning.northstar.util.NorthstarCodecs;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record RocketDestinationPacket(
        int entityId,
        @Nullable RocketDestination destination
) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, RocketDestinationPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RocketDestinationPacket::entityId,
            NorthstarCodecs.nullableStream(RocketDestination.STREAM_CODEC), RocketDestinationPacket::destination,
            RocketDestinationPacket::new
    );

    @Override
    public void handle(LocalPlayer player) {
        if (Minecraft.getInstance().level.getEntity(entityId) instanceof RocketContraptionEntity rocket) {
            rocket.getContraption().destination = destination;
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.ROCKET_DESTINATION;
    }

}
