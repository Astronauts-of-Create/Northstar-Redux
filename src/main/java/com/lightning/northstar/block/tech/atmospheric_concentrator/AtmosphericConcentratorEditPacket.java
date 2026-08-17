package com.lightning.northstar.block.tech.atmospheric_concentrator;

import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.planet.data.AtmosphereFluid;
import com.simibubi.create.foundation.utility.AdventureUtil;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record AtmosphericConcentratorEditPacket(
        BlockPos pos,
        FluidStack fluid
) implements ServerboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, AtmosphericConcentratorEditPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, AtmosphericConcentratorEditPacket::pos,
            FluidStack.STREAM_CODEC, AtmosphericConcentratorEditPacket::fluid,
            AtmosphericConcentratorEditPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.UPDATE_ATMOSPHERIC_CONCENTRATOR;
    }

    @Override
    public void handle(ServerPlayer player) {
        if (player.isSpectator() ||
            AdventureUtil.isAdventure(player) ||
            !pos.closerThan(player.blockPosition(), 20) ||
            !(player.level().getBlockEntity(pos) instanceof AtmosphericConcentratorBlockEntity be)) {
            return;
        }

        AtmosphereFluid fluid = be.getLevel()
                .northstar$dimension()
                .atmosphere()
                .composition()
                .stream()
                .filter(f -> FluidStack.isSameFluidSameComponents(f.asFluidStack(1), this.fluid))
                .findFirst()
                .orElse(null);

        be.setCollectedFluid(fluid);
    }

}
