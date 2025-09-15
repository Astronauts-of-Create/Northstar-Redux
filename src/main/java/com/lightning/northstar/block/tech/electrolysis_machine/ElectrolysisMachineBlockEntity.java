package com.lightning.northstar.block.tech.electrolysis_machine;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.item.NorthstarRecipeTypes;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.List;

public class ElectrolysisMachineBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {

    public static final BehaviourType<SmartFluidTankBehaviour>
            OUTPUT1 = new BehaviourType<>("Output1"),
            OUTPUT2 = new BehaviourType<>("Output2");

    private static final Object ELECTROLYSIS_RECIPE_KEY = new Object();

    protected SmartFluidTankBehaviour inputTank;
    protected SmartFluidTankBehaviour outputTankL;
    protected SmartFluidTankBehaviour outputTankR;
    protected float processingTime;
    protected Recipe<?> currentRecipe;

    public ElectrolysisMachineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                NorthstarBlockEntityTypes.ELECTROLYSIS_MACHINE.get(),
                (be, face) -> {
                    if (face == Direction.UP)
                        return be.inputTank.getCapability();
                    if (face == be.getBlockState().getValue(ElectrolysisMachineBlock.HORIZONTAL_FACING).getClockWise())
                        return be.outputTankL.getCapability();
                    if (face == be.getBlockState().getValue(ElectrolysisMachineBlock.HORIZONTAL_FACING).getCounterClockWise())
                        return be.outputTankR.getCapability();
                    return null;
                });
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

        if (!ElectrolysisRecipe.match(this, currentRecipe)) {
            currentRecipe = RecipeFinder.get(ELECTROLYSIS_RECIPE_KEY, level, this::matchStaticFilters)
                    .stream()
                    .filter(r -> ElectrolysisRecipe.match(this, r.value()))
                    .findFirst()
                    .map(RecipeHolder::value)
                    .orElse(null);
        }
        if (currentRecipe == null) {
            return;
        }


        processingTime += Math.abs(speed);

        int toProcess = (int) (processingTime / 256);
        processingTime %= 256;

        for (int i = 0; i < toProcess; i++) {
            if (!ElectrolysisRecipe.apply(this, currentRecipe, false)) {
                break;
            }
        }
    }

    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
        return recipe.value().getType() == NorthstarRecipeTypes.ELECTROLYSIS.getType();
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putFloat("ProcessingTime", processingTime);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        processingTime = compound.getFloat("ProcessingTime");
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (super.addToGoggleTooltip(tooltip, isPlayerSneaking)) {
            tooltip.add(Component.empty());
        }

        CreateLang.translate("gui.goggles.electrolysis_machine")
                .forGoggles(tooltip);

        addTankToolTip(tooltip, "gui.goggles.electrolysis_input", inputTank);
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

    public float getTotalFluidUnits(float partialTicks) {
        int renderedFluids = 0;
        float totalUnits = 0;

        SmartFluidTankBehaviour behaviour = inputTank;
        if (behaviour == null)
            return 0;
        for (SmartFluidTankBehaviour.TankSegment tankSegment : behaviour.getTanks()) {
            if (tankSegment.getRenderedFluid()
                    .isEmpty())
                continue;
            float units = tankSegment.getTotalUnits(partialTicks);
            if (units < 1)
                continue;
            totalUnits += units;
            renderedFluids++;
        }


        if (renderedFluids == 0)
            return 0;
        if (totalUnits < 1)
            return 0;
        return totalUnits;
    }

}