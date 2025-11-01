package com.lightning.northstar.block.tech.temperature_regulator;

import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraption.ActorConfigPacket;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.utility.AdventureUtil;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.MutablePair;

public class TemperatureRegulatorEditPacket implements ServerboundPacketPayload {

    public static final StreamCodec<ByteBuf, TemperatureRegulatorEditPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public TemperatureRegulatorEditPacket decode(ByteBuf object) {
            return new TemperatureRegulatorEditPacket(
                    ByteBufCodecs.INT.decode(object),
                    BlockPos.STREAM_CODEC.decode(object),
                    ByteBufCodecs.INT.decode(object),
                    ByteBufCodecs.BOOL.decode(object),
                    clampSize(ByteBufCodecs.INT.decode(object)),
                    clampSize(ByteBufCodecs.INT.decode(object)),
                    clampSize(ByteBufCodecs.INT.decode(object))
            );
        }

        @Override
        public void encode(ByteBuf object, TemperatureRegulatorEditPacket packet) {
            ByteBufCodecs.INT.encode(object, packet.contraptionId);
            BlockPos.STREAM_CODEC.encode(object, packet.pos);
            ByteBufCodecs.INT.encode(object, packet.temperature);
            ByteBufCodecs.BOOL.encode(object, packet.limit);
            ByteBufCodecs.INT.encode(object, packet.sizeX);
            ByteBufCodecs.INT.encode(object, packet.sizeY);
            ByteBufCodecs.INT.encode(object, packet.sizeZ);
        }
    };

    private final int contraptionId;
    private final BlockPos pos;
    private final int temperature;
    private final boolean limit;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;

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
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.UPDATE_TEMPERATURE_REGULATOR;
    }

    @Override
    public void handle(ServerPlayer player) {
        if (player == null || player.isSpectator() || AdventureUtil.isAdventure(player))
            return;
        Level world = player.level();
        if (world.getEntity(contraptionId) instanceof AbstractContraptionEntity entity) {
            handleContraption(player, entity);
        } else {
            handleWorld(player, world);
        }
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

        CatnipServices.NETWORK.sendToClientsTrackingEntity(entity, new ActorConfigPacket(entity.getId(), localPos, nbt));
    }

    private void handleWorld(ServerPlayer player, Level world) {
        if (!world.isLoaded(pos) || !pos.closerThan(player.blockPosition(), 20))
            return;

        if (world.getBlockEntity(pos) instanceof TemperatureRegulatorBlockEntity be) {
            float oldTemperature = be.regulator.temperature;
            be.regulator.temperature = temperature;
            be.regulator.setBounds(be.getBlockPos(), limit, sizeX, sizeY, sizeZ);
            if (!Mth.equal(oldTemperature, temperature))
                be.onTemperatureChanged();
            be.sendData();
            be.setChanged();
        }
    }

    private static int clampSize(int size) {
        return Mth.clamp(size, 1, TemperatureRegulatorBlockEntity.MAX_LIMIT_SIZE);
    }

}