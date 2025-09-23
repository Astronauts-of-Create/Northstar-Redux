package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.api.data.recipe.ElectrolysisRecipeGen;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class NorthstarElectrolysisRecipeGen extends ElectrolysisRecipeGen {

    GeneratedRecipe
            $ = null,

    BRINE = create("brine",
            b -> b.require(NorthstarFluidTags.C_BRINE.tag, 10)
                    .output(NorthstarFluids.CHLORINE.get(), 2)
                    .output(NorthstarFluids.SODIUM.get(), 2)),

    WATER = create("water",
            b -> b.require(FluidTags.WATER, 10)
                    .output(NorthstarFluids.OXYGEN.get(), 3)
                    .output(NorthstarFluids.HYDROGEN.get(), 6));

    public NorthstarElectrolysisRecipeGen(PackOutput generator) {
        super(generator, Northstar.MOD_ID);
    }

    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(String name, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return super.createWithDeferredId(() -> Northstar.asResource(name), transform);
    }

    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(Supplier<ItemLike> singleIngredient, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return super.create(Northstar.MOD_ID, singleIngredient, transform);
    }

}
