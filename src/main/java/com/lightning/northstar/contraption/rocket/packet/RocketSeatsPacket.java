package com.lightning.northstar.contraption.rocket.packet;

import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record RocketSeatsPacket(
        int entityId,
        Map<UUID, Vec3> offsets
) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, RocketSeatsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RocketSeatsPacket::entityId,
            ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, CatnipStreamCodecs.VEC3), RocketSeatsPacket::offsets,
            RocketSeatsPacket::new
    );

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(LocalPlayer player) {
        if (Minecraft.getInstance().level.getEntity(entityId) instanceof RocketContraptionEntity rocket) {
            rocket.getVirtualSeats().clear();
            rocket.getVirtualSeats().putAll(offsets);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.ROCKET_SEATS;
    }

}
