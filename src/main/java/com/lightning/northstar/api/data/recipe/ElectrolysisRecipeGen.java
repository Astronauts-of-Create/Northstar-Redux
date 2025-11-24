package com.lightning.northstar.api.data.recipe;

import com.lightning.northstar.content.NorthstarRecipeTypes;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.data.PackOutput;

public abstract class ElectrolysisRecipeGen extends ProcessingRecipeGen {

    public ElectrolysisRecipeGen(PackOutput generator, String defaultNamespace) {
        super(generator, defaultNamespace);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return NorthstarRecipeTypes.ELECTROLYSIS;
    }

}
