package com.lightning.northstar.block.tech.ice_box;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.item.NorthstarRecipeTypes;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IceBoxBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    private static final Object FREEZING_RECIPES_KEY = new Object();
    public static final int OUTPUT_ANIMATION_TIME = 10;

    protected SmartInventory inputInventory;
    protected SmartInventory outputInventory;
    protected SmartFluidTankBehaviour inputTank;
    protected SmartFluidTankBehaviour outputTank;
    protected FilteringBehaviour filtering;

    protected IItemHandlerModifiable itemCapability;
    protected IFluidHandler fluidCapability;

    protected boolean contentsChanged;
    protected Recipe<?> currentRecipe;

    protected int processingTicks;

    protected List<IntAttached<ItemStack>> visualizedOutputItems;
    protected List<IntAttached<FluidStack>> visualizedOutputFluids;

    public IceBoxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        inputInventory = new SmartInventory(9, this, 16, true);
        inputInventory.whenContentsChanged($ -> contentsChanged = true);

        outputInventory = new SmartInventory(9, this, 16, true)
                .forbidInsertion()
                .withMaxStackSize(64);

        contentsChanged = true;
        itemCapability = new CombinedInvWrapper(inputInventory, outputInventory);

        visualizedOutputFluids = Collections.synchronizedList(new ArrayList<>());
        visualizedOutputItems = Collections.synchronizedList(new ArrayList<>());
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, NorthstarBlockEntityTypes.ICE_BOX.get(), (be, face) -> be.itemCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, NorthstarBlockEntityTypes.ICE_BOX.get(), (be, face) -> be.fluidCapability);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));

        filtering = new FilteringBehaviour(this, new IceBoxValueBox())
                .withCallback($ -> contentsChanged = true)
                .forRecipes();
        behaviours.add(filtering);

        inputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 2, 1000, true)
                .whenFluidUpdates(() -> contentsChanged = true);
        behaviours.add(inputTank);

        outputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 2, 1000, true)
                .whenFluidUpdates(() -> contentsChanged = true)
                .forbidInsertion();
        behaviours.add(outputTank);

        fluidCapability = new CombinedTankWrapper(outputTank.getCapability(), inputTank.getCapability());
    }

    @Override
    public void destroy() {
        super.destroy();

        ItemHelper.dropContents(level, worldPosition, inputInventory);
        ItemHelper.dropContents(level, worldPosition, outputInventory);
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide)
            return;

        if (contentsChanged) {
            currentRecipe = getMatchingRecipe();
            contentsChanged = false;
        }

        if (!(currentRecipe instanceof FreezingRecipe r))
            return;

        float currentTemperature = NorthstarTemperature.getTemperatureAt(level, worldPosition);
        if (!r.isTemperatureWithinRange(currentTemperature)) {
            processingTicks = Math.max(0, processingTicks - 1);
            return;
        }

        if (++processingTicks >= r.getProcessingDuration()) {
            if (FreezingRecipe.apply(this, r)) {
                processingTicks = 0;
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 16);
            }
        }
    }

    public boolean acceptOutputs(List<ItemStack> outputItems, List<FluidStack> outputFluids, boolean simulate) {
        outputInventory.allowInsertion();
        outputTank.allowInsertion();
        boolean acceptOutputsInner = acceptOutputsInner(outputItems, outputFluids, simulate);
        outputInventory.forbidInsertion();
        outputTank.forbidInsertion();
        return acceptOutputsInner;
    }

    private boolean acceptOutputsInner(List<ItemStack> outputItems, List<FluidStack> outputFluids, boolean simulate) {
        BlockState blockState = getBlockState();
        if (!(blockState.getBlock() instanceof IceBoxBlock))
            return false;

        Direction direction = blockState.getValue(IceBoxBlock.FACING);
        if (direction != Direction.DOWN) {

            BlockEntity be = level.getBlockEntity(worldPosition.below().relative(direction));

            InvManipulationBehaviour inserter = be == null ? null : BlockEntityBehaviour.get(level, be.getBlockPos(), InvManipulationBehaviour.TYPE);
            IItemHandler targetInv = be == null ? null : level.getCapability(Capabilities.ItemHandler.BLOCK, be.getBlockPos(), be.getBlockState(), be, direction.getOpposite());
            if (targetInv == null && inserter != null)
                targetInv = inserter.getInventory();
            IFluidHandler targetTank = be == null ? null : level.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), be.getBlockState(), be, direction.getOpposite());
            boolean externalTankNotPresent = targetTank == null;

            if (!outputItems.isEmpty() && targetInv == null)
                return false;
            if (!outputFluids.isEmpty() && externalTankNotPresent) {
                // Special case - fluid outputs but output only accepts items
                targetTank = outputTank.getCapability();
                if (targetTank == null)
                    return false;
                if (!acceptFluidOutputsIntoIceBox(outputFluids, simulate, targetTank))
                    return false;
            }

            if (simulate)
                return true;
        }

        IItemHandler targetInv = outputInventory;
        IFluidHandler targetTank = outputTank.getCapability();

        if (targetInv == null && !outputItems.isEmpty())
            return false;
        if (!acceptItemOutputsIntoIceBox(outputItems, simulate, targetInv))
            return false;
        if (outputFluids.isEmpty())
            return true;
        if (targetTank == null)
            return false;
        if (!acceptFluidOutputsIntoIceBox(outputFluids, simulate, targetTank))
            return false;

        return true;
    }

    private boolean acceptFluidOutputsIntoIceBox(List<FluidStack> outputFluids, boolean simulate, IFluidHandler targetTank) {
        for (FluidStack fluidStack : outputFluids) {
            FluidAction action = simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE;
            int fill = targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler
                    ? ((SmartFluidTankBehaviour.InternalFluidHandler) targetTank).forceFill(fluidStack.copy(), action)
                    : targetTank.fill(fluidStack.copy(), action);
            if (fill != fluidStack.getAmount())
                return false;
        }
        return true;
    }

    private boolean acceptItemOutputsIntoIceBox(List<ItemStack> outputItems, boolean simulate, IItemHandler targetInv) {
        for (ItemStack itemStack : outputItems) {
            if (!ItemHandlerHelper.insertItemStacked(targetInv, itemStack.copy(), simulate)
                    .isEmpty())
                return false;
        }
        return true;
    }

    public float getTotalFluidUnits(float partialTicks) {
        int renderedFluids = 0;
        float totalUnits = 0;

        for (TankSegment tankSegment : inputTank.getTanks()) {
            if (tankSegment.getRenderedFluid().isEmpty())
                continue;
            float units = tankSegment.getTotalUnits(partialTicks);
            if (units < 1)
                continue;
            totalUnits += units;
            renderedFluids++;
        }
        // lazy copy and paste but works, is fast and is fast
        for (TankSegment tankSegment : outputTank.getTanks()) {
            if (tankSegment.getRenderedFluid().isEmpty())
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

    public boolean isEmpty() {
        return inputInventory.isEmpty() && inputTank.isEmpty() && outputInventory.isEmpty() && outputTank.isEmpty();
    }

    public SmartInventory getInputInventory() {
        return inputInventory;
    }

    protected Recipe<?> getMatchingRecipe() {
        if (isEmpty())
            return null;

        return RecipeFinder.get(FREEZING_RECIPES_KEY, level, this::matchStaticFilters)
                .stream()
                .filter(this::matchFreezingRecipe)
                .map(RecipeHolder::value)
                .min((r1, r2) -> r2.getIngredients().size() - r1.getIngredients().size())
                .orElse(null);
    }

    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
        return recipe.value().getType() == NorthstarRecipeTypes.FREEZING.getType();
    }

    protected boolean matchFreezingRecipe(RecipeHolder<? extends Recipe<?>> recipe) {
        if (recipe == null)
            return false;
        return FreezingRecipe.match(this, recipe.value());
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        inputInventory.deserializeNBT(registries, compound.getCompound("InputItems"));
        outputInventory.deserializeNBT(registries, compound.getCompound("OutputItems"));

        if (!clientPacket)
            return;

        NBTHelper.iterateCompoundList(compound.getList("VisualizedItems", Tag.TAG_COMPOUND),
                c -> visualizedOutputItems.add(IntAttached.with(OUTPUT_ANIMATION_TIME, ItemStack.parseOptional(registries, c))));
        NBTHelper.iterateCompoundList(compound.getList("VisualizedFluids", Tag.TAG_COMPOUND),
                c -> visualizedOutputFluids.add(IntAttached.with(OUTPUT_ANIMATION_TIME, FluidStack.parseOptional(registries, c))));
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.put("InputItems", inputInventory.serializeNBT(registries));
        compound.put("OutputItems", outputInventory.serializeNBT(registries));

        if (!clientPacket)
            return;

        compound.put("VisualizedItems", NBTHelper.writeCompoundList(visualizedOutputItems, ia -> (CompoundTag) ia.getValue().save(registries)));
        compound.put("VisualizedFluids", NBTHelper.writeCompoundList(visualizedOutputFluids, ia -> (CompoundTag) ia.getValue().save(registries, new CompoundTag())));
        visualizedOutputItems.clear();
        visualizedOutputFluids.clear();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        NorthstarLang.translate("gui.goggles.ice_box_contents")
                .forGoggles(tooltip);

        NorthstarLang.translate("gui.goggles.generic.temperature")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        NorthstarLang.temperature(NorthstarTemperature.getTemperatureAt(level, worldPosition))
                .forGoggles(tooltip, 1);

        IItemHandlerModifiable items = itemCapability;
        IFluidHandler fluids = fluidCapability;

        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack stackInSlot = items.getStackInSlot(i);
            if (stackInSlot.isEmpty())
                continue;
            CreateLang.text("")
                    .add(Component.translatable(stackInSlot.getDescriptionId())
                            .withStyle(ChatFormatting.GRAY))
                    .add(CreateLang.text(" x" + stackInSlot.getCount())
                            .style(ChatFormatting.GREEN))
                    .forGoggles(tooltip, 1);
        }
        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
        for (int b = 0; b < fluids.getTanks(); b++) {
            FluidStack stackInSlot = fluids.getFluidInTank(b);
            if (stackInSlot.isEmpty())
                continue;
            if (!stackInSlot.getFluid().getFluidType().isAir()) {
                CreateLang.fluidName(stackInSlot)
                        .style(ChatFormatting.GRAY)
                        .forGoggles(tooltip);
                CreateLang.builder()
                        .add(CreateLang.number(stackInSlot.getAmount())
                                .add(mb)
                                .style(ChatFormatting.GOLD))
                        .text(ChatFormatting.GRAY, " / ")
                        .add(CreateLang.number(fluids.getTankCapacity(b))
                                .add(mb)
                                .style(ChatFormatting.DARK_GRAY))
                        .forGoggles(tooltip, 1);
            }
        }

        return true;
    }

    private static class IceBoxValueBox extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 12, 16.05);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis().isHorizontal();
        }

    }

}
