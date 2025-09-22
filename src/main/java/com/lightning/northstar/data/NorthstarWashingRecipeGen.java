package com.lightning.northstar.data;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarItems;
import com.simibubi.create.api.data.recipe.WashingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class NorthstarWashingRecipeGen extends WashingRecipeGen {

    GeneratedRecipe
            $ = null,

    RAW_TUNGSTEN = create(() -> NorthstarItems.CRUSHED_RAW_TUNGSTEN,
            b -> b.output(NorthstarItems.TUNGSTEN_NUGGET, 9)
                    .output(0.75f, Items.QUARTZ));

    public NorthstarWashingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Northstar.MOD_ID);
    }

}
