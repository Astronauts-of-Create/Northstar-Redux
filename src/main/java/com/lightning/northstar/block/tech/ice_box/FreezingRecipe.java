package com.lightning.northstar.block.tech.ice_box;

import com.lightning.northstar.item.NorthstarRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FreezingRecipe extends ProcessingRecipe<RecipeInput, FreezingRecipe.Params> {

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
        IFluidHandler availableFluids = icebox.inputTank.getCapability();

        List<ItemStack> recipeOutputItems = new ArrayList<>();
        List<FluidStack> recipeOutputFluids = new ArrayList<>();

        List<Ingredient> ingredients = new LinkedList<>(recipe.getIngredients());
        List<SizedFluidIngredient> fluidIngredients = recipe instanceof ProcessingRecipe<?, ?> r ? r.getFluidIngredients() : List.of();

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
            for (SizedFluidIngredient fluidIngredient : fluidIngredients) {
                int amountRequired = fluidIngredient.amount();

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
                    CraftingInput remainderInput = new DummyCraftingContainer(availableItems, extractedItemsFromSlot)
                            .asCraftInput();

                    recipeOutputItems.addAll(r.rollResults(icebox.getLevel().random));
                    recipeOutputFluids.addAll(r.getFluidResults());
                    recipeOutputItems.addAll(r.getRemainingItems(remainderInput));
                }
            }

            if (!icebox.acceptOutputs(recipeOutputItems, recipeOutputFluids, simulate))
                return false;
        }

        return true;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Serializer implements RecipeSerializer<FreezingRecipe> {
        private static final MapCodec<FreezingRecipe> CODEC = ProcessingRecipe.codec(FreezingRecipe::new, Params.CODEC);
        private static final StreamCodec<RegistryFriendlyByteBuf, FreezingRecipe> STREAMED_CODEC = ProcessingRecipe
                .streamCodec(FreezingRecipe::new, Params.STREAM_CODEC);

        @Override
        public MapCodec<FreezingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FreezingRecipe> streamCodec() {
            return STREAMED_CODEC;
        }
    }

    public static class Params extends ProcessingRecipeParams {
        public static MapCodec<Params> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                codec(Params::new).forGetter(Function.identity()),
                Codec.INT.optionalFieldOf("minTemperature", Integer.MIN_VALUE).forGetter(p -> p.minTemperature),
                Codec.INT.optionalFieldOf("maxTemperature", Integer.MAX_VALUE).forGetter(p -> p.maxTemperature)
        ).apply(instance, (params, minTemperature, maxTemperature) -> {
            params.minTemperature = minTemperature;
            params.maxTemperature = maxTemperature;
            return params;
        }));
        public static StreamCodec<RegistryFriendlyByteBuf, Params> STREAM_CODEC = streamCodec(Params::new);

        protected int minTemperature = Integer.MIN_VALUE;
        protected int maxTemperature = Integer.MAX_VALUE;

        @Override
        protected void encode(RegistryFriendlyByteBuf buffer) {
            super.encode(buffer);
            buffer.writeVarInt(minTemperature);
            buffer.writeVarInt(maxTemperature);
        }

        @Override
        protected void decode(RegistryFriendlyByteBuf buffer) {
            super.decode(buffer);
            minTemperature = buffer.readVarInt();
            maxTemperature = buffer.readVarInt();
        }
    }

    protected int minTemperature;
    protected int maxTemperature;

    public FreezingRecipe(Params params) {
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
    public boolean matches(RecipeInput inv, @Nonnull Level worldIn) {
        return false;
    }

}
