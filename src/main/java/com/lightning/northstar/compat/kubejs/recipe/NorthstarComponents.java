package com.lightning.northstar.compat.kubejs.recipe;

import com.mojang.datafixers.util.Either;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import dev.latvian.mods.kubejs.create.recipe.CreateRecipeComponents;
import dev.latvian.mods.kubejs.create.recipe.ProcessingOutputRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.FluidStackComponent;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ListRecipeComponent;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public interface NorthstarComponents {

    ListRecipeComponent<Either<SizedFluidIngredient, Ingredient>> SIZED_FLUID_OR_ITEM_LIST = CreateRecipeComponents.SIZED_FLUID_INGREDIENT.instance()
            .or(IngredientComponent.INGREDIENT.instance())
            .asList();

    ListRecipeComponent<Either<FluidStack, ProcessingOutput>> FLUID_OR_PROCESSING_OUTPUT_LIST = FluidStackComponent.FLUID_STACK.instance()
            .or(ProcessingOutputRecipeComponent.TYPE.instance())
            .asList();

}
