package com.lightning.northstar.block.tech.oxygen_filler;

import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.world.NorthstarOxygen;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.particle.AirParticleData;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.util.List;

public class OxygenFillerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    public int airLevel;
    public int airTimer;
    private boolean hasStopped = false;
    private int audioTick = 0;

    protected LazyOptional<IItemHandlerModifiable> itemCapability;
    SmartFluidTankBehaviour tank;
    public Container container = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            //             OxygenFillerBlockEntity.this.slotsChanged(this);
        }
    };
    protected ItemStackHandler inventory;

    public OxygenFillerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        inventory = new ItemStackHandler();
        itemCapability = LazyOptional.of(() -> new CombinedInvWrapper(inventory));
    }


    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, 1000);
        behaviours.add(tank);
    }

    public ItemStackHandler getInventoryOfBlock() {
        return inventory;
    }

    @Override
    public void tick() {
        super.tick();
        ItemStack item = container.getItem(0);
        Fluid fluid = tank.getPrimaryHandler().getFluid().getFluid();
        int increment = 2;
        if (item.is(NorthstarItemTags.OXYGEN_SOURCES.tag) && NorthstarOxygen.isOxygen(fluid)) {
            CompoundTag tag = item.getOrCreateTag();
            int currentOxy = tag.getInt("Oxygen");
            while (currentOxy + increment > NorthstarOxygen.MAXIMUM_OXYGEN) {
                increment--;
            }
            increment = Math.max(increment, 0);
            if (increment == 0 && !hasStopped) {
                AllSoundEvents.CONFIRM.playAt(level, worldPosition, 0.4f, 0, true);
                hasStopped = true;
            } else if (increment != 0) {
                hasStopped = false;
                audioTick++;
                if (level.isClientSide) {
                    if (audioTick % 13 == 0) {
                        BlockPos pos = this.getBlockPos();
                        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), NorthstarSounds.AIRFLOW.get(), SoundSource.BLOCKS, 0.5f, 0, false);
                    }
                    Vec3 centerOf = VecHelper.getCenterOf(worldPosition);
                    Vec3 v = VecHelper.offsetRandomly(centerOf, level.random, .65f);
                    Vec3 m = centerOf.subtract(v);
                    if (level.random.nextBoolean())
                        level.addParticle(new AirParticleData(1, .05f), v.x, v.y, v.z, m.x, m.y, m.z);
                }
            }
            int newOxyAmount = Mth.clamp(increment, 0, tank.getPrimaryHandler().getFluidAmount());
            int newoxy = currentOxy + newOxyAmount;
            tag.putInt("Oxygen", newoxy);
            item.setTag(tag);
            this.tank.getPrimaryHandler().drain(new FluidStack(fluid, newOxyAmount), FluidAction.EXECUTE);
        }
    }


    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("Air", airLevel);
        compound.putInt("Timer", airTimer);
        compound.put("item", this.container.getItem(0).save(new CompoundTag()));
    }

    @Override
    public void writeSafe(CompoundTag compound) {
        super.writeSafe(compound);
        compound.put("item", this.container.getItem(0).save(new CompoundTag()));
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        airLevel = compound.getInt("Air");
        airTimer = compound.getInt("Timer");
        this.container.setItem(0, ItemStack.of(compound.getCompound("item")));
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
        CreateLang.translate("gui.goggles.oxygen_filler")
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
        ItemStack itemStack = container.getItem(0);
        if (!itemStack.isEmpty()) {
            CreateLang.builder()
                    .add(CreateLang.number(itemStack.getCount())
                            .style(ChatFormatting.GRAY))
                    .text(ChatFormatting.DARK_GRAY, "x ")
                    .add(CreateLang.itemName(itemStack)
                            .style(ChatFormatting.GRAY))
                    .forGoggles(tooltip, 1);
            CompoundTag thing = itemStack.getTag();
            int currentOxy = thing.getInt("Oxygen");
            if (currentOxy != 0) {
                CreateLang.builder()
                        .add(CreateLang.number(currentOxy)
                                .style(ChatFormatting.GRAY))
                        .add(mb)
                        .style(ChatFormatting.GRAY)
                        .forGoggles(tooltip, 2);
            }
        }
        return true;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && side == getBlockState().getValue(OxygenFillerBlock.HORIZONTAL_FACING).getOpposite())
            return tank.getCapability().cast();
        return super.getCapability(cap, side);
    }

}
