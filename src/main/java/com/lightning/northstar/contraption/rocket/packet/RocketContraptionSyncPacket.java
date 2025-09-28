package com.lightning.northstar.contraption.rocket.packet;

import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
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
    public int launchTime;
    public boolean launched;
    public boolean landing;
    public boolean blasting;
    public boolean slowing;
    public boolean activeLaunch;

    public RocketContraptionSyncPacket(int contraptionEntityId, Vec3 pos, float lift_vel, int launchTime,
                                       boolean launched, boolean landing, boolean blasting, boolean slowing,
                                       boolean activeLaunch) {
        this.contraptionEntityId = contraptionEntityId;
        this.pos = pos;
        this.lift_vel = lift_vel;
        this.launchTime = launchTime;
        this.launched = launched;
        this.landing = landing;
        this.blasting = blasting;
        this.slowing = slowing;
        this.activeLaunch = activeLaunch;
    }

    public RocketContraptionSyncPacket(FriendlyByteBuf buffer) {
        pos = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        lift_vel = buffer.readFloat();
        contraptionEntityId = buffer.readInt();
        launchTime = buffer.readInt();
        launched = buffer.readBoolean();
        landing = buffer.readBoolean();
        blasting = buffer.readBoolean();
        slowing = buffer.readBoolean();
        activeLaunch = buffer.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeDouble(pos.x);
        buffer.writeDouble(pos.y);
        buffer.writeDouble(pos.z);
        buffer.writeFloat(lift_vel);
        buffer.writeInt(contraptionEntityId);
        buffer.writeInt(launchTime);
        buffer.writeBoolean(launched);
        buffer.writeBoolean(landing);
        buffer.writeBoolean(blasting);
        buffer.writeBoolean(slowing);
        buffer.writeBoolean(activeLaunch);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> RocketContraptionEntity.handleSyncPacket(this)));
        return true;
    }

}
