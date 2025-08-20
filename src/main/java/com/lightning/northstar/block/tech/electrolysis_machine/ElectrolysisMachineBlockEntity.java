package com.lightning.northstar.block.tech.electrolysis_machine;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarFluids;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElectrolysisMachineBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {

    public static final BehaviourType<SmartFluidTankBehaviour>
            OUTPUT1 = new BehaviourType<>("Output1"),
            OUTPUT2 = new BehaviourType<>("Output2");

    protected SmartFluidTankBehaviour inputTank;
    protected SmartFluidTankBehaviour outputTankL;
    protected SmartFluidTankBehaviour outputTankR;
    protected float processingTime;

    public ElectrolysisMachineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        inputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 1000, true);
        outputTankL = new SmartFluidTankBehaviour(OUTPUT1, this, 1, 1000, true).forbidInsertion();
        outputTankR = new SmartFluidTankBehaviour(OUTPUT2, this, 1, 1000, true).forbidInsertion();
        behaviours.add(inputTank);
        behaviours.add(outputTankL);
        behaviours.add(outputTankR);
    }

    @Override
    public void tick() {
        super.tick();

        processingTime += Math.abs(speed);

        // simple loop, should work fine unless someone tweaks the configuration to reach millions of RPM (please don't do that)
        while (processingTime >= 256) {
            processingTime -= 256;

            if (inputTank.getPrimaryHandler().getFluid().getFluid() == Fluids.WATER.getSource()) {
                if (outputTankR.getPrimaryHandler().getFluidAmount() <= 998 && outputTankR.getPrimaryHandler().getFluidAmount() <= 993 && inputTank.getPrimaryHandler().getFluidAmount() >= 10) {
                    inputTank.getPrimaryHandler().drain(new FluidStack(Fluids.WATER.getSource(), 10), FluidAction.EXECUTE);
                    outputTankL.getPrimaryHandler().fill(new FluidStack(NorthstarFluids.HYDROGEN.get().getSource(), 2), FluidAction.EXECUTE);
                    outputTankR.getPrimaryHandler().fill(new FluidStack(NorthstarFluids.OXYGEN.get().getSource(), 7), FluidAction.EXECUTE);
                } else if (processingTime >= 256) {
                    processingTime %= 256; // we can't process anymore for this tick as the tank is full, just ignore the rest
                }
            }
        }
    }


    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("ProcessingTime", processingTime);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        processingTime = compound.getFloat("ProcessingTime");
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        addTankToolTip(tooltip, "gui.goggles.electrolysis_machine", inputTank);
        addTankToolTip(tooltip, "gui.goggles.electrolysis_orange_port", outputTankL);
        addTankToolTip(tooltip, "gui.goggles.electrolysis_blue_port", outputTankR);
        return true;
    }

    private void addTankToolTip(List<Component> tooltip, String color, SmartFluidTankBehaviour tank) {
        FluidStack fluidStack = tank.getPrimaryHandler().getFluidInTank(0);

        if (!fluidStack.getFluid().getFluidType().isAir()) {
            CreateLang.translate(color)
                    .add(CreateLang.fluidName(fluidStack))
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
        } else {
            CreateLang.translate(color)
                    .add(CreateLang.translate("gui.goggles.empty"))
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
        }

        Lang.builder(Northstar.MOD_ID)
                .add(CreateLang.number(fluidStack.getAmount())
                        .add(CreateLang.translate("generic.unit.millibuckets"))
                        .style(ChatFormatting.GOLD))
                .text(ChatFormatting.GRAY, " / ")
                .add(CreateLang.number(tank.getPrimaryHandler().getTankCapacity(0))
                        .add(CreateLang.translate("generic.unit.millibuckets"))
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (isFluidHandlerCap(cap) && side == Direction.UP)
            return inputTank.getCapability().cast();
        if (isFluidHandlerCap(cap) && side == getBlockState().getValue(ElectrolysisMachineBlock.HORIZONTAL_FACING).getClockWise())
            return outputTankL.getCapability().cast();
        if (isFluidHandlerCap(cap) && side == getBlockState().getValue(ElectrolysisMachineBlock.HORIZONTAL_FACING).getCounterClockWise())
            return outputTankR.getCapability().cast();
        return super.getCapability(cap, side);
    }

}
