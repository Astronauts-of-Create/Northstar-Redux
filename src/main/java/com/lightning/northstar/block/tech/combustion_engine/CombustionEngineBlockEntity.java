package com.lightning.northstar.block.tech.combustion_engine;

import com.lightning.northstar.block.tech.oxygen_concentrator.OxygenConcentratorBlock;
import com.lightning.northstar.content.NorthstarTags;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.List;

public class CombustionEngineBlockEntity extends GeneratingKineticBlockEntity implements IHaveGoggleInformation {

    public SmartFluidTankBehaviour tank;
    public boolean powered = false;
    public int powerLevel = 0;

    public CombustionEngineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        int requiredFuel = 0;
        int power = 0;

        Fluid fluid = tank.getPrimaryHandler().getFluid().getFluid();
        int fluidAmount = tank.getPrimaryHandler().getFluidAmount();
        if (NorthstarTags.NorthstarFluidTags.TIER_1_ROCKET_FUEL.matches(fluid)) {
            requiredFuel = 4;
            power = 4;
        } else if (NorthstarTags.NorthstarFluidTags.TIER_2_ROCKET_FUEL.matches(fluid)) {
            requiredFuel = 3;
            power = 6;
        } else if (NorthstarTags.NorthstarFluidTags.TIER_3_ROCKET_FUEL.matches(fluid)) {
            requiredFuel = 2;
            power = 8;
        }

        if (power == 0 || fluidAmount < requiredFuel) {
            powered = false;
            updateGeneratedRotation();
            return;
        }

        powered = true;
        powerLevel = power;
        updateGeneratedRotation();

        tank.getPrimaryHandler().drain(requiredFuel, FluidAction.EXECUTE);
    }

    @Override
    public float getGeneratedSpeed() {
        return powered ? 128 * (powerLevel / 8f) : 0;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, 10000));
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && side == getBlockState().getValue(OxygenConcentratorBlock.HORIZONTAL_FACING))
            return tank.getCapability().cast();
        return super.getCapability(cap, side);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
        CreateLang.translate("gui.goggles.combustion_engine")
                .forGoggles(tooltip);
        FluidStack fluidStack = tank.getPrimaryHandler().getFluidInTank(0);
        if (!fluidStack.getFluid().getFluidType().isAir()) {
            CreateLang.fluidName(fluidStack)
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
        } else {
            CreateLang.translate("gui.goggles.empty")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
        }
        CreateLang.builder()
                .add(CreateLang.number(fluidStack.getAmount())
                        .add(mb)
                        .style(ChatFormatting.GOLD))
                .text(ChatFormatting.GRAY, " / ")
                .add(CreateLang.number(tank.getPrimaryHandler().getTankCapacity(0))
                        .add(mb)
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);

        float stressBase = calculateAddedStressCapacity();
        float speed = getTheoreticalSpeed();
        if (speed != getGeneratedSpeed() && speed != 0)
            stressBase *= getGeneratedSpeed() / speed;
        speed = Math.abs(speed);

        float stressTotal = stressBase * speed;

        CreateLang.translate("tooltip.capacityProvided")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);

        CreateLang.number(stressTotal)
                .translate("generic.unit.stress")
                .style(ChatFormatting.AQUA)
                .space()
                .add(CreateLang.translate("gui.goggles.at_current_speed")
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);
        return true;
    }

}
