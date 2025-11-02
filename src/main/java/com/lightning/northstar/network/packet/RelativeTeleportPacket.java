package com.lightning.northstar.network.packet;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class RelativeTeleportPacket extends SimplePacketBase {

    public final int entityId;
    public final Vec3 offset;

    public RelativeTeleportPacket(int entityId, Vec3 offset) {
        this.entityId = entityId;
        this.offset = offset;
    }

    public RelativeTeleportPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.offset = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeDouble(offset.x);
        buf.writeDouble(offset.y);
        buf.writeDouble(offset.z);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handle));
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    private void handle() {
        Minecraft minecraft = Minecraft.getInstance();
        Entity other = minecraft.level.getEntity(entityId);
        if (other != null) {
            minecraft.player.setPos(other.position().subtract(offset));
        }
    }

}
