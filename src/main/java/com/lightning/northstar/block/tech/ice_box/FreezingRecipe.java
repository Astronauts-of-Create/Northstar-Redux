package com.lightning.northstar.block.tech.ice_box;

import com.google.gson.JsonObject;
import com.lightning.northstar.item.NorthstarRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FreezingRecipe extends ProcessingRecipe<Container> {

    public static boolean match(IceBoxBlockEntity icebox, Recipe<?> recipe) {
        if (!(recipe instanceof FreezingRecipe r)) {
            return false;
        }

        boolean matchItem = r.results.isEmpty() || icebox.filtering.test(r.results.get(0).getStack());
        boolean matchFluid = r.fluidResults.isEmpty() || icebox.filtering.test(r.fluidResults.get(0));
        if (!matchItem && !matchFluid) {
            return false;
        }

        return apply(icebox, recipe, true);
    }

    public static boolean apply(IceBoxBlockEntity icebox, Recipe<?> recipe) {
        return apply(icebox, recipe, false);
    }

    private static boolean apply(IceBoxBlockEntity icebox, Recipe<?> recipe, boolean test) {
        IItemHandler availableItems = icebox.inputInventory;
        IFluidHandler availableFluids = icebox.inputTank.getCapability().orElse(null);

        List<ItemStack> recipeOutputItems = new ArrayList<>();
        List<FluidStack> recipeOutputFluids = new ArrayList<>();

        List<Ingredient> ingredients = new LinkedList<>(recipe.getIngredients());
        List<FluidIngredient> fluidIngredients = recipe instanceof ProcessingRecipe<?> r ? r.getFluidIngredients() : List.of();

        for (boolean simulate : Iterate.trueAndFalse) {
            if (!simulate && test)
                return true;

            int[] extractedItemsFromSlot = new int[availableItems.getSlots()];
            int[] extractedFluidsFromTank = new int[availableFluids.getTanks()];

            Ingredients:
            for (Ingredient ingredient : ingredients) {
                for (int slot = 0; slot < availableItems.getSlots(); slot++) {
                    if (simulate && availableItems.getStackInSlot(slot).getCount() <= extractedItemsFromSlot[slot])
                        continue;
                    ItemStack extracted = availableItems.extractItem(slot, 1, true);
                    if (!ingredient.test(extracted))
                        continue;
                    if (!simulate)
                        availableItems.extractItem(slot, 1, false);
                    extractedItemsFromSlot[slot]++;
                    continue Ingredients;
                }

                // something wasn't found
                return false;
            }

            boolean fluidsAffected = false;
            FluidIngredients:
            for (FluidIngredient fluidIngredient : fluidIngredients) {
                int amountRequired = fluidIngredient.getRequiredAmount();

                for (int tank = 0; tank < availableFluids.getTanks(); tank++) {
                    FluidStack fluidStack = availableFluids.getFluidInTank(tank);
                    if (simulate && fluidStack.getAmount() <= extractedFluidsFromTank[tank])
                        continue;
                    if (!fluidIngredient.test(fluidStack))
                        continue;
                    int drainedAmount = Math.min(amountRequired, fluidStack.getAmount());
                    if (!simulate) {
                        fluidStack.shrink(drainedAmount);
                        fluidsAffected = true;
                    }
                    amountRequired -= drainedAmount;
                    if (amountRequired != 0)
                        continue;
                    extractedFluidsFromTank[tank] += drainedAmount;
                    continue FluidIngredients;
                }

                // something wasn't found
                return false;
            }

            if (fluidsAffected && !simulate) {
                icebox.getBehaviour(SmartFluidTankBehaviour.INPUT).forEach(TankSegment::onFluidStackChanged);
                icebox.getBehaviour(SmartFluidTankBehaviour.OUTPUT).forEach(TankSegment::onFluidStackChanged);
            }

            if (simulate) {
                if (recipe instanceof FreezingRecipe r) {
                    recipeOutputItems.addAll(r.rollResults());
                    recipeOutputFluids.addAll(r.getFluidResults());
                    recipeOutputItems.addAll(r.getRemainingItems(icebox.getInputInventory()));
                } else {
                    recipeOutputItems.add(recipe.getResultItem(icebox.getLevel().registryAccess()));

                    if (recipe instanceof CraftingRecipe craftingRecipe) {
                        recipeOutputItems.addAll(craftingRecipe.getRemainingItems(new DummyCraftingContainer(availableItems, extractedItemsFromSlot)));
                    }
                }
            }

            if (!icebox.acceptOutputs(recipeOutputItems, recipeOutputFluids, simulate))
                return false;
        }

        return true;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Serializer extends ProcessingRecipeSerializer<FreezingRecipe> {
        public Serializer() {
            super(FreezingRecipe::new);
        }

        @Override
        protected FreezingRecipe readFromBuffer(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            FreezingRecipe recipe = super.readFromBuffer(recipeId, buffer);
            recipe.minTemperature = buffer.readVarInt();
            recipe.maxTemperature = buffer.readVarInt();
            return recipe;
        }

        @Override
        protected void writeToBuffer(FriendlyByteBuf buffer, FreezingRecipe recipe) {
            super.writeToBuffer(buffer, recipe);
            buffer.writeVarInt(recipe.minTemperature);
            buffer.writeVarInt(recipe.maxTemperature);

        }

        @Override
        protected FreezingRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
            FreezingRecipe recipe = super.readFromJson(recipeId, json);
            recipe.minTemperature = GsonHelper.getAsInt(json, "minTemperature", Integer.MIN_VALUE);
            recipe.maxTemperature = GsonHelper.getAsInt(json, "maxTemperature", Integer.MAX_VALUE);
            return recipe;
        }

        @Override
        protected void writeToJson(JsonObject json, FreezingRecipe recipe) {
            super.writeToJson(json, recipe);
            if (recipe.minTemperature != Integer.MIN_VALUE)
                json.addProperty("minTemperature", recipe.minTemperature);
            if (recipe.maxTemperature != Integer.MAX_VALUE)
                json.addProperty("maxTemperature", recipe.maxTemperature);
        }
    }

    public static class Params extends ProcessingRecipeParams {
        protected int minTemperature = Integer.MIN_VALUE;
        protected int maxTemperature = Integer.MAX_VALUE;

        public Params(ResourceLocation id) {
            super(id);
        }
    }

    protected int minTemperature;
    protected int maxTemperature;

    public FreezingRecipe(ProcessingRecipeParams params) {
        super(NorthstarRecipeTypes.FREEZING, params);
        if (params instanceof Params p) {
            this.minTemperature = p.minTemperature;
            this.maxTemperature = p.maxTemperature;
        }
    }

    public boolean isTemperatureWithinRange(float temperature) {
        return temperature >= minTemperature && temperature <= maxTemperature;
    }

    @Override
    protected int getMaxInputCount() {
        return 9;
    }

    @Override
    protected int getMaxOutputCount() {
        return 4;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 2;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 2;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    public int getMinTemperature() {
        return minTemperature;
    }

    public int getMaxTemperature() {
        return maxTemperature;
    }

    @Override
    public boolean matches(Container inv, @Nonnull Level worldIn) {
        return false;
    }

}
