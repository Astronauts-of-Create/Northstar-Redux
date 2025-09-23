package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.data.recipe.PressingRecipeGen;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class NorthstarPressingRecipeGen extends PressingRecipeGen {

    GeneratedRecipe
            $ = null,

    FLATTENED_DOUGH = create(() -> AllItems.DOUGH,
            b -> b.output(NorthstarItems.FLATTENED_DOUGH)),

    MARTIAN_STEEL_SHEET = create(() -> NorthstarItems.MARTIAN_STEEL,
            b -> b.output(NorthstarItems.MARTIAN_STEEL_SHEET)),

    TITANIUM_SHEET = create(() -> NorthstarItems.TITANIUM_INGOT,
            b -> b.output(NorthstarItems.TITANIUM_SHEET)),

    TUNGSTEN_SHEET = create(() -> NorthstarItems.TUNGSTEN_INGOT,
            b -> b.output(NorthstarItems.TUNGSTEN_SHEET));

    public NorthstarPressingRecipeGen(PackOutput output) {
        super(output);
    }

    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(String name, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return super.createWithDeferredId(() -> Northstar.asResource(name), transform);
    }

    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(Supplier<ItemLike> singleIngredient, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return super.create(Northstar.MOD_ID, singleIngredient, transform);
    }

}
