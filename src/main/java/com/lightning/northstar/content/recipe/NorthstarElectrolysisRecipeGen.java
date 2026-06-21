package com.lightning.northstar.content.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.api.data.recipe.ElectrolysisRecipeGen;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;

import java.util.concurrent.CompletableFuture;

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

    public NorthstarElectrolysisRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Northstar.MOD_ID);
    }

}
