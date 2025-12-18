package com.lightning.northstar.block.tech.oxygen_filler;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class OxygenFillerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    public final Container container = new SimpleContainer(1);
    public final IFluidHandler fluidHandler = new IFluidHandler() {
        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            ItemStack item = getContainedItem();
            if (tank == 0 || item == null || !item.has(NorthstarDataComponents.OXYGEN))
                return FluidStack.EMPTY;
            int oxygen = item.get(NorthstarDataComponents.OXYGEN);
            return oxygen == 0 ? FluidStack.EMPTY : new FluidStack(NorthstarFluids.OXYGEN.get(), oxygen);
        }

        @Override
        public int getTankCapacity(int tank) {
            return getContainedItem() == null ? 0 : NorthstarOxygen.MAXIMUM_OXYGEN;
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return tank == 0 && NorthstarOxygen.isOxygen(stack.getFluid());
        }

        @Override
        public int fill(FluidStack stack, FluidAction action) {
            ItemStack item = getContainedItem();
            if (!isFluidValid(0, stack) || item == null)
                return 0;
            int oxygen = item.has(NorthstarDataComponents.OXYGEN) ? item.get(NorthstarDataComponents.OXYGEN) : 0;
            int fillable = Mth.clamp(NorthstarOxygen.MAXIMUM_OXYGEN - oxygen, 0, stack.getAmount());
            if (action.execute() && fillable != 0) {
                item.set(NorthstarDataComponents.OXYGEN, oxygen + fillable);
                sendData();
                if (oxygen + fillable >= NorthstarOxygen.MAXIMUM_OXYGEN) {
                    AllSoundEvents.CONFIRM.playOnServer(level, worldPosition, 0.4f, 0);
                }
            }
            return fillable;
        }

        @Override
        public FluidStack drain(FluidStack stack, FluidAction action) {
            if (!isFluidValid(0, stack))
                return FluidStack.EMPTY;
            FluidStack drained = stack.copy();
            drained.setAmount(drainAmount(stack.getAmount(), action));
            return drained;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return new FluidStack(NorthstarFluids.OXYGEN.get(), drainAmount(maxDrain, action));
        }

        private int drainAmount(int amount, FluidAction action) {
            ItemStack item = getContainedItem();
            if (item == null)
                return 0;
            int oxygen = item.has(NorthstarDataComponents.OXYGEN) ? item.get(NorthstarDataComponents.OXYGEN) : 0;
            int drainable = Math.min(amount, oxygen);
            if (action.execute() && drainable != 0) {
                item.set(NorthstarDataComponents.OXYGEN, oxygen - drainable);
                sendData();
            }
            return drainable;
        }
    };

    public OxygenFillerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void destroy() {
        super.destroy();

        ItemHelper.dropContents(level, worldPosition, new InvWrapper(container));
        container.clearContent();
    }

    @Nullable
    protected ItemStack getContainedItem() {
        ItemStack item = container.getItem(0);
        return NorthstarItemTags.OXYGEN_SOURCES.matches(item) ? item : null;
    }

    @Override
    public void tick() {
        super.tick();

        /*if (level.isClientSide) {
            if (audioTick++ % 13 == 0) {
                level.playLocalSound(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), NorthstarSounds.AIRFLOW.get(), SoundSource.BLOCKS, 0.5f, 0, false);
            }

            if (level.random.nextBoolean()) {
                Vec3 c = VecHelper.getCenterOf(worldPosition);
                Vec3 v = VecHelper.offsetRandomly(c, level.random, .65f);
                Vec3 m = c.subtract(v);
                level.addParticle(new AirParticleData(1, .05f), v.x, v.y, v.z, m.x, m.y, m.z);
            }
        }*/
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);

        compound.put("item", container.getItem(0).saveOptional(registries));
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        container.setItem(0, ItemStack.parseOptional(registries, compound.getCompound("item")));
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        ItemStack item = container.getItem(0);
        if (item.isEmpty())
            return false;

        NorthstarLang.translate("gui.goggles.oxygen_filler")
                .forGoggles(tooltip);
        CreateLang.itemName(item)
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);

        if (NorthstarItemTags.OXYGEN_SOURCES.matches(item)) {
            int oxygen = item.has(NorthstarDataComponents.OXYGEN) ? item.get(NorthstarDataComponents.OXYGEN) : 0;
            CreateLang.builder()
                    .add(CreateLang.number(oxygen)
                            .add(NorthstarLang.MB)
                            .style(ChatFormatting.GOLD))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(CreateLang.number(NorthstarOxygen.MAXIMUM_OXYGEN)
                            .add(NorthstarLang.MB)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
        } else {
            NorthstarLang.translate("gui.goggles.oxygen_filler.not_oxygen_tank")
                    .style(ChatFormatting.RED)
                    .forGoggles(tooltip, 1);
        }

        return true;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, NorthstarBlockEntityTypes.OXYGEN_FILLER.get(), (be, face) -> {
            if (face == null || face == be.getBlockState().getValue(OxygenFillerBlock.HORIZONTAL_FACING).getOpposite())
                return be.fluidHandler;
            return null;
        });
    }

}
