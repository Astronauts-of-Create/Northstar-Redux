package com.lightning.northstar.block.tech.rocket_station;

import com.lightning.northstar.accessor.NorthstarLevel;
import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraption.rocket.LaunchStatus;
import com.lightning.northstar.contraption.rocket.RocketContraption;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.lightning.northstar.contraption.rocket.RocketDestination;
import com.lightning.northstar.contraption.rocket.packet.RocketDestinationPacket;
import com.lightning.northstar.util.NorthstarCodecs;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.Nullable;

public record RocketStationEditPacket(
        BlockPos pos,
        int rocketId,
        boolean flag,
        @Nullable RocketDestination destination
) implements ServerboundPacketPayload {

    public static final StreamCodec<ByteBuf, RocketStationEditPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, RocketStationEditPacket::pos,
            ByteBufCodecs.VAR_INT, RocketStationEditPacket::rocketId,
            ByteBufCodecs.BOOL, RocketStationEditPacket::flag,
            NorthstarCodecs.nullableStream(RocketDestination.STREAM_CODEC), RocketStationEditPacket::destination,
            RocketStationEditPacket::new
    );

    @Override
    public void handle(ServerPlayer player) {
        if (rocketId >= 0) {
            handleEntity(player);
        } else {
            handleWorld(player);
        }
    }

    private void handleEntity(ServerPlayer player) {
        if (!(player.level().getEntity(rocketId) instanceof RocketContraptionEntity rocket) || rocket.getStatus() != LaunchStatus.WAITING) {
            return;
        }
        if (!pos.equals(BlockPos.ZERO)) {
            return; // only the main rocket station can be accessed
        }

        RocketContraption contraption = rocket.getContraption();
        MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor = contraption.getActorAt(BlockPos.ZERO);
        if (actor == null || !RocketStationMenu.validateDestination(NorthstarLevel.SERVER_TRACKER, RocketStationActor.get(actor.right).container.getItem(0), destination))
            return;

        contraption.destination = destination;
        if (destination != null) {
            actor.right.blockEntityData.put("Destination", destination.toTag());
        } else {
            actor.right.blockEntityData.remove("Destination");
        }

        if (flag && !rocket.isOutOfWorld()) {
            rocket.disassemble();
        } else {
            CatnipServices.NETWORK.sendToClientsTrackingEntity(rocket, new RocketDestinationPacket(rocket.getId(), destination));
        }
    }

    private void handleWorld(ServerPlayer player) {
        if (!(player.level().getBlockEntity(pos) instanceof RocketStationBlockEntity be)) {
            return;
        }
        if (!RocketStationMenu.validateDestination(NorthstarLevel.SERVER_TRACKER, be.container.getItem(0), destination)) {
            return;
        }

        be.destination = destination;
        be.sendData();
        be.setChanged();

        if (flag) {
            be.assemble();
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.UPDATE_ROCKET_STATION;
    }

}
