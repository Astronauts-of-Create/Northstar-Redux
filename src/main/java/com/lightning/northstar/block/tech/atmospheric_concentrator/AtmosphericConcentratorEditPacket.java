package com.lightning.northstar.block.tech.atmospheric_concentrator;

import com.lightning.northstar.planet.data.AtmosphereFluid;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.simibubi.create.foundation.utility.AdventureUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AtmosphericConcentratorEditPacket extends SimplePacketBase {

    private final BlockPos pos;
    private final FluidStack fluid;

    public AtmosphericConcentratorEditPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.fluid = buffer.readFluidStack();
    }

    public AtmosphericConcentratorEditPacket(BlockPos pos, FluidStack fluid) {
        this.pos = pos;
        this.fluid = fluid;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeFluidStack(fluid);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null ||
                player.isSpectator() ||
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
                    .filter(f -> f.asFluidStack(1).isFluidEqual(this.fluid))
                    .findFirst()
                    .orElse(null);

            be.setCollectedFluid(fluid);
        });
        return true;
    }

}
