package com.lightning.northstar.block.tech.ice_box;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.item.NorthstarRecipeTypes;
import com.lightning.northstar.world.TemperatureStuff;
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
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.data.Couple;
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
import net.minecraft.world.item.crafting.Ingredient;
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
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class IceBoxBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    private boolean contentsChanged;

    public IceBoxInventory inputInventory;
    public SmartInventory outputInventory;
    public SmartFluidTankBehaviour inputTank;
    protected SmartFluidTankBehaviour outputTank;
    private FilteringBehaviour filtering;

    protected IItemHandlerModifiable itemCapability;
    protected IFluidHandler fluidCapability;

    private Couple<SmartInventory> invs;
    private Couple<SmartFluidTankBehaviour> tanks;

    int recipeBackupCheck;
    private RecipeWrapper wrapper;
    protected Recipe<?> currentRecipe;
    protected int recipeTemp;
    private static final Object freezingRecipesKey = new Object();

    public static final int OUTPUT_ANIMATION_TIME = 10;
    List<IntAttached<ItemStack>> visualizedOutputItems;
    List<IntAttached<FluidStack>> visualizedOutputFluids;

    public IceBoxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inputInventory = new IceBoxInventory(9, this);
        inputInventory.whenContentsChanged($ -> contentsChanged = true);
        outputInventory = new IceBoxInventory(9, this).forbidInsertion()
                .withMaxStackSize(64);
        contentsChanged = true;
        itemCapability = new CombinedInvWrapper(inputInventory, outputInventory);

        invs = Couple.create(inputInventory, outputInventory);
        tanks = Couple.create(inputTank, outputTank);

        visualizedOutputFluids = Collections.synchronizedList(new ArrayList<>());
        visualizedOutputItems = Collections.synchronizedList(new ArrayList<>());
        recipeBackupCheck = 20;
        wrapper = new RecipeWrapper(inputInventory);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, NorthstarBlockEntityTypes.ICE_BOX.get(), (be, face) -> be.itemCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, NorthstarBlockEntityTypes.ICE_BOX.get(), (be, face) -> be.fluidCapability);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        inputInventory.deserializeNBT(registries, tag.getCompound("InputItems"));
        outputInventory.deserializeNBT(registries, tag.getCompound("OutputItems"));

        if (!clientPacket)
            return;

        NBTHelper.iterateCompoundList(tag.getList("VisualizedItems", Tag.TAG_COMPOUND),
                c -> visualizedOutputItems.add(IntAttached.with(OUTPUT_ANIMATION_TIME, ItemStack.parseOptional(registries, c))));
        NBTHelper.iterateCompoundList(tag.getList("VisualizedFluids", Tag.TAG_COMPOUND),
                c -> visualizedOutputFluids.add(IntAttached.with(OUTPUT_ANIMATION_TIME, FluidStack.parseOptional(registries, c))));
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        tag.put("InputItems", inputInventory.serializeNBT(registries));
        tag.put("OutputItems", outputInventory.serializeNBT(registries));

        if (!clientPacket)
            return;

        tag.put("VisualizedItems", NBTHelper.writeCompoundList(visualizedOutputItems, ia -> (CompoundTag) ia.getValue().save(registries)));
        tag.put("VisualizedFluids", NBTHelper.writeCompoundList(visualizedOutputFluids, ia -> (CompoundTag) ia.getValue().save(registries)));
        visualizedOutputItems.clear();
        visualizedOutputFluids.clear();
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

            BlockEntity be = level.getBlockEntity(worldPosition.below()
                    .relative(direction));

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


    protected boolean updateRecipe() {
        if (level == null || level.isClientSide)
            return true;

        List<Recipe<?>> recipes = getMatchingRecipes();
        if (recipes.isEmpty())
            return true;
        currentRecipe = recipes.get(0);
//        System.out.println(currentRecipe.getResultItem());
        sendData();
        return true;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));
        filtering = new FilteringBehaviour(this, new IceBoxValueBox()).withCallback(newFilter -> contentsChanged = true)
                .forRecipes();
        behaviours.add(filtering);

        inputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 2, 1000, true)
                .whenFluidUpdates(() -> contentsChanged = true);
        outputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 2, 1000, true)
                .whenFluidUpdates(() -> contentsChanged = true)
                .forbidInsertion();
        behaviours.add(inputTank);
        behaviours.add(outputTank);

        fluidCapability = new CombinedTankWrapper(inputTank.getCapability(), outputTank.getCapability());
    }

    public float getTotalFluidUnits(float partialTicks) {
        int renderedFluids = 0;
        float totalUnits = 0;

        SmartFluidTankBehaviour behaviour = inputTank;
        if (behaviour == null)
            return 0;
        for (TankSegment tankSegment : behaviour.getTanks()) {
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


    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide)
            return;
        updateRecipe();
        int temperature = TemperatureStuff.getTemp(worldPosition, level);
        float freezespeed = 0;
        if (temperature <= 0) {
            freezespeed = Math.abs(temperature) / 15;
        }
//        System.out.println("Remaining time: " + inventory.remainingTime);

        if (inputInventory.remainingTime <= 0) {
            inputInventory.remainingTime = 500;
        }
//        if(inventory.remainingTime > 0) {
        inputInventory.remainingTime -= freezespeed;
//        System.out.println(inputInventory.remainingTime);
//            System.out.println("Applied Recipe: " + inventory.appliedRecipe);

        List<Recipe<?>> recipes = getMatchingRecipes();
        if (!recipes.isEmpty())
            currentRecipe = recipes.get(0);
        if (currentRecipe != null && currentRecipe instanceof FreezingRecipe) {
            //using processing duration as a stand in for temperature because I can't be bothered to make that it's own thing
            recipeTemp = ((FreezingRecipe) currentRecipe).getProcessingDuration() > 0 ? ((FreezingRecipe) currentRecipe).getProcessingDuration() : 0;
        }
        if (temperature <= -recipeTemp) {
            if (inputInventory.remainingTime < 20) {
                applyFreezingRecipe();
                if (!inputInventory.appliedRecipe) {
                    applyRecipe();
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 16);
                }
                inputInventory.remainingTime = 500;
            }
        }
//        }
        IItemHandlerModifiable items = itemCapability;
        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack itemStack = items.getStackInSlot(i);
            visualizedOutputItems.add(IntAttached.withZero(itemStack));
        }
    }

    protected void applyFreezingRecipe() {
        if (currentRecipe == null)
            return;
        if (!FreezingRecipe.apply(this, currentRecipe))
            return;
        inputTank.sendDataImmediately();
        outputTank.sendDataImmediately();
        if (matchFreezingRecipe(currentRecipe)) {
            sendData();
        }
        notifyChangeOfContents();
    }

    private void applyRecipe() {
        List<ItemStack> list = new ArrayList<>();
        if (currentRecipe != null && currentRecipe instanceof FreezingRecipe) {
            int rolls = inputInventory.getStackInSlot(0)
                    .getCount();
            inputInventory.clearSlot(0);
            for (int roll = 0; roll < rolls; roll++) {
                List<ItemStack> rolledResults = ((FreezingRecipe) currentRecipe)
                        .rollResults();
                for (ItemStack stack : rolledResults) {
                    ItemHelper.addToList(stack, list);
                }
            }
            inputInventory.appliedRecipe = true;
            for (int slot = 0; slot < list.size() && slot + 1 < outputInventory.getSlots(); slot++)
                outputInventory.setStackInSlot(slot + 1, list.get(slot));
        }

    }


    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inputInventory);
    }

    @Override
    public void notifyUpdate() {
        super.notifyUpdate();
    }

    public void notifyChangeOfContents() {
        contentsChanged = true;
    }

    public FilteringBehaviour getFilter() {
        return filtering;
    }

    public boolean isEmpty() {
        return inputInventory.isEmpty() && inputTank.isEmpty() && outputInventory.isEmpty() && outputTank.isEmpty();
    }


    @Override
    public void lazyTick() {
        super.lazyTick();

        if (!level.isClientSide) {
            if (recipeBackupCheck-- > 0)
                return;
            recipeBackupCheck = 20;
            if (isEmpty())
                return;
            notifyChangeOfContents();
            return;
        }
    }

    public SmartInventory getInputInventory() {
        return inputInventory;

    }

    public void onWrenched(Direction face) {
        //bruh
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("gui.goggles.ice_box_contents")
                .forGoggles(tooltip);

        IItemHandlerModifiable items = itemCapability;
        IFluidHandler fluids = fluidCapability;
        boolean isEmpty = true;

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
            isEmpty = false;
        }
        if (fluids != null) {
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
                isEmpty = false;
            }
        }

        if (isEmpty)
            tooltip.remove(0);

        return true;
    }

    private Recipe<?> checkForIngredients(List<Recipe<?>> list) {
        for (Recipe<?> recipe : list) {
            if (!(recipe instanceof FreezingRecipe))
                continue;
            int ingredAmount = 0;
            int requiredAmount = 0;
            int fluidamount = 0;
            int requiredFluid = 0;
            for (Ingredient ingred : ((FreezingRecipe) recipe).getIngredients()) {
                for (ItemStack item : ingred.getItems()) {
                    for (int i = 0; i > inputInventory.getContainerSize(); i++) {
                        if (inputInventory.getItem(i) != null && inputInventory.getItem(i).getCount() >= item.getCount()) {
                            ingredAmount++;
                        }
                    }
                    requiredAmount += item.getCount();
                }
            }
            for (FluidIngredient ingred : ((FreezingRecipe) recipe).getFluidIngredients()) {
                for (FluidStack item : ingred.getMatchingFluidStacks()) {
                    for (int i = 0; i > inputTank.getTanks().length; i++) {
                        if (inputTank.getPrimaryHandler().getFluid() != null && inputTank.getPrimaryHandler().getFluid().getFluid() == item.getFluid()
                                && inputTank.getPrimaryHandler().getFluidAmount() > item.getAmount()) {
                            fluidamount += item.getAmount();
                        }
                    }
                    requiredFluid += item.getAmount();
                }
            }
            if (ingredAmount >= requiredAmount && fluidamount >= requiredFluid) {
                return recipe;
            }
        }
        return null;
    }

    private void itemInserted(ItemStack stack) {
        List<Recipe<?>> recipes = getMatchingRecipes();
        if (!recipes.isEmpty())
            currentRecipe = recipes.get(0);
//        System.out.println("Searching for recipe!");;
        inputInventory.remainingTime = 500;
        if (currentRecipe != null)
//            System.out.println("RECIPE DETECTED!  " + currentRecipe.getResultItem());
            inputInventory.appliedRecipe = false;
    }

    protected List<Recipe<?>> getMatchingRecipes() {
        List<Recipe<?>> matchingRecipes = getMatchingRecipes2();


        if (isEmpty())
            return matchingRecipes;

        IItemHandler availableItems = itemCapability;
        if (availableItems == null)
            return matchingRecipes;

        return matchingRecipes;
    }

    protected List<Recipe<?>> getMatchingRecipes2() {
        if (Optional.of(this).map(IceBoxBlockEntity::isEmpty)
                .orElse(true))
            return new ArrayList<>();

        return RecipeFinder.get(getRecipeCacheKey(), level, this::matchStaticFilters)
                .stream()
                .map(RecipeHolder::value)
                .filter(this::matchFreezingRecipe)
                .sorted((r1, r2) -> r2.getIngredients()
                        .size()
                        - r1.getIngredients()
                        .size())
                .collect(Collectors.toList());
    }


    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
        return recipe.value().getType() == NorthstarRecipeTypes.FREEZING.getType();
    }

    protected Object getRecipeCacheKey() {
        return freezingRecipesKey;
    }

    protected boolean matchFreezingRecipe(Recipe<?> recipe) {
        if (recipe == null)
            return false;
        return FreezingRecipe.match(this, recipe);
    }


    static class IceBoxValueBox extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 12, 16.05);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis()
                    .isHorizontal();
        }

    }

}
