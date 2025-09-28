package com.lightning.northstar.contraption.rocket.packet;

import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public class RocketContraptionQuickSyncPacket extends SimplePacketBase {
    public int contraptionEntityId;
    public boolean slowing;

    public RocketContraptionQuickSyncPacket(boolean vActiveLaunch, int id) {
        slowing = vActiveLaunch;
        contraptionEntityId = id;
    }

    public RocketContraptionQuickSyncPacket(FriendlyByteBuf buffer) {
        slowing = buffer.readBoolean();
        contraptionEntityId = buffer.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(slowing);
        buffer.writeInt(contraptionEntityId);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> RocketContraptionEntity.handleQuickSyncPacket(this)));
        return true;
    }

}