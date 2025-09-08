package com.lightning.northstar.contraptions.packets;

import com.lightning.northstar.contraptions.RocketContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public class RocketContraptionSyncPacket extends SimplePacketBase {
    public int contraptionEntityId;
    public Vec3 pos;
    public float lift_vel;
    public int launchtime;
    public boolean launched;
    public boolean landing;
    public boolean blasting;
    public boolean slowing;
    public boolean activeLaunch;
    public byte dissasemblyTicks;

    public RocketContraptionSyncPacket(Vec3 syncedPos, float lift_vel2, int id,
                                       int vLaunchtime, boolean vLaunched,
                                       boolean vLanding, boolean vBlasting,
                                       boolean vSlowing, boolean vActiveLaunch,
                                       byte vDissasemblyTicks) {
        pos = syncedPos;
        lift_vel = lift_vel2;
        contraptionEntityId = id;
        launchtime = vLaunchtime;
        launched = vLaunched;
        landing = vLanding;
        blasting = vBlasting;
        slowing = vSlowing;
        activeLaunch = vActiveLaunch;
        dissasemblyTicks = vDissasemblyTicks;
    }

    public RocketContraptionSyncPacket(FriendlyByteBuf buffer) {
        pos = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        lift_vel = buffer.readFloat();
        contraptionEntityId = buffer.readInt();
        launchtime = buffer.readInt();
        launched = buffer.readBoolean();
        landing = buffer.readBoolean();
        blasting = buffer.readBoolean();
        slowing = buffer.readBoolean();
        activeLaunch = buffer.readBoolean();
        dissasemblyTicks = buffer.readByte();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeDouble(pos.x);
        buffer.writeDouble(pos.y);
        buffer.writeDouble(pos.z);
        buffer.writeFloat(lift_vel);
        buffer.writeInt(contraptionEntityId);
        buffer.writeInt(launchtime);
        buffer.writeBoolean(launched);
        buffer.writeBoolean(landing);
        buffer.writeBoolean(blasting);
        buffer.writeBoolean(slowing);
        buffer.writeBoolean(activeLaunch);
        buffer.writeByte(dissasemblyTicks);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> RocketContraptionEntity.handleSyncPacket(this)));
        return true;
    }

}