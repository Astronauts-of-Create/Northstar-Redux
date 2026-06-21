package com.lightning.northstar.content.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.api.data.recipe.EngravingRecipeGen;
import com.lightning.northstar.content.NorthstarItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class NorthstarEngravingRecipeGen extends EngravingRecipeGen {

    GeneratedRecipe
            $ = null,

    ENRICHED_GLOWSTONE_ORE = create(() -> NorthstarItems.RAW_GLOWSTONE_ORE,
            b -> b.duration(100)
                    .output(NorthstarItems.ENRICHED_GLOWSTONE_ORE));

    public NorthstarEngravingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Northstar.MOD_ID);
    }

}
