package com.lightning.northstar.data;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarItems;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.data.recipe.WashingRecipeGen;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class NorthstarWashingRecipeGen extends WashingRecipeGen {

    GeneratedRecipe
            $ = null,

    RAW_TUNGSTEN = create(() -> NorthstarItems.CRUSHED_RAW_TUNGSTEN,
            b -> b.output(NorthstarItems.TUNGSTEN_NUGGET, 9)
                    .output(0.75f, Items.QUARTZ));

    public NorthstarWashingRecipeGen(PackOutput output) {
        super(output);
    }

    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(Supplier<ItemLike> singleIngredient, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return super.create(Northstar.MOD_ID, singleIngredient, transform);
    }

}
