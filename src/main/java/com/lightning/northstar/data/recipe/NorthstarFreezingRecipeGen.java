package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.api.data.recipe.FreezingRecipeGen;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

import java.util.concurrent.CompletableFuture;

public class NorthstarFreezingRecipeGen extends FreezingRecipeGen {

    GeneratedRecipe
            $ = null,

    ICE = create("water",
            b -> b.duration(0)
                    .require(FluidTags.WATER, 2000)
                    .output(Items.ICE)),

    LIQUID_HYDROGEN = create("liquid_hydrogen",
            b -> b.duration(253)
                    .require(NorthstarFluidTags.C_HYDROGEN.tag, 1000)
                    .output(NorthstarFluids.LIQUID_HYDROGEN.get(), 1000)),

    LIQUID_OXYGEN = create("liquid_oxygen",
            b -> b.duration(185)
                    .require(NorthstarFluidTags.C_OXYGEN.tag, 1000)
                    .output(NorthstarFluids.LIQUID_OXYGEN.get(), 1000)),

    METHANE_ICE = create("methane_ice",
            b -> b.duration(182)
                    .require(NorthstarFluidTags.C_METHANE.tag, 2000)
                    .output(NorthstarBlocks.METHANE_ICE)),

    VANILLA_ICE_CREAM = create("vanilla_ice_cream",
            b -> b.duration(20)
                    .require(NorthstarFluidTags.C_MILK.tag, 200)
                    .require(Items.SUGAR)
                    .output(NorthstarFluids.VANILLA_ICE_CREAM.get(), 200));

    public NorthstarFreezingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Northstar.MOD_ID);
    }

}
