package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.api.data.recipe.ElectrolysisRecipeGen;
import com.lightning.northstar.content.NorthstarFluids;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluids;

public class NorthstarElectrolysisRecipeGen extends ElectrolysisRecipeGen {

    GeneratedRecipe
            $ = null,

    CHLORINE = create("chlorine",
            b -> b.require(NorthstarFluids.BRINE.get(), 10)
                    .output(NorthstarFluids.CHLORINE.get(), 7)
                    .output(NorthstarFluids.SODIUM_HYDROXIDE.get(), 1)),

    WATER = create("water",
            b -> b.require(Fluids.WATER, 10)
                    .output(NorthstarFluids.OXYGEN.get(), 7)
                    .output(NorthstarFluids.HYDROGEN.get(), 2));

    public NorthstarElectrolysisRecipeGen(PackOutput generator) {
        super(generator, Northstar.MOD_ID);
    }

}
