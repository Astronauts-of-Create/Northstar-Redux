package com.lightning.northstar.block.tech.oxygen_sealer;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.oxygen.OxygenTrackingSealer;
import com.lightning.northstar.world.sealer.SealingMode;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MovingOxygenSealer implements NorthstarOxygen.Provider {

    public final Contraption contraption;
    public final OxygenTrackingSealer sealer = new OxygenTrackingSealer(SealingMode.OXYGEN);
    public float pendingDrain;
    public float activeDrain;
    public float drain;
    public boolean active;
    public boolean showLeak;

    MovingOxygenSealer(Contraption contraption) {
        this.contraption = contraption;
    }

    void tick(MovementContext context) {
        sealer.processSeal(context.contraption.getContraptionWorld(), context.localPos, Direction.UP,
                NorthstarConfigs.server().oxygenSealerMaxContraptionSealed.get());

        if (sealer.hasLeak()) {
            if (showLeak)
                sealer.renderLeakPath(context.contraption.entity.level(), context.contraption.entity);
            active = false;
            return;
        }

        MountedFluidStorageWrapper fluids = context.contraption.getStorage().getFluids();
        Fluid oxygen = findOxygenIn(fluids);
        if (oxygen == null) {
            active = false;
            return;
        }

        pendingDrain -= fluids.drain(new FluidStack(oxygen, (int) pendingDrain), context.world.isClientSide ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE).getAmount();
        active = pendingDrain < 1;

        if (!active)
            return;

        drain = sealer.getPassiveDrain() + sealer.getActiveDrain() + sealer.calculateDynamicConsumption(context.contraption.getContraptionWorld()) + activeDrain;
        pendingDrain += drain;
        activeDrain = 0;
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

    @Override
    public void drainOxygen(float oxygen) {
        activeDrain += oxygen;
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
