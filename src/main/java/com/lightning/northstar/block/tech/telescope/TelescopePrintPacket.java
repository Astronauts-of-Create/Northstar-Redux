package com.lightning.northstar.block.tech.telescope;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TelescopePrintPacket extends BlockEntityConfigurationPacket<TelescopeBlockEntity> {

    private String planetName;


    public TelescopePrintPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    public TelescopePrintPacket(BlockPos pos) {
        super(pos);
    }

    public static TelescopePrintPacket print(BlockPos pos, String strin) {
        TelescopePrintPacket packet = new TelescopePrintPacket(pos);
        packet.planetName = strin;
        return packet;
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeUtf(planetName);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        planetName = buffer.readUtf();
    }

    @Override
    protected void applySettings(ServerPlayer player, TelescopeBlockEntity be) {
        Level level = be.getLevel();
        BlockPos blockPos = be.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);

        if (!(blockState.getBlock() instanceof TelescopeBlock))
            return;

        be.print(planetName, player);
    }

    @Override
    protected void applySettings(TelescopeBlockEntity be) {
    }

}