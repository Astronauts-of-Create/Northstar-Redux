package com.lightning.northstar.data.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lightning.northstar.Northstar;
import com.lightning.northstar.api.data.recipe.StandardRecipeGen;
import com.lightning.northstar.content.NorthstarFluids;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NorthstarCreateAdditionLiquidBurningRecipeGen extends StandardRecipeGen {

    GeneratedRecipe
            $ = null,

    BIOFUEL = create("biofuel", NorthstarFluids.BIOFUEL.get(), false),

    HYDROGEN = create("hydrogen", NorthstarFluids.HYDROGEN.get(), false),

    LIQUID_HYDROGEN = create("liquid_hydrogen", NorthstarFluids.LIQUID_HYDROGEN.get(), true),

    METHANE = create("methane", NorthstarFluids.METHANE.get(), false),

    HYDROCARBON = create("hydrocarbon", NorthstarFluids.HYDROCARBON.get(), false);

    public NorthstarCreateAdditionLiquidBurningRecipeGen(PackOutput output) {
        super(output, Northstar.MOD_ID);
    }

    @Override
    public @NotNull String getName() {
        return Northstar.MOD_ID + "'s create addition liquid burning recipes";
    }

    public GeneratedRecipe create(String name, Fluid fluid, boolean superheated) {
        // values are the same as they would be for buckets, see somewhere inside BlazeBurnerBlockEntity
        return create(name, fluid, superheated ? 3200 : 1600, superheated);
    }

    public GeneratedRecipe create(String name, Fluid fluid, int burnTime, boolean superheated) {
        // https://github.com/mrh0/createaddition/blob/1.20.1/src/main/java/com/mrh0/createaddition/recipe/liquid_burning/LiquidBurningRecipeSerializer.java
        return register(consumer -> consumer.accept(new FinishedRecipe() {
            @Override
            public @NotNull JsonObject serializeRecipe() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", "createaddition:liquid_burning");
                serializeRecipeData(jsonObject);
                return jsonObject;
            }

            @Override
            public void serializeRecipeData(@NotNull JsonObject json) {
                json.add("input", FluidIngredient.fromFluid(fluid, 1000).serialize());
                json.addProperty("burnTime", burnTime);
                if (superheated)
                    json.addProperty("superheated", true);

                JsonArray conditions = new JsonArray();
                conditions.add(CraftingHelper.serialize(new ModLoadedCondition("createaddition")));
                json.add("conditions", conditions);
            }

            @Override
            public @NotNull ResourceLocation getId() {
                return Northstar.asResource("create_addition/liquid_burning/" + name);
            }

            @Override
            public @NotNull RecipeSerializer<?> getType() {
                throw new RuntimeException("Unsupported"); // Only used by #serializeRecipe
            }

            @Override
            public @Nullable JsonObject serializeAdvancement() {
                return null;
            }

            @Override
            public @Nullable ResourceLocation getAdvancementId() {
                return null;
            }
        }));
    }

}
