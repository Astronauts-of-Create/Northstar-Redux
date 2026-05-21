package com.lightning.northstar.contraption.rocket.packet;

import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;

public class RocketSeatsPacket extends SimplePacketBase {

    public final int entityId;
    public final Map<UUID, Vec3> offsets;

    public RocketSeatsPacket(int entityId, Map<UUID, Vec3> offsets) {
        this.entityId = entityId;
        this.offsets = offsets;
    }

    public RocketSeatsPacket(FriendlyByteBuf buffer) {
        this(buffer.readVarInt(), buffer.readMap(FriendlyByteBuf::readUUID, buf -> new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(entityId);
        buffer.writeMap(offsets, FriendlyByteBuf::writeUUID, (buf, vec) -> buf.writeDouble(vec.x()).writeDouble(vec.y()).writeDouble(vec.z()));
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handle));
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private void handle() {
        if (Minecraft.getInstance().level.getEntity(entityId) instanceof RocketContraptionEntity rocket) {
            rocket.getVirtualSeats().clear();
            rocket.getVirtualSeats().putAll(offsets);
        }
    }

}
