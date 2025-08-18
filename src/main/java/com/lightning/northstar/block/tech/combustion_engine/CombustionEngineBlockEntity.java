package com.lightning.northstar.block.tech.combustion_engine;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.content.NorthstarTags;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.List;

public class CombustionEngineBlockEntity extends GeneratingKineticBlockEntity implements IHaveGoggleInformation {

    SmartFluidTankBehaviour tank;
    boolean powered = false;
    int powerLevel = 0;

    public CombustionEngineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, NorthstarBlockEntityTypes.COMBUSTION_ENGINE.get(), (be, face) -> {
            if (face == be.getBlockState().getValue(CombustionEngineBlock.HORIZONTAL_FACING))
                return be.tank.getCapability();
            return null;
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick() {
        super.tick();
        int increment = 0;
        powered = false;
        Fluid fluid = tank.getPrimaryHandler().getFluid().getFluid();
        int fluidamount = tank.getPrimaryHandler().getFluidAmount();
        if (fluid.is(NorthstarTags.NorthstarFluidTags.TIER_1_ROCKET_FUEL.tag)) {
            if (fluidamount > 4) {
                increment = 4;
            } else {
                increment = fluidamount;
            }
            powered = true;
            powerLevel = 4;
        } else if (fluid.is(NorthstarTags.NorthstarFluidTags.TIER_2_ROCKET_FUEL.tag)) {
            if (fluidamount > 3) {
                increment = 3;
            } else {
                increment = fluidamount;
            }
            powered = true;
            powerLevel = 6;
        } else if (fluid.is(NorthstarTags.NorthstarFluidTags.TIER_3_ROCKET_FUEL.tag)) {
            if (fluidamount > 2) {
                increment = 2;
            } else {
                increment = fluidamount;
            }
            powered = true;
            powerLevel = 8;
        }
        tank.getPrimaryHandler().drain(increment, FluidAction.EXECUTE);
        this.updateGeneratedRotation();
    }

    @Override
    public float getGeneratedSpeed() {
        return (powered ? 1 : 0) * 128 * powerLevel / 8;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, 10000);
        behaviours.add(tank);
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
