package com.lightning.northstar.block.tech.oxygen_concentrator;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.world.dimension.NorthstarDimensions;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.List;

public class OxygenConcentratorBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {

    public int airLevel;
    public int airTimer;

    public SmartFluidTankBehaviour tank;

    public OxygenConcentratorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, NorthstarBlockEntityTypes.OXYGEN_CONCENTRATOR.get(), (be, face) -> {
            if (face == be.getBlockState().getValue(OxygenConcentratorBlock.HORIZONTAL_FACING).getOpposite())
                return be.tank.getCapability();
            return null;
        });
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, 1000);
        behaviours.add(tank);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (super.addToGoggleTooltip(tooltip, isPlayerSneaking)) {
            tooltip.add(Component.empty());
        }

        containedFluidTooltip(tooltip, isPlayerSneaking, tank.getCapability());
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (speed == 0 || overStressed)
            return;
        float abs = Math.abs(getSpeed());
        int increment = Mth.clamp(((int) abs - 100) / 200, 1, 5);
        airLevel = Math.min(500, airLevel + increment);

        ResourceKey<Level> dimension = level.dimension();
        if (dimension.equals(Level.OVERWORLD)) {
            tank.getPrimaryHandler().fill(new FluidStack(NorthstarFluids.OXYGEN.get(), increment), FluidAction.EXECUTE);
        } else if (dimension.equals(NorthstarDimensions.MARS_DIM_KEY) || dimension.equals(NorthstarDimensions.VENUS_DIM_KEY)) {
            tank.getPrimaryHandler().fill(new FluidStack(NorthstarFluids.CARBON.get(), increment), FluidAction.EXECUTE);
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);

        compound.putInt("Air", airLevel);
        compound.putInt("Timer", airTimer);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        airLevel = compound.getInt("Air");
        airTimer = compound.getInt("Timer");
    }

}
