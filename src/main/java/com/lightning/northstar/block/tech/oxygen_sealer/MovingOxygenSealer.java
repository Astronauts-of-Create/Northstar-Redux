package com.lightning.northstar.block.tech.oxygen_sealer;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.world.NorthstarOxygen;
import com.lightning.northstar.world.SealingProvider;
import com.lightning.northstar.world.sealer.ProgressiveBlockSealer;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class MovingOxygenSealer implements SealingProvider {

    public final Contraption contraption;
    public final ProgressiveBlockSealer sealer = new ProgressiveBlockSealer();
    public float pendingDrain;
    public boolean active;

    MovingOxygenSealer(Contraption contraption) {
        this.contraption = contraption;
    }

    void tick(MovementContext context) {
        if (sealer.isSealInProgress()) {
            sealer.updateSeal(context.contraption.getContraptionWorld(), NorthstarConfigs.server().oxygenSealerMaxContraptionSealed.get());
        } else {
            sealer.beginSeal(context.contraption.getContraptionWorld(), context.localPos, Direction.UP);
        }

        if (sealer.hasLeak()) {
            return;
        }

        active = false;

        MountedFluidStorageWrapper fluids = context.contraption.getStorage().getFluids();
        Fluid oxygen = findOxygenIn(fluids);
        if (oxygen == null)
            return;
        pendingDrain -= fluids.drain(new FluidStack(oxygen, (int) pendingDrain), context.world.isClientSide ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE).getAmount();
        if (pendingDrain >= 1)
            return; // don't process anything

        pendingDrain += NorthstarConfigs.server().oxygenSealerOxygenPerBlockPerTick.getF() * sealer.getSealedBlockCount();
        active = true;
    }

    @Override
    public boolean isSealed(Vec3 pos) {
        if (!active)
            return false;
        Vec3 local = contraption.entity.toLocalVector(pos, 0);
        return sealer.getSealedBlocks().contains(BlockPos.asLong(Mth.floor(local.x), Mth.floor(local.y), Mth.floor(local.z)));
    }

    @Override
    public boolean isSealed(Vec3i pos) {
        return isSealed(Vec3.atLowerCornerOf(pos));
    }

    private static Fluid findOxygenIn(MountedFluidStorageWrapper fluids) {
        for (int i = 0; i < fluids.getTanks(); i++) {
            Fluid fluid = fluids.getFluidInTank(i).getFluid();
            if (NorthstarOxygen.isOxygen(fluid)) {
                return fluid;
            }
        }
        return null;
    }

}
