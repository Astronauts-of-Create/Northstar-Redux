package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.ice_box.FreezingRecipeGen;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;

public class NorthstarFreezingRecipeGen extends FreezingRecipeGen {

    GeneratedRecipe
            $ = null,

    ICE = create("water",
            b -> b.colderThan(0)
                    .duration(100)
                    .require(FluidTags.WATER, 2000)
                    .output(Items.ICE)),

    LIQUID_HYDROGEN = create("liquid_hydrogen",
            b -> b.colderThan(-253)
                    .duration(100)
                    .require(NorthstarFluidTags.C_HYDROGEN.tag, 1000)
                    .output(NorthstarFluids.LIQUID_HYDROGEN.get(), 1000)),

    LIQUID_OXYGEN = create("liquid_oxygen",
            b -> b.colderThan(-185)
                    .duration(100)
                    .require(NorthstarFluidTags.C_OXYGEN.tag, 1000)
                    .output(NorthstarFluids.LIQUID_OXYGEN.get(), 1000)),

    METHANE_ICE = create("methane_ice",
            b -> b.colderThan(-182)
                    .duration(100)
                    .require(NorthstarFluidTags.C_METHANE.tag, 2000)
                    .output(NorthstarBlocks.METHANE_ICE)),

    VANILLA_ICE_CREAM = create("vanilla_ice_cream",
            b -> b.withinTemperature(-25, -15)
                    .duration(100)
                    .require(NorthstarFluidTags.C_MILK.tag, 200)
                    .require(Items.SUGAR)
                    .output(NorthstarFluids.VANILLA_ICE_CREAM.get(), 200));

    public NorthstarFreezingRecipeGen(PackOutput generator) {
        super(generator, Northstar.MOD_ID);
    }

}
