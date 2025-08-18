package com.lightning.northstar.contraptions;

import com.lightning.northstar.content.NorthstarPackets;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;

public class RocketContraptionSyncPacket implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, RocketContraptionSyncPacket> STREAM_CODEC = null;/*StreamCodec.composite(
            ByteBufCodecs.VAR_INT, packet -> packet.contraptionEntityId,
            Vec3.STREAM_CODEC, packet -> packet.pos,
            ByteBufCodecs.FLOAT, packet -> packet.lift_vel,
            ByteBufCodecs.VAR_INT, packet -> packet.launchtime,
            ByteBufCodecs.BOOL, packet -> packet.stopControlling,
            RocketContraptionSyncPacket::new
    );*/

    public int contraptionEntityId;
    public Vec3 pos;
    public float lift_vel;
    public int launchTime;
    public boolean launched;
    public boolean landing;
    public boolean blasting;
    public boolean slowing;
    public boolean activeLaunch;

    public RocketContraptionSyncPacket(int contraptionEntityId, Vec3 pos, float lift_vel, int launchTime, boolean launched, boolean landing, boolean blasting, boolean slowing, boolean activeLaunch) {
        this.contraptionEntityId = contraptionEntityId;
        this.pos = pos;
        this.lift_vel = lift_vel;
        this.launchTime = launchTime;
        this.launched = launched;
        this.landing = landing;
        this.blasting = blasting;
        this.slowing = slowing;
        this.activeLaunch = activeLaunch;
    }

    @Override
    public void handle(LocalPlayer player) {
        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RocketContraptionEntity.handleSyncPacket(this));
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.ROCKET_SYNC_PACKET;
    }

}
