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
    UUID playerID;
    Vec3 offset;

    public SoftReleasePacket(UUID player, int rce2, Vec3 offset) {
        this.playerID = player;
        this.contraptionEntityId = rce2;
        this.offset = offset;
    }

    public SoftReleasePacket(FriendlyByteBuf buffer) {
        this.playerID = buffer.readUUID();
        this.offset = new Vec3(buffer.readVector3f());
        this.contraptionEntityId = buffer.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(playerID);
        buffer.writeVector3f(offset.toVector3f());
        buffer.writeInt(contraptionEntityId);
    }

    @Override
    public boolean handle(Context context) {
        Entity entity = Minecraft.getInstance().level.getEntity(contraptionEntityId);
        if (entity instanceof RocketContraptionEntity rce) {
            rce.softReleaseMap.put(playerID,
                    new RocketContraptionEntity.SoftReleaseInfo(offset, new AtomicInteger(0)));
        }

        return true;
    }

}