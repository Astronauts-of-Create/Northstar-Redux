package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarItems;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.data.recipe.PolishingRecipeGen;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class NorthstarPolishingRecipeGen extends PolishingRecipeGen {

    GeneratedRecipe
    $ = null,

    POLISHED_AMETHYST = create(() -> Items.AMETHYST_SHARD,
            b -> b.output(NorthstarItems.POLISHED_AMETHYST)),

    POLISHED_DIAMOND = create(() -> Items.DIAMOND,
            b -> b.output(NorthstarItems.POLISHED_DIAMOND)),

    POLISHED_LUNAR_SAPPHIRE = create(() -> NorthstarItems.LUNAR_SAPPHIRE_SHARD,
            b -> b.output(NorthstarItems.POLISHED_LUNAR_SAPPHIRE));

    public NorthstarPolishingRecipeGen(PackOutput output) {
        super(output);
    }

    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(String name, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return super.createWithDeferredId(() -> Northstar.asResource(name), transform);
    }

    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(Supplier<ItemLike> singleIngredient, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return super.create(Northstar.MOD_ID, singleIngredient, transform);
    }

}
