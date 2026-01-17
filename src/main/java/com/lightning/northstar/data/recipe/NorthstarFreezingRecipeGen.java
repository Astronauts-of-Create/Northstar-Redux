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
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class NorthstarFreezingRecipeGen extends FreezingRecipeGen {

    public NorthstarFreezingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Northstar.MOD_ID);

        create("water_freezing", b -> b
                .colderThan(0)
                .duration(100)
                .require(FluidTags.WATER, 1000)
                .output(Items.ICE));

        create("ice_melting", b -> b
                .hotterThan(0)
                .duration(100)
                .require(Items.ICE)
                .output(Fluids.WATER, 1000));

        create("methane_freezing", b -> b
                .colderThan(-182)
                .duration(100)
                .require(NorthstarFluidTags.C_METHANE.tag, 1000)
                .output(NorthstarBlocks.METHANE_ICE));

        create("methane_ice_melting", b -> b
                .hotterThan(-182)
                .duration(100)
                .require(NorthstarBlocks.METHANE_ICE)
                .output(NorthstarFluids.METHANE.get(), 1000));

        createGas("hydrogen", -253,
                NorthstarFluidTags.C_LIQUID_HYDROGEN.tag, NorthstarFluids.LIQUID_HYDROGEN,
                NorthstarFluidTags.C_HYDROGEN.tag, NorthstarFluids.HYDROGEN);

        createGas("oxygen", -185,
                NorthstarFluidTags.C_LIQUID_OXYGEN.tag, NorthstarFluids.LIQUID_OXYGEN,
                NorthstarFluidTags.C_OXYGEN.tag, NorthstarFluids.OXYGEN);

        create("vanilla_ice_cream", b -> b
                .withinTemperature(-25, -15)
                .duration(100)
                .require(Tags.Fluids.MILK, 200)
                .require(Items.SUGAR)
                .output(NorthstarFluids.VANILLA_ICE_CREAM.get(), 200));
    }

}
