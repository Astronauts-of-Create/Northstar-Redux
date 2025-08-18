package com.lightning.northstar.block.tech.electrolysis_machine;

import com.lightning.northstar.item.NorthstarRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public class ElectrolysisRecipe extends StandardProcessingRecipe<RecipeWrapper> {

    protected ElectrolysisRecipe(IRecipeTypeInfo type, ProcessingRecipeParams params) {
        super(type, params);
    }

    public ElectrolysisRecipe(ProcessingRecipeParams params) {
        this(NorthstarRecipeTypes.ELECTROLYSIS, params);
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
    public boolean matches(RecipeWrapper pContainer, Level pLevel) {
        return true;
    }

}
