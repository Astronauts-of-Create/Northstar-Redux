package com.lightning.northstar.network.packet;

import com.lightning.northstar.content.NorthstarPackets;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;

public record ForceContraptionControlPacket(
        int entityId,
        BlockPos controlPos
) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, ForceContraptionControlPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ForceContraptionControlPacket::entityId,
            BlockPos.STREAM_CODEC, ForceContraptionControlPacket::controlPos,
            ForceContraptionControlPacket::new
    );

    @Override
    public void handle(LocalPlayer player) {
        if (Minecraft.getInstance().level.getEntity(entityId) instanceof AbstractContraptionEntity entity) {
            // Click twice to stop controlling and recontrol if needed
            if (entity.equals(ControlsHandler.getContraption()) && controlPos.equals(ControlsHandler.getControlsPos())) {
                entity.handlePlayerInteraction(Minecraft.getInstance().player, controlPos, Direction.NORTH, InteractionHand.MAIN_HAND);
            }
            entity.handlePlayerInteraction(Minecraft.getInstance().player, controlPos, Direction.NORTH, InteractionHand.MAIN_HAND);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.FORCE_CONTRAPTION_CONTROL;
    }

}
