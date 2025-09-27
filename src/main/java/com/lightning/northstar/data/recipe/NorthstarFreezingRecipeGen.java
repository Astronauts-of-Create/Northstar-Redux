package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.ice_box.FreezingRecipeGen;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class NorthstarFreezingRecipeGen extends FreezingRecipeGen {

    GeneratedRecipe
            $ = null,

    ICE = create("water",
            b -> b.colderThan(0)
                    .duration(100)
                    .require(FluidTags.WATER, 2000)
                    .output(Items.ICE)),

    LIQUID_HYDROGEN = createGas("hydrogen", -253,
            NorthstarFluidTags.C_LIQUID_HYDROGEN.tag, NorthstarFluids.LIQUID_HYDROGEN,
            NorthstarFluidTags.C_HYDROGEN.tag, NorthstarFluids.HYDROGEN),

    LIQUID_OXYGEN = createGas("oxygen", -185,
            NorthstarFluidTags.C_LIQUID_OXYGEN.tag, NorthstarFluids.LIQUID_OXYGEN,
            NorthstarFluidTags.C_OXYGEN.tag, NorthstarFluids.OXYGEN),

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

    public NorthstarFreezingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Northstar.MOD_ID);
    }

}
