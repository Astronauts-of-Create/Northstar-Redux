package com.lightning.northstar.block.tech.telescope;

import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record TelescopePrintPacket(
        BlockPos pos,
        ResourceLocation planetId
) implements ServerboundPacketPayload {

    public static final StreamCodec<ByteBuf, TelescopePrintPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, TelescopePrintPacket::pos,
            ResourceLocation.STREAM_CODEC, TelescopePrintPacket::planetId,
            TelescopePrintPacket::new
    );

    @Override
    public void handle(ServerPlayer player) {
        if (player.level().getBlockState(pos).is(NorthstarBlocks.TELESCOPE.get())) {
            TelescopeBlock.handlePrintRequest(player, pos, planetId);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.TELESCOPE_PRINT;
    }

}
