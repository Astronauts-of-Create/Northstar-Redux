package com.lightning.northstar.block.tech.temperature_regulator;

import com.lightning.northstar.content.NorthstarPackets;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class TemperatureRegulatorEditPacket extends BlockEntityConfigurationPacket<TemperatureRegulatorBlockEntity> {

    public static final StreamCodec<ByteBuf, TemperatureRegulatorEditPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.pos,
            BlockPos.STREAM_CODEC, packet -> packet.offset,
            BlockPos.STREAM_CODEC, packet -> packet.size,
            ByteBufCodecs.INT, packet -> packet.temp,
            ByteBufCodecs.BOOL, packet -> packet.envFill,
            TemperatureRegulatorEditPacket::new
    );

    private BlockPos offset;
    private BlockPos size;
    private int temp;
    private boolean envFill;

    public TemperatureRegulatorEditPacket(BlockPos pos, BlockPos offset, BlockPos size, int temp, boolean envFill) {
        super(pos);
        this.offset = offset;
        this.size = size;
        this.temp = temp;
        this.envFill = envFill;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.UPDATE_TEMPERATURE_REGULATOR;
    }

    @Override
    protected void applySettings(ServerPlayer player, TemperatureRegulatorBlockEntity entity) {
        entity.changeTemp(temp);
        entity.changeSize(size.getX(), size.getY(), size.getZ(), offset.getX(), offset.getY(), offset.getZ(), envFill);
    }

}