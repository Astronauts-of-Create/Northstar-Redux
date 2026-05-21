package com.lightning.northstar.contraption.rocket.packet;

import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.lightning.northstar.contraption.rocket.RocketDestination;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;

public class RocketDestinationPacket extends SimplePacketBase {

    public final int entityId;
    public final RocketDestination destination;

    public RocketDestinationPacket(int entityId, RocketDestination destination) {
        this.entityId = entityId;
        this.destination = destination;
    }

    public RocketDestinationPacket(FriendlyByteBuf buffer) {
        this(buffer.readVarInt(), buffer.readOptional(RocketDestination::new).orElse(null));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(entityId);
        buffer.writeOptional(Optional.ofNullable(destination), (buf, dest) -> dest.writeBuffer(buf));
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handle));
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private void handle() {
        if (Minecraft.getInstance().level.getEntity(entityId) instanceof RocketContraptionEntity rocket) {
            rocket.getContraption().destination = destination;
        }
    }

}
