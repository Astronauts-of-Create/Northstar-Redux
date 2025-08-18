package com.lightning.northstar.block.tech.telescope;

import com.lightning.northstar.content.NorthstarPackets;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TelescopePrintPacket extends BlockEntityConfigurationPacket<TelescopeBlockEntity> {

    public static final StreamCodec<ByteBuf, TelescopePrintPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.pos,
            ByteBufCodecs.STRING_UTF8, packet -> packet.planetName,
            TelescopePrintPacket::new
    );

    private String planetName;

    public TelescopePrintPacket(BlockPos pos, String planetName) {
        super(pos);
        this.planetName = planetName;
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
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.TELESCOPE_PRINT;
    }

}
