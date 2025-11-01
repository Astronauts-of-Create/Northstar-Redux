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

public class EntityLockPacket extends SimplePacketBase {
    public int contraptionEntityId;
    public LockInfo info;
    public UUID playerID;

    public record LockInfo(Vec3 offset, AtomicInteger ticks) {
        public static final int FOREVER = Integer.MAX_VALUE;

        public Vec3 offset() {
            return this.offset;
        }

        public AtomicInteger ticks() {
            return this.ticks;
        }
    }

    public EntityLockPacket(UUID playerId, int contraptionId, LockInfo info) {
        this.info = info;
        this.playerID = playerId;
        this.contraptionEntityId = contraptionId;
    }

    public EntityLockPacket(FriendlyByteBuf buffer) {
        this.playerID = buffer.readUUID();
        this.contraptionEntityId = buffer.readInt();

        Vec3 offset = new Vec3(buffer.readVector3f());
        AtomicInteger ticks = new AtomicInteger(buffer.readInt());
        info = new LockInfo(offset, ticks);
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
            rce.entityLockMap.put(playerID, info);
        }
        return true;
    }

}