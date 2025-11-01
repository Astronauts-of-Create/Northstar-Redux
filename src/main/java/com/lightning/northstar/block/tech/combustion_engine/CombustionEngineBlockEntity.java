package com.lightning.northstar.block.tech.combustion_engine;

import com.lightning.northstar.block.tech.atmospheric_concentrator.AtmosphericConcentratorBlock;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.lightning.northstar.client.BasicTickableSoundInstance;
import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.contraption.FuelType;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
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

    public ScrollOptionBehaviour<WindmillBearingBlockEntity.RotationDirection> movementDirection;
    public SmartFluidTankBehaviour tank;
    protected float usageBuffer;
    protected float generatorSpeed;
    protected Fluid lastFluid;
    protected FuelType fuelType;

    @OnlyIn(Dist.CLIENT)
    protected BasicTickableSoundInstance sound;

    public CombustionEngineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, 1000));

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
        movementDirection.withCallback($ -> reActivateSource = true);
        behaviours.add(movementDirection);
    }

    @Override
    public void tick() {
        super.tick();

        FluidStack fluid = tank.getPrimaryHandler().getFluid();
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

        if (!NorthstarOxygen.hasOxygen(level, worldPosition)) {
            setGeneratorSpeed(0);
            return;
        }

        if (generatorSpeed > 0 && !isOverStressed())
            usageBuffer += fuel.combustionEngineUse();
        int drainable = Math.min(fluid.getAmount(), (int) usageBuffer);
        tank.getPrimaryHandler().drain(drainable, IFluidHandler.FluidAction.EXECUTE);
        usageBuffer -= drainable;

        if (usageBuffer > 1) {
            setGeneratorSpeed(0);
        } else if (generatorSpeed == 0) {
            setGeneratorSpeed(fuel.combustionEngineRpm());
        }
    }

    private void setGeneratorSpeed(float generatorSpeed) {
        if (this.generatorSpeed != generatorSpeed) {
            this.generatorSpeed = generatorSpeed;
            updateGeneratedRotation();
        }
    }

    //1 large water wheel can spin a mill at 128 (half) speed before it overstresses
    //10 torque can move 3 mills at full speed before overstressing
    final static float TORQUE = 10; //128 was original

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();

        if (!Mth.equal(generatorSpeed, 0) && !isOverStressed()) {
            if (sound == null || sound.isStopped()) {
                sound = new BasicTickableSoundInstance(NorthstarSounds.COMBUSTION_ENGINE.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom(), this);
                sound.setLooping(true);
                Minecraft.getInstance().getSoundManager().play(sound);
            }
        } else if (sound != null) {
            sound.cancel();
            sound = null;
        }
    }

    @Override
    protected boolean isNoisy() {
        return false; // we're still noisy but disable the base Create sounds
    }

    @Override
    public float getGeneratedSpeed() {
        return generatorSpeed * (movementDirection.getValue() == 1 ? 1 : -1);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && (side == null || side == getBlockState().getValue(AtmosphericConcentratorBlock.HORIZONTAL_FACING)))
            return tank.getCapability().cast();
        return super.getCapability(cap, side);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        FluidStack fluidStack = tank.getPrimaryHandler().getFluidInTank(0);
        if (!fluidStack.getFluid().getFluidType().isAir()) {
            Lang.fluidName(fluidStack)
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
        } else {
            Lang.translate("gui.goggles.empty")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
        }

        LangBuilder mb = Lang.translate("generic.unit.millibuckets");
        Lang.builder()
                .add(Lang.number(fluidStack.getAmount())
                        .add(mb)
                        .style(ChatFormatting.GOLD))
                .text(ChatFormatting.GRAY, " / ")
                .add(Lang.number(tank.getPrimaryHandler().getTankCapacity(0))
                        .add(mb)
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);

        if (fuelType != null) {
            NorthstarLang.translate("gui.goggles.fuel_use")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            Lang.number(fuelType.combustionEngineUse())
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
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("GeneratorSpeed", generatorSpeed);
    }

}
