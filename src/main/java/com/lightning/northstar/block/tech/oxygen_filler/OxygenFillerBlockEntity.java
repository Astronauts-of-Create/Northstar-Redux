package com.lightning.northstar.block.tech.oxygen_filler;

import com.lightning.northstar.content.*;
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
import net.minecraft.core.HolderLookup;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.List;

public class OxygenFillerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    public int airLevel;
    public int airTimer;
    private boolean hasStopped = false;
    private int audioTick = 0;

    protected IItemHandlerModifiable itemCapability;
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
        itemCapability = new CombinedInvWrapper(inventory);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, NorthstarBlockEntityTypes.OXYGEN_FILLER.get(), (be, face) -> {
            if (face == be.getBlockState().getValue(OxygenFillerBlock.HORIZONTAL_FACING).getOpposite())
                return be.tank.getCapability();
            return null;
        });
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
            int currentOxy = item.has(NorthstarDataComponents.OXYGEN) ? item.get(NorthstarDataComponents.OXYGEN) : 0;
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
            item.set(NorthstarDataComponents.OXYGEN, newoxy);
            this.tank.getPrimaryHandler().drain(new FluidStack(fluid, newOxyAmount), FluidAction.EXECUTE);
        }
    }


    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        tag.putInt("Air", airLevel);
        tag.putInt("Timer", airTimer);
        tag.put("item", container.getItem(0).saveOptional(registries));
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeSafe(tag, registries);

        tag.put("item", container.getItem(0).saveOptional(registries));
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        airLevel = tag.getInt("Air");
        airTimer = tag.getInt("Timer");
        container.setItem(0, ItemStack.parseOptional(registries, tag.getCompound("item")));
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
            int currentOxy = itemStack.has(NorthstarDataComponents.OXYGEN) ? itemStack.get(NorthstarDataComponents.OXYGEN) : 0;
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

}
