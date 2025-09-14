package com.lightning.northstar.block.tech.temperature_regulator;

import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraptions.ActorConfigPacket;
import com.lightning.northstar.world.NorthstarTemperature;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.simibubi.create.foundation.utility.AdventureUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.MutablePair;

public class TemperatureRegulatorEditPacket extends SimplePacketBase {

    private final int contraptionId;
    private final BlockPos pos;
    private final int temperature;
    private final boolean limit;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;

    public TemperatureRegulatorEditPacket(FriendlyByteBuf buffer) {
        contraptionId = buffer.readVarInt();
        pos = buffer.readBlockPos();
        temperature = Mth.clamp(buffer.readVarInt(), NorthstarTemperature.MINIMUM_TEMPERATURE, NorthstarTemperature.MAXIMUM_TEMPERATURE);
        limit = buffer.readBoolean();
        sizeX = clampSize(buffer.readVarInt());
        sizeY = clampSize(buffer.readVarInt());
        sizeZ = clampSize(buffer.readVarInt());
    }

    public TemperatureRegulatorEditPacket(int contraptionId, BlockPos pos, int temperature, boolean limit, int sizeX, int sizeY, int sizeZ) {
        this.contraptionId = contraptionId;
        this.pos = pos;
        this.temperature = temperature;
        this.limit = limit;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(contraptionId);
        buffer.writeBlockPos(pos);
        buffer.writeVarInt(temperature);
        buffer.writeBoolean(limit);
        buffer.writeVarInt(sizeX);
        buffer.writeVarInt(sizeY);
        buffer.writeVarInt(sizeZ);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || player.isSpectator() || AdventureUtil.isAdventure(player))
                return;
            Level world = player.level();
            if (world.getEntity(contraptionId) instanceof AbstractContraptionEntity entity) {
                handleContraption(player, entity);
            } else {
                handleWorld(player, world);
            }
        });
        return true;
    }

    private void handleContraption(ServerPlayer player, AbstractContraptionEntity entity) {
        MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor = entity.getContraption().getActorAt(pos);
        if (actor == null)
            return;
        if (!(actor.right.temporaryData instanceof MovingTemperatureRegulator regulator))
            return;
        BlockPos localPos = regulator.context.localPos;
        if (!entity.toGlobalVector(Vec3.atBottomCenterOf(localPos), 0).closerThan(player.position(), 20))
            return;

        regulator.regulator.temperature = temperature;
        regulator.regulator.setBounds(localPos, limit, sizeX, sizeY, sizeZ);


        CompoundTag nbt = actor.left.nbt() == null ? new CompoundTag() : actor.left.nbt().copy(); // needed copy?
        regulator.regulator.write(nbt);
        actor.left = new StructureTemplate.StructureBlockInfo(localPos, actor.left.state(), nbt);
        entity.setBlock(localPos, actor.left);

        NorthstarPackets.getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new ActorConfigPacket(entity.getId(), localPos, nbt));
    }

    private void handleWorld(ServerPlayer player, Level world) {
        if (!world.isLoaded(pos) || !pos.closerThan(player.blockPosition(), 20))
            return;

        if (world.getBlockEntity(pos) instanceof TemperatureRegulatorBlockEntity be) {
            be.regulator.temperature = temperature;
            be.regulator.setBounds(be.getBlockPos(), limit, sizeX, sizeY, sizeZ);
            be.sendData();
            be.setChanged();
        }
    }

    private static int clampSize(int size) {
        return Mth.clamp(size, 1, TemperatureRegulatorBlockEntity.MAX_LIMIT_SIZE);
    }

}
