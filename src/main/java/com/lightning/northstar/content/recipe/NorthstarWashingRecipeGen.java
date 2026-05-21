package com.lightning.northstar.content.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarItems;
import com.simibubi.create.api.data.recipe.WashingRecipeGen;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

public class NorthstarWashingRecipeGen extends WashingRecipeGen {

    GeneratedRecipe
            $ = null,

    RAW_TUNGSTEN = create(() -> NorthstarItems.CRUSHED_RAW_TUNGSTEN,
            b -> b.output(NorthstarItems.TUNGSTEN_NUGGET, 9)
                    .output(0.75f, Items.QUARTZ));

    public NorthstarWashingRecipeGen(PackOutput output) {
        super(output, Northstar.MOD_ID);
    }


}
