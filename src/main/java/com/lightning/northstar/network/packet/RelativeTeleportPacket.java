package com.lightning.northstar.network.packet;

import com.lightning.northstar.content.NorthstarPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record RelativeTeleportPacket(int entityId, Vec3 offset) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, RelativeTeleportPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, p -> p.entityId,
            CatnipStreamCodecs.VEC3, p -> p.offset,
            RelativeTeleportPacket::new
    );

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(LocalPlayer localPlayer) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity other = minecraft.level.getEntity(entityId);
        if (other != null) {
            minecraft.player.setPos(other.position().subtract(offset));
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.RELATIVE_TELEPORT;
    }

}
