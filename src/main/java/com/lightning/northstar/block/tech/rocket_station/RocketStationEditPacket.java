package com.lightning.northstar.block.tech.rocket_station;

import com.lightning.northstar.content.NorthstarPackets;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RocketStationEditPacket extends BlockEntityConfigurationPacket<RocketStationBlockEntity> {

    public static final StreamCodec<ByteBuf, RocketStationEditPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.pos,
            ByteBufCodecs.BOOL, packet -> packet.tryAssemble,
            RocketStationEditPacket::new
    );

    private boolean tryAssemble;

    public RocketStationEditPacket(BlockPos pos, Boolean tryAssemble) {
        super(pos);
        this.tryAssemble = tryAssemble;
    }

    public static RocketStationEditPacket tryAssemble(BlockPos pos) {
        return new RocketStationEditPacket(pos, true);
    }

    @Override
    protected void applySettings(ServerPlayer player, RocketStationBlockEntity be) {
        Level level = be.getLevel();
        BlockPos blockPos = be.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);

        if (!(blockState.getBlock() instanceof RocketStationBlock))
            return;

        if (tryAssemble)
            be.queueAssembly(player);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.UPDATE_ROCKET_STATION;
    }

}
