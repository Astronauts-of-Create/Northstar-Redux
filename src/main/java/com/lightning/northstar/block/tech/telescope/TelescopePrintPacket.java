package com.lightning.northstar.block.tech.telescope;

import com.lightning.northstar.content.NorthstarBlocks;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TelescopePrintPacket extends SimplePacketBase {

    public final BlockPos pos;
    public final ResourceLocation planetId;

    public TelescopePrintPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readResourceLocation());
    }

    public TelescopePrintPacket(BlockPos pos, ResourceLocation planetId) {
        this.pos = pos;
        this.planetId = planetId;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeResourceLocation(planetId);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.level().getBlockState(pos).is(NorthstarBlocks.TELESCOPE.get())) {
                TelescopeBlock.handlePrintRequest(player, pos, planetId);
            }
        });
        return true;
    }

}
