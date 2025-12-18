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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.UUID;

public record EntityLockPacket(
        int contraptionEntityId,
        LockInfo info,
        UUID playerID
) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, EntityLockPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EntityLockPacket::contraptionEntityId,
            LockInfo.STREAM_CODEC, EntityLockPacket::info,
            UUIDUtil.STREAM_CODEC, EntityLockPacket::playerID,
            EntityLockPacket::new
    );

    @Override
    public void handle(LocalPlayer localPlayer) {
        Entity entity = Minecraft.getInstance().level.getEntity(contraptionEntityId);
        if (entity instanceof RocketContraptionEntity rce) {
            rce.entityLockMap.put(playerID, info);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.ENTITY_LOCK;
    }

    public record LockInfo(Vec3 offset, MutableInt ticks) {
        public static final int FOREVER = Integer.MAX_VALUE;
        public static final StreamCodec<ByteBuf, LockInfo> STREAM_CODEC = StreamCodec.composite(
                CatnipStreamCodecs.VEC3, LockInfo::offset,
                ByteBufCodecs.INT, info -> info.ticks().intValue(),
                LockInfo::new
        );

        public LockInfo(Vec3 offset, int ticks) {
            this(offset, new MutableInt(ticks));
        }
    }

}
