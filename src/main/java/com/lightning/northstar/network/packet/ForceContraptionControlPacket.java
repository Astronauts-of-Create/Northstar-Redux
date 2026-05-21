package com.lightning.northstar.network.packet;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class ForceContraptionControlPacket extends SimplePacketBase {

    public final int entityId;
    public final BlockPos controlPos;

    public ForceContraptionControlPacket(int entityId, BlockPos controlPos) {
        this.entityId = entityId;
        this.controlPos = controlPos;
    }

    public ForceContraptionControlPacket(FriendlyByteBuf buffer) {
        this(buffer.readVarInt(), buffer.readBlockPos());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(entityId);
        buffer.writeBlockPos(controlPos);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (Minecraft.getInstance().level.getEntity(entityId) instanceof AbstractContraptionEntity entity) {
                // Click twice to stop controlling and recontrol if needed
                if (entity.equals(ControlsHandler.getContraption()) && controlPos.equals(ControlsHandler.getControlsPos())) {
                    entity.handlePlayerInteraction(Minecraft.getInstance().player, controlPos, Direction.NORTH, InteractionHand.MAIN_HAND);
                }
                entity.handlePlayerInteraction(Minecraft.getInstance().player, controlPos, Direction.NORTH, InteractionHand.MAIN_HAND);
            }
        }));
        return false;
    }

}
