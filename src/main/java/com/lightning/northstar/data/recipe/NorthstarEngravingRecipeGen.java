package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.api.data.recipe.EngravingRecipeGen;
import com.lightning.northstar.content.NorthstarItems;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class NorthstarEngravingRecipeGen extends EngravingRecipeGen {

    GeneratedRecipe
            $ = null,

    ENRICHED_GLOWSTONE_ORE = create(() -> NorthstarItems.RAW_GLOWSTONE_ORE,
            b -> b.output(NorthstarItems.ENRICHED_GLOWSTONE_ORE));

    public NorthstarEngravingRecipeGen(PackOutput generator) {
        super(generator, Northstar.MOD_ID);
    }

    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(String name, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return super.createWithDeferredId(() -> Northstar.asResource(name), transform);
    }

    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(Supplier<ItemLike> singleIngredient, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return super.create(Northstar.MOD_ID, singleIngredient, transform);
    }

}
