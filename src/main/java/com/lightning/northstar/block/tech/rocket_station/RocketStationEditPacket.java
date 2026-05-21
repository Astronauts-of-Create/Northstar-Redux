package com.lightning.northstar.block.tech.rocket_station;

import com.lightning.northstar.accessor.NorthstarLevel;
import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraption.rocket.LaunchStatus;
import com.lightning.northstar.contraption.rocket.RocketContraption;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.lightning.northstar.contraption.rocket.RocketDestination;
import com.lightning.northstar.contraption.rocket.packet.RocketDestinationPacket;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Optional;

public class RocketStationEditPacket extends SimplePacketBase {

    public final BlockPos pos;
    public final int rocketId;
    public final boolean flag;
    public final RocketDestination destination;

    public RocketStationEditPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readBoolean(), buffer.readOptional(RocketDestination::new).orElse(null));
    }

    public RocketStationEditPacket(BlockPos pos, int rocketId, boolean flag, RocketDestination destination) {
        this.pos = pos;
        this.rocketId = rocketId;
        this.flag = flag;
        this.destination = destination;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(rocketId);
        buffer.writeBoolean(flag);
        buffer.writeOptional(Optional.ofNullable(destination), (buf, value) -> value.writeBuffer(buf));
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (rocketId >= 0) {
                handleEntity(player);
            } else {
                handleWorld(player);
            }
        });
        return true;
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
            NorthstarPackets.getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> rocket), new RocketDestinationPacket(rocket.getId(), destination));
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

}
