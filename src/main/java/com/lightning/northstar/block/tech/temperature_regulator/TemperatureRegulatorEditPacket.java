package com.lightning.northstar.block.tech.temperature_regulator;

import com.lightning.northstar.world.NorthstarTemperature;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;

public class TemperatureRegulatorEditPacket extends BlockEntityConfigurationPacket<TemperatureRegulatorBlockEntity> {

    private int temperature;
    private boolean limit;
    private int sizeX;
    private int sizeY;
    private int sizeZ;

    public TemperatureRegulatorEditPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    public TemperatureRegulatorEditPacket(BlockPos pos, int temperature, boolean limit, int sizeX, int sizeY, int sizeZ) {
        super(pos);
        this.temperature = temperature;
        this.limit = limit;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeVarInt(temperature);
        buffer.writeBoolean(limit);
        buffer.writeVarInt(sizeX);
        buffer.writeVarInt(sizeY);
        buffer.writeVarInt(sizeZ);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        temperature = buffer.readVarInt();
        limit = buffer.readBoolean();
        sizeX = buffer.readVarInt();
        sizeY = buffer.readVarInt();
        sizeZ = buffer.readVarInt();
    }

    @Override
    protected void applySettings(TemperatureRegulatorBlockEntity be) {
        be.targetTemperature = Mth.clamp(temperature, NorthstarTemperature.MINIMUM_TEMPERATURE, NorthstarTemperature.MAXIMUM_TEMPERATURE);
        be.setBounds(limit, clampSize(sizeX), clampSize(sizeY), clampSize(sizeZ));
    }

    private static int clampSize(int size) {
        return Mth.clamp(size, 1, TemperatureRegulatorBlockEntity.MAX_LIMIT_SIZE);
    }

}
