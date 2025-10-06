package com.lightning.northstar.contraption.rocket.packet;

import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class SoftReleasePacket extends SimplePacketBase {
    public int contraptionEntityId;
    public SoftReleaseInfo info;
    public UUID playerID;

    public record SoftReleaseInfo(Vec3 offset, AtomicInteger ticks) {
        public Vec3 offset() {
            return this.offset;
        }

        public AtomicInteger ticks() {
            return this.ticks;
        }
    }

    public SoftReleasePacket(UUID playerId, int contraptionId, SoftReleaseInfo info) {
        this.info = info;
        this.playerID = playerId;
        this.contraptionEntityId = contraptionId;
    }

    public SoftReleasePacket(FriendlyByteBuf buffer) {
        this.playerID = buffer.readUUID();
        this.contraptionEntityId = buffer.readInt();

        Vec3 offset = new Vec3(buffer.readVector3f());
        AtomicInteger ticks = new AtomicInteger(buffer.readInt());
        info = new SoftReleaseInfo(offset, ticks);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(playerID);
        buffer.writeInt(contraptionEntityId);

        buffer.writeVector3f(info.offset.toVector3f());
        buffer.writeInt(info.ticks.get());
    }

    @Override
    public boolean handle(Context context) {
        Entity entity = Minecraft.getInstance().level.getEntity(contraptionEntityId);
        if (entity instanceof RocketContraptionEntity rce) {
            rce.softReleaseMap.put(playerID, info);
        }

        return true;
    }

}