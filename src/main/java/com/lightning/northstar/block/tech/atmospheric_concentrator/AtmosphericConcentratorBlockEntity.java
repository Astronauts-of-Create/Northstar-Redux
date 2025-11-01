package com.lightning.northstar.block.tech.atmospheric_concentrator;

import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.world.dimension.NorthstarDimensions;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AtmosphericConcentratorBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {

    protected SmartFluidTankBehaviour tank;
    protected float buffer;

    public AtmosphericConcentratorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, 1000)
                .forbidInsertion();
        behaviours.add(tank);
    }

    @Override
    public void tick() {
        super.tick();

        Fluid fluid = getCollectedFluid();
        float speed = getCollectionSpeed();
        if (fluid == Fluids.EMPTY || Mth.equal(speed, 0))
            return;

        float newBuffer = buffer + speed;
        int filled = Mth.floor(newBuffer);
        buffer = newBuffer - filled;
        tank.getPrimaryHandler().fill(new FluidStack(fluid, filled), FluidAction.EXECUTE);
    }

    public Fluid getCollectedFluid() {
        ResourceKey<Level> dimension = level.dimension();
        if (dimension.equals(Level.OVERWORLD))
            return NorthstarFluids.OXYGEN.getSource();
        if (dimension.equals(NorthstarDimensions.MARS_DIM_KEY) || dimension.equals(NorthstarDimensions.VENUS_DIM_KEY))
            return NorthstarFluids.CARBON.getSource();
        return Fluids.EMPTY;
    }

    public float getCollectionSpeed() {
        // 10 mB/t at 256 RPM
        return Math.abs(speed) / 25.6f;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        NorthstarLang.translate("gui.goggles.atmospheric_concentrator")
                .forGoggles(tooltip);

        if (IRotate.StressImpact.isEnabled())
            addStressImpactStats(tooltip, calculateStressApplied());

        Fluid fluid = getCollectedFluid();
        if (fluid == Fluids.EMPTY) {
            NorthstarLang.translate("gui.goggles.atmospheric_concentrator.no_atmosphere")
                    .style(ChatFormatting.RED)
                    .forGoggles(tooltip);
        } else {
            NorthstarLang.translate("gui.goggles.atmospheric_concentrator.collected_fluid")
                    .style(ChatFormatting.GRAY)
                    .add(Lang.fluidName(new FluidStack(fluid, 1)))
                    .forGoggles(tooltip);

            Lang.builder()
                    .add(Lang.number(tank.getPrimaryHandler().getFluidAmount())
                            .add(NorthstarLang.MB)
                            .style(ChatFormatting.GOLD))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(Lang.number(tank.getPrimaryHandler().getCapacity())
                            .add(NorthstarLang.MB)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);

            NorthstarLang.translate("gui.goggles.atmospheric_concentrator.collection_rate")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);

            Lang.number(getCollectionSpeed())
                    .add(NorthstarLang.MB_PER_TICK)
                    .style(ChatFormatting.GOLD)
                    .forGoggles(tooltip, 1);
        }

        return true;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && (side == null || side == getBlockState().getValue(AtmosphericConcentratorBlock.HORIZONTAL_FACING).getOpposite()))
            return tank.getCapability().cast();
        return super.getCapability(cap, side);
    }

}
