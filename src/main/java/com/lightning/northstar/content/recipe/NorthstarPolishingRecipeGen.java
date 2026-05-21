package com.lightning.northstar.content.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarItems;
import com.simibubi.create.api.data.recipe.PolishingRecipeGen;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

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
        super(output, Northstar.MOD_ID);
    }

}
