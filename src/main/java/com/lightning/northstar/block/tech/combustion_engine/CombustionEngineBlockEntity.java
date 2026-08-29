package com.lightning.northstar.block.tech.combustion_engine;

import com.lightning.northstar.client.BasicTickableSoundInstance;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.contraption.FuelType;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CombustionEngineBlockEntity extends GeneratingKineticBlockEntity implements IHaveGoggleInformation {

    public static final float OXYGEN_CONSUMPTION_MULTIPLIER = 20;
    public static final BehaviourType<SmartFluidTankBehaviour>
            FUEL_TANK = new BehaviourType<>("Fuel"),
            OXIDIZER_TANK = new BehaviourType<>("Oxidizer");

    public ScrollOptionBehaviour<WindmillBearingBlockEntity.RotationDirection> movementDirection;
    public SmartFluidTankBehaviour fuelTank;
    public SmartFluidTankBehaviour oxidizerTank;
    protected float fuelUsageBuffer;
    protected float oxidizerUsageBuffer;
    protected float generatorSpeed;
    protected Fluid lastFluid;
    protected FuelType fuelType;
    protected boolean disabled;

    @OnlyIn(Dist.CLIENT)
    protected BasicTickableSoundInstance sound;

    public CombustionEngineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(fuelTank = new SmartFluidTankBehaviour(FUEL_TANK, this, 1, 1000, false));
        behaviours.add(oxidizerTank = new SmartFluidTankBehaviour(OXIDIZER_TANK, this, 1, 1000, false));

        ValueBoxTransform slot = new ValueBoxTransform.Sided() {
            @Override
            protected boolean isSideActive(BlockState state, Direction direction) {
                return direction == Direction.UP;
            }

            @Override
            public Vec3 getLocalOffset(BlockState state) {
                return VecHelper.voxelSpace(8, 12, 8);
            }

            @Override
            protected Vec3 getSouthLocation() {
                return Vec3.ZERO;
            }
        };

        movementDirection = new ScrollOptionBehaviour<>(WindmillBearingBlockEntity.RotationDirection.class,
                Lang.translateDirect("contraptions.windmill.rotation_direction"), this, slot);
        movementDirection.value = 1;
        movementDirection.withCallback($ -> reActivateSource = true);
        behaviours.add(movementDirection);
    }

    @Override
    public void tick() {
        super.tick();

        FluidStack fluid = fuelTank.getPrimaryHandler().getFluid();
        if (!fluid.getFluid().equals(lastFluid)) {
            lastFluid = fluid.getFluid();
            fuelType = FuelType.getFuelType(lastFluid);
            if (fuelType != null && (fuelType.combustionEngineRpm() == 0 || fuelType.combustionEngineUse() == 0))
                fuelType = null;
        }

        FuelType fuel = this.fuelType;
        if (fuel == null) {
            setGeneratorSpeed(0);
            return;
        }

        boolean hasAtmosphere = level.northstar$oxygen().hasOxygen();
        boolean hasOxidizer = !hasAtmosphere && NorthstarOxygen.isBreathable(oxidizerTank.getPrimaryHandler().getFluidInTank(0).getFluid());
        NorthstarOxygen.Provider sealer = hasAtmosphere || hasOxidizer ? null : level.northstar$oxygen().getSealer(worldPosition);

        if (!hasAtmosphere && !hasOxidizer && sealer == null) {
            setGeneratorSpeed(0);
            return;
        }

        if (generatorSpeed > 0 && !isOverStressed()) {
            fuelUsageBuffer += fuel.combustionEngineUse();
            if (!hasAtmosphere) {
                oxidizerUsageBuffer += NorthstarConfigs.server().oxygenSealerBlockActiveDrain.getF() * OXYGEN_CONSUMPTION_MULTIPLIER;
            }
        }

        fuelUsageBuffer = drainTank(fuelTank, fuelUsageBuffer);

        if (hasOxidizer) {
            oxidizerUsageBuffer = drainTank(oxidizerTank, oxidizerUsageBuffer);
        } else if (sealer != null) {
            sealer.drainOxygen(oxidizerUsageBuffer);
            oxidizerUsageBuffer = 0;
        }

        if (fuelUsageBuffer > 1 || oxidizerUsageBuffer > 1 || disabled) {
            setGeneratorSpeed(0);
        } else if (generatorSpeed == 0) {
            setGeneratorSpeed(fuel.combustionEngineRpm());
        }
    }

    private float drainTank(SmartFluidTankBehaviour tank, float buffer) {
        int drainable = Math.min(tank.getPrimaryHandler().getFluidAmount(), (int) buffer);
        tank.getPrimaryHandler().drain(drainable, IFluidHandler.FluidAction.EXECUTE);
        return buffer - drainable;
    }

    private void setGeneratorSpeed(float generatorSpeed) {
        if (this.generatorSpeed != generatorSpeed) {
            this.generatorSpeed = generatorSpeed;
            updateGeneratedRotation();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        //super.tickAudio();

        sound = BasicTickableSoundInstance.playLoopingSound(this, sound, !Mth.equal(generatorSpeed, 0) && !isOverStressed(), NorthstarSounds.COMBUSTION_ENGINE.get());
    }

    @Override
    protected boolean isNoisy() {
        return false; // we're still noisy but disable the base Create sounds
    }

    @Override
    public float getGeneratedSpeed() {
        return convertToDirection(generatorSpeed * (movementDirection.getValue() == 1 ? -1 : 1), getBlockState().getValue(CombustionEngineBlock.HORIZONTAL_FACING));
    }

    public void updateRedstone() {
        if (level.isClientSide())
            return;
        boolean powered = level.hasNeighborSignal(worldPosition);
        if (powered == disabled)
            return;
        disabled = powered;
        notifyUpdate();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (side == null)
                return LazyOptional.of(() -> new CombinedTankWrapper(fuelTank.getPrimaryHandler(), oxidizerTank.getPrimaryHandler())).cast();
            if (side == getBlockState().getValue(CombustionEngineBlock.HORIZONTAL_FACING))
                return fuelTank.getCapability().cast();
            if (side == Direction.DOWN)
                return oxidizerTank.getCapability().cast();
            return LazyOptional.empty();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        NorthstarLang.addTankTooltip(tooltip, fuelTank.getPrimaryHandler(), Component.translatable("northstar.gui.goggles.combustion_engine.fuel"));
        if (!level.northstar$oxygen().hasOxygen() || !oxidizerTank.isEmpty()) {
            NorthstarLang.addTankTooltip(tooltip, oxidizerTank.getPrimaryHandler(), Component.translatable("northstar.gui.goggles.combustion_engine.oxidizer"));
        }

        if (fuelType != null) {
            NorthstarLang.translate("gui.goggles.combustion_engine.fuel_usage")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            NorthstarLang.number(fuelType.combustionEngineUse())
                    .style(ChatFormatting.GOLD)
                    .add(NorthstarLang.MB_PER_TICK)
                    .forGoggles(tooltip, 1);
        }

        if (!level.northstar$oxygen().hasOxygen()) {
            NorthstarLang.translate("gui.goggles.combustion_engine.oxidizer_usage")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            NorthstarLang.number(NorthstarConfigs.server().oxygenSealerBlockActiveDrain.getF() * OXYGEN_CONSUMPTION_MULTIPLIER)
                    .style(ChatFormatting.GOLD)
                    .add(NorthstarLang.MB_PER_TICK)
                    .forGoggles(tooltip, 1);
        }

        return true;
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        generatorSpeed = compound.getFloat("GeneratorSpeed");
        fuelUsageBuffer = compound.getFloat("FuelUsageBuffer");
        oxidizerUsageBuffer = compound.getFloat("OxidizerUsageBuffer");
        disabled = compound.getBoolean("Disabled");
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("GeneratorSpeed", generatorSpeed);
        compound.putFloat("FuelUsageBuffer", fuelUsageBuffer);
        compound.putFloat("OxidizerUsageBuffer", oxidizerUsageBuffer);
        compound.putBoolean("Disabled", disabled);
    }

}
