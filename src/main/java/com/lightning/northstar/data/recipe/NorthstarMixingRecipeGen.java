package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.item.NorthstarPotions;
import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

import java.util.concurrent.CompletableFuture;

public class NorthstarMixingRecipeGen extends MixingRecipeGen {

    GeneratedRecipe
            $ = null,

    BRINE = create("brine",
            b -> b.require(FluidTags.WATER, 500)
                    .require(NorthstarItemTags.C_DUSTS_SALT.tag)
                    .output(NorthstarFluids.BRINE.get(), 550)),

    CHOCOLATE_ICE_CREAM = create("chocolate_ice_cream",
            b -> b.require(NorthstarFluidTags.C_VANILLA_ICE_CREAM.tag, 200)
                    .require(AllFluids.CHOCOLATE.get(), 50)
                    .output(NorthstarFluids.CHOCOLATE_ICE_CREAM.get(), 250)),

    HYDROCARBON_FROM_CARBON = create("hydrocarbon_from_carbon",
            b -> b.requiresHeat(HeatCondition.SUPERHEATED)
                    .require(NorthstarFluidTags.C_CARBON.tag, 500)
                    .require(FluidTags.WATER, 500)
                    .require(NorthstarItems.SODIUM_CATALYST)
                    .require(NorthstarItems.SODIUM_CATALYST)
                    .require(NorthstarItems.SODIUM_CATALYST)
                    .output(NorthstarFluids.HYDROCARBON.get(), 500)),

    HYDROCARBON_FROM_SODIUM = create("hydrocarbon_from_sodium",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(NorthstarFluidTags.C_CARBON.tag, 750)
                    .require(NorthstarFluidTags.C_SODIUM.tag, 100)
                    .output(NorthstarFluids.HYDROCARBON.get(), 750)),

    POTION_REGENERATION_3 = create("potion_regeneration_3",
            b -> b.require(PotionFluidHandler.potionIngredient(Potions.STRONG_REGENERATION, 50))
                    .require(NorthstarItems.ENRICHED_GLOWSTONE_ORE)
                    .output(PotionFluid.of(50, new PotionContents(NorthstarPotions.ENHANCED_REGENERATION), PotionFluid.BottleType.REGULAR))),

    POTION_HEALING_3 = create("potion_healing_3",
            b -> b.require(PotionFluidHandler.potionIngredient(Potions.STRONG_HEALING, 50))
                    .require(NorthstarItems.ENRICHED_GLOWSTONE_ORE)
                    .output(PotionFluid.of(50, new PotionContents(NorthstarPotions.ENHANCED_HEALING), PotionFluid.BottleType.REGULAR))),

    POTION_STRENGTH_3 = create("potion_strength_3",
            b -> b.require(PotionFluidHandler.potionIngredient(Potions.STRONG_STRENGTH, 50))
                    .require(NorthstarItems.ENRICHED_GLOWSTONE_ORE)
                    .output(PotionFluid.of(50, new PotionContents(NorthstarPotions.ENHANCED_STRENGTH), PotionFluid.BottleType.REGULAR))),

    STRAWBERRY_ICE_CREAM = create("strawberry_ice_cream",
            b -> b.require(NorthstarFluidTags.C_VANILLA_ICE_CREAM.tag, 200)
                    .require(NorthstarItems.MARTIAN_STRAWBERRY)
                    .output(NorthstarFluids.STRAWBERRY_ICE_CREAM.get(), 250)),

    // Pre electrolysis, uses sodium catalysis but has a bad ratio
    TITANIUM1 = create("titanium1",
            b -> b.requiresHeat(HeatCondition.SUPERHEATED)
                    .require(NorthstarItems.RUTILE_CONCENTRATE)
                    .require(NorthstarItems.RUTILE_CONCENTRATE)
                    .require(NorthstarItems.RUTILE_CONCENTRATE)
                    .require(NorthstarItems.SODIUM_CATALYST)
                    .require(AllItems.ZINC_INGOT)
                    .require(NorthstarFluidTags.C_CARBON.tag, 500)
                    .output(NorthstarFluids.TITANIUM_TETRACHLORIDE.get(), 500)),

    // Post electrolysis, better ratios
    TITANIUM2 = create("titanium2",
            b -> b.requiresHeat(HeatCondition.SUPERHEATED)
                    .require(NorthstarItems.RUTILE_CONCENTRATE)
                    .require(NorthstarItems.RUTILE_CONCENTRATE)
                    .require(AllItems.ZINC_INGOT)
                    .require(NorthstarFluidTags.C_CHLORINE.tag, 250)
                    .require(NorthstarFluidTags.C_CARBON.tag, 500)
                    .output(NorthstarFluids.TITANIUM_TETRACHLORIDE.get(), 1000)),

    WATER_FROM_BLUE_ICE = create("water_from_blue_ice",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(Items.BLUE_ICE)
                    .output(Fluids.WATER, 1000 * 9 * 9)),

    WATER_FROM_PACKED_BLUE_ICE = create("water_from_packed_ice",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(Items.PACKED_ICE)
                    .output(Fluids.WATER, 1000 * 9)),

    WATER_FROM_ICE = create("water_from_ice",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(Items.ICE)
                    .output(Fluids.WATER, 1000)),

    WATER_FROM_SNOW = create("water_from_snow",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(Blocks.SNOW_BLOCK)
                    .output(Fluids.WATER, 250));

    public NorthstarMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Northstar.MOD_ID);
    }

}
