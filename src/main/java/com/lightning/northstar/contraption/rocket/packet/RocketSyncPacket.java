package com.lightning.northstar.contraption.rocket.packet;

import com.lightning.northstar.contraption.rocket.LaunchStatus;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public class RocketSyncPacket extends SimplePacketBase {

    public final int entityId;
    public final int countdown;
    public final float velocity;
    public final LaunchStatus status;
    public final boolean thrustersEnabled;

    public RocketSyncPacket(int entityId, int countdown, float velocity, LaunchStatus status, boolean thrustersEnabled) {
        this.entityId = entityId;
        this.countdown = countdown;
        this.velocity = velocity;
        this.status = status;
        this.thrustersEnabled = thrustersEnabled;
    }

    public RocketSyncPacket(FriendlyByteBuf buffer) {
        this(buffer.readVarInt(), buffer.readVarInt(), buffer.readFloat(), buffer.readEnum(LaunchStatus.class), buffer.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(entityId);
        buffer.writeVarInt(countdown);
        buffer.writeFloat(velocity);
        buffer.writeEnum(status);
        buffer.writeBoolean(thrustersEnabled);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handle));
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private void handle() {
        if (Minecraft.getInstance().level.getEntity(entityId) instanceof RocketContraptionEntity rocket) {
            rocket.onSync(this);
        }
    }

}
