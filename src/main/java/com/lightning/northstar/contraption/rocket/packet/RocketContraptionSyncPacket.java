package com.lightning.northstar.contraption.rocket.packet;

import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;

public record RocketContraptionSyncPacket(
        int contraptionEntityId,
        Vec3 pos,
        float lift_vel,
        int launchTime,
        boolean launched,
        boolean landing,
        boolean blasting,
        boolean slowing,
        boolean activeLaunch,
        boolean isInFlight
) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, RocketContraptionSyncPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public RocketContraptionSyncPacket decode(ByteBuf buf) {
            return new RocketContraptionSyncPacket(
                    ByteBufCodecs.INT.decode(buf),
                    CatnipStreamCodecs.VEC3.decode(buf),
                    ByteBufCodecs.FLOAT.decode(buf),
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf)
            );
        }

        @Override
        public void encode(ByteBuf buf, RocketContraptionSyncPacket packet) {
            ByteBufCodecs.INT.encode(buf, packet.contraptionEntityId);
            CatnipStreamCodecs.VEC3.encode(buf, packet.pos);
            ByteBufCodecs.FLOAT.encode(buf, packet.lift_vel);
            ByteBufCodecs.INT.encode(buf, packet.launchTime);
            ByteBufCodecs.BOOL.encode(buf, packet.launched);
            ByteBufCodecs.BOOL.encode(buf, packet.landing);
            ByteBufCodecs.BOOL.encode(buf, packet.blasting);
            ByteBufCodecs.BOOL.encode(buf, packet.slowing);
            ByteBufCodecs.BOOL.encode(buf, packet.activeLaunch);
            ByteBufCodecs.BOOL.encode(buf, packet.isInFlight);
        }
    };

    @Override
    public void handle(LocalPlayer player) {
        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RocketContraptionEntity.handleSyncPacket(this));
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.ROCKET_SYNC;
    }

}
