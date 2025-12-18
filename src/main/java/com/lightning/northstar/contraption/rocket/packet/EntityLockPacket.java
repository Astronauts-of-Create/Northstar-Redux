package com.lightning.northstar.contraption.rocket.packet;

import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.UUID;

public class EntityLockPacket extends SimplePacketBase {

    public int contraptionEntityId;
    public LockInfo info;
    public UUID playerID;

    public EntityLockPacket(UUID playerId, int contraptionId, LockInfo info) {
        this.info = info;
        this.playerID = playerId;
        this.contraptionEntityId = contraptionId;
    }

    public EntityLockPacket(FriendlyByteBuf buffer) {
        this.playerID = buffer.readUUID();
        this.contraptionEntityId = buffer.readInt();
        this.info = new LockInfo(new Vec3(buffer.readVector3f()), new MutableInt(buffer.readInt()));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(playerID);
        buffer.writeInt(contraptionEntityId);

        buffer.writeVector3f(info.offset.toVector3f());
        buffer.writeInt(info.ticks.intValue());
    }

    @Override
    public boolean handle(Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handleClient);
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient() {
        Entity entity = Minecraft.getInstance().level.getEntity(contraptionEntityId);
        if (entity instanceof RocketContraptionEntity rce) {
            rce.entityLockMap.put(playerID, info);
        }
    }

    public record LockInfo(Vec3 offset, MutableInt ticks) {
        public static final int FOREVER = Integer.MAX_VALUE;
    }

}
