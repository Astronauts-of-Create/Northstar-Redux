package com.lightning.northstar.block.tech.rocket_controls;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class RocketControlsInputPacket implements ServerboundPacketPayload {

    public static final StreamCodec<ByteBuf, RocketControlsInputPacket> STREAM_CODEC = StreamCodec.composite(
            CatnipStreamCodecBuilders.list(ByteBufCodecs.VAR_INT), packet -> packet.activatedButtons,
            ByteBufCodecs.BOOL, packet -> packet.press,
            ByteBufCodecs.VAR_INT, packet -> packet.contraptionEntityId,
            BlockPos.STREAM_CODEC, packet -> packet.controlsPos,
            ByteBufCodecs.BOOL, packet -> packet.stopControlling,
            RocketControlsInputPacket::new
    );

    private List<Integer> activatedButtons;
    private boolean press;
    private int contraptionEntityId;
    private BlockPos controlsPos;
    private boolean stopControlling;

    public RocketControlsInputPacket(List<Integer> activatedButtons, boolean press, int contraptionEntityId,
                                     BlockPos controlsPos, boolean stopControlling) {
        this.contraptionEntityId = contraptionEntityId;
        this.activatedButtons = activatedButtons;
        this.press = press;
        this.contraptionEntityId = contraptionEntityId;
        this.controlsPos = controlsPos;
        this.stopControlling = stopControlling;
    }

    @Override
    public void handle(ServerPlayer player) {
        Level world = player.getCommandSenderWorld();
        UUID uniqueID = player.getUUID();

        if (player.isSpectator() && press)
            return;

        Entity entity = world.getEntity(contraptionEntityId);
        if (!(entity instanceof RocketContraptionEntity rce))
            return;
        if (stopControlling) {
            rce.stopControlling(controlsPos);
            rce.cancelLaunch();
            return;
        }

        if (rce.toGlobalVector(Vec3.atCenterOf(controlsPos), 0).closerThan(player.position(), 16)) {
            Northstar.LOGGER.debug("Key press Detected!");
            RocketControlsServerHandler.receivePressed(world, rce, controlsPos, uniqueID, activatedButtons, press);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.ROCKET_CONTROLS_INPUT;
    }

}