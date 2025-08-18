package com.lightning.northstar.block.tech.oxygen_concentrator;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.content.NorthstarFluids;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.List;

public class OxygenConcentratorBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {

    public int airLevel;
    public int airTimer;

    SmartFluidTankBehaviour tank;
//    public Container container = new SimpleContainer(1) {
//          public void setChanged() {
//                 super.setChanged();
//                 OxygenConcentratorBlockEntity.this.slotsChanged(this);

    /// /          }
//    };
//    protected ItemStackHandler inventory;
    public OxygenConcentratorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, NorthstarBlockEntityTypes.OXYGEN_CONCENTRATOR.get(), (be, face) -> {
            if (face == be.getBlockState().getValue(OxygenConcentratorBlock.HORIZONTAL_FACING).getOpposite())
                return be.tank.getCapability();
            return null;
        });
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, 10000);
        behaviours.add(tank);
    }

//     public void slotsChanged(Container pInventory) {
//         if (pInventory == this.container) {
//             ItemStack item = container.getItem(0);
//             if (container.getItem(0).is(NorthstarItemTags.OXYGEN_SOURCES.tag)) {
//                 CompoundTag thing = item.getTag();
//                 ListTag lore = new ListTag();
//                 int currentOxy = thing.getInt("Oxygen");
//                 System.out.println(currentOxy);
//                 if(currentOxy < OxygenStuff.maximumOxy) {
//                     int oxytarget = OxygenStuff.maximumOxy - currentOxy;
//                     int newoxy = currentOxy;
//                     if(this.tank.getPrimaryHandler().getFluidAmount() > oxytarget) {
//                         newoxy += oxytarget;
//                         System.out.println("oxytarget " + oxytarget);
//                         thing.putInt("Oxygen", newoxy);
//                         lore.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Oxygen: " + newoxy + "mb").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false))).toString()));
//                         item.getOrCreateTagElement("display").put("Lore", lore);
//                         item.setTag(thing);
//                         System.out.println(thing);
//                         this.tank.getPrimaryHandler().drain(
//                         new FluidStack(NorthstarFluids.OXYGEN.get(), oxytarget), FluidAction.EXECUTE);
//                         return;
//                     }else
//                     {
//                         newoxy += this.tank.getPrimaryHandler().getFluidAmount();
//                         System.out.println("oxytarget " + oxytarget);
//                         thing.putInt("Oxygen", newoxy);
//                         lore.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Oxygen: " + newoxy + "mb").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false))).toString()));
//                         item.getOrCreateTagElement("display").put("Lore", lore);
//                         item.setTag(thing);
//                         System.out.println(thing);
//                         this.tank.getPrimaryHandler().drain(
//                         new FluidStack(NorthstarFluids.OXYGEN.get(), this.tank.getPrimaryHandler().getFluidAmount()), FluidAction.EXECUTE);
//                         return;
//                     }


//                 }
//             }
//         }
//     }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("gui.goggles.kinetic_stats")
                .forGoggles(tooltip);
        addStressImpactStats(tooltip, calculateStressApplied());

        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
        CreateLang.translate("gui.goggles.oxygen_concentrator")
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
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (getSpeed() == 0)
            return;
        float abs = Math.abs(getSpeed());
        int increment = Mth.clamp(((int) abs - 100) / 200, 1, 5);
        airLevel = Math.min(500, airLevel + increment);
        tank.getPrimaryHandler().fill(new FluidStack(NorthstarFluids.OXYGEN.get(), increment), FluidAction.EXECUTE);
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);

        compound.putInt("Air", airLevel);
        compound.putInt("Timer", airTimer);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        airLevel = compound.getInt("Air");
        airTimer = compound.getInt("Timer");
    }

}
