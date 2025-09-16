package com.lightning.northstar.block.tech.oxygen_concentrator;

import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.world.dimension.NorthstarDimensions;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.List;

public class OxygenConcentratorBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {

    public int airLevel;
    public int airTimer;

    public SmartFluidTankBehaviour tank;

    public OxygenConcentratorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
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

        containedFluidTooltip(tooltip, isPlayerSneaking, tank.getCapability().cast());
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
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("Air", airLevel);
        compound.putInt("Timer", airTimer);
    }

    @Override
    public void writeSafe(CompoundTag compound) {
        super.writeSafe(compound);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        airLevel = compound.getInt("Air");
        airTimer = compound.getInt("Timer");
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && side == getBlockState().getValue(OxygenConcentratorBlock.HORIZONTAL_FACING).getOpposite())
            return tank.getCapability().cast();
        return super.getCapability(cap, side);
    }

}
