package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.lightning.northstar.data.ModCompat;
import com.simibubi.create.api.data.recipe.CompactingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.data.recipe.Mods;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class NorthstarCompactingRecipeGen extends CompactingRecipeGen {

    GeneratedRecipe
            $ = null,

    BIOFUEL_FROM_DRIED_KELP = create("biofuel_from_dried_kelp",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(Items.DRIED_KELP)
                    .require(Items.DRIED_KELP)
                    .require(Items.DRIED_KELP)
                    .require(Items.BONE_MEAL)
                    .output(NorthstarFluids.BIOFUEL.get(), 150)),

    BIOFUEL_FROM_DRY_PLANT_FIBER = create("biofuel_from_dry_plant_fiber",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(NorthstarItems.DRY_PLANT_FIBER)
                    .require(NorthstarItems.DRY_PLANT_FIBER)
                    .require(NorthstarItems.DRY_PLANT_FIBER)
                    .require(NorthstarItems.DRY_PLANT_FIBER)
                    .require(NorthstarItems.DRY_PLANT_FIBER)
                    .require(Items.BONE_MEAL)
                    .output(NorthstarFluids.BIOFUEL.get(), 20)),

    BIOFUEL_MEKANISM = create("biofuel_from_mekanism",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(Mods.MEK, "bio_fuel")
                    // very low but Mekanism biofuel is really easy to get
                    .output(NorthstarFluids.BIOFUEL.get(), 20)
                    .whenModLoaded(Mods.MEK.getId())),

    BRINE_TO_SALT = create("brine_to_salt",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(NorthstarFluidTags.C_BRINE.tag, 750)
                    .output(NorthstarItems.SALT)),

    CARBON_FROM_BIODIESEL = create("carbon_from_biodiesel",
            b -> b.requiresHeat(HeatCondition.SUPERHEATED)
                    .require(NorthstarFluidTags.COMPAT_CDG_BIODIESEL.tag, 1000)
                    .output(NorthstarFluids.CARBON.get(), 500)
                    .whenModLoaded(ModCompat.CDG.getModId())),

    CARBON_FROM_BIOFUEL = create("carbon_from_biofuel",
            b -> b.requiresHeat(HeatCondition.SUPERHEATED)
                    .require(NorthstarFluidTags.C_BIOFUEL.tag, 1000)
                    .output(NorthstarFluids.CARBON.get(), 500)),

    CARBON_FROM_COAL = create("carbon_from_coal",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(Items.COAL)
                    .require(Items.COAL)
                    .require(Items.COAL)
                    .output(NorthstarFluids.CARBON.get(), 1000)),

    CARBON_FROM_MOON_SAND = create("carbon_from_moon_sand",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(NorthstarBlocks.MOON_SAND)
                    .output(NorthstarFluids.CARBON.get(), 100)),

    CARBON_FROM_VENUS_GRAVEL = create("carbon_from_venus_gravel",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(NorthstarBlocks.VENUS_GRAVEL)
                    .output(NorthstarFluids.CARBON.get(), 100)),

    MARTIAN_STEEL_INGOT = create("martian_steel_ingot",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(NorthstarTags.NorthstarItemTags.C_INGOTS_TITANIUM.tag)
                    .require(NorthstarItems.RAW_MARTIAN_IRON_ORE)
                    .require(NorthstarItems.VOLCANIC_ASH)
                    .output(NorthstarItems.MARTIAN_STEEL, 2)),

    SODIUM_FROM_BLOOM_FUNGUS = create("sodium_from_bloom_fungus",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(NorthstarBlocks.BLOOM_FUNGUS_BLOCK)
                    .require(NorthstarBlocks.BLOOM_FUNGUS_BLOCK)
                    .require(NorthstarBlocks.BLOOM_FUNGUS_BLOCK)
                    .output(NorthstarFluids.SODIUM.get(), 1000)),

    SODIUM_FROM_PLATE_FUNGUS = create("sodium_from_plate_fungus",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(NorthstarBlocks.PLATE_FUNGUS_STEM_BLOCK)
                    .require(NorthstarBlocks.PLATE_FUNGUS_STEM_BLOCK)
                    .require(NorthstarBlocks.PLATE_FUNGUS_STEM_BLOCK)
                    .output(NorthstarFluids.SODIUM.get(), 1000)),

    SODIUM_FROM_SPIKE_FUNGUS = create("sodium_from_spike_fungus",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(NorthstarBlocks.SPIKE_FUNGUS_BLOCK)
                    .require(NorthstarBlocks.SPIKE_FUNGUS_BLOCK)
                    .require(NorthstarBlocks.SPIKE_FUNGUS_BLOCK)
                    .output(NorthstarFluids.SODIUM.get(), 1000)),

    SODIUM_FROM_TOWER_FUNGUS = create("sodium_from_tower_fungus",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(NorthstarBlocks.TOWER_FUNGUS_STEM_BLOCK)
                    .require(NorthstarBlocks.TOWER_FUNGUS_STEM_BLOCK)
                    .require(NorthstarBlocks.TOWER_FUNGUS_STEM_BLOCK)
                    .output(NorthstarFluids.SODIUM.get(), 1000));

    public NorthstarCompactingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Northstar.MOD_ID);
    }

}
