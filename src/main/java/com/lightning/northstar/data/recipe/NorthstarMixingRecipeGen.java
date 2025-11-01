package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.item.NorthstarPotions;
import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

public class NorthstarMixingRecipeGen extends MixingRecipeGen {

    GeneratedRecipe
            $ = null,

    BRINE = create("brine",
            b -> b.require(FluidTags.WATER, 500)
                    .require(NorthstarItemTags.C_DUSTS_SALT.tag)
                    .output(NorthstarFluids.BRINE.get(), 550)),

    HYDROGEN_CHLORIDE = create("hydrogen_chloride",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(NorthstarFluids.CHLORINE.get(), 500)
                    .require(NorthstarFluids.HYDROGEN.get(), 500)
                    .output(NorthstarFluids.HYDROGEN_CHLORIDE.get(), 550)),

    HYDROCHLORIC_ACID = create("hydrochloric_acid",
                    b -> b.requiresHeat(HeatCondition.HEATED)
                            .require(Fluids.WATER, 500)
                            .require(NorthstarFluids.HYDROGEN_CHLORIDE.get(), 500)
                            .output(NorthstarFluids.HYDROCHLORIC_ACID.get(), 1000)),

    GALENA = create("galena_to_lead_ingot_and_sulfuric_acid",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(NorthstarItems.RAW_GALENA)
                    .output(NorthstarFluids.SULFURIC_ACID.get(), 550)
                    .output(ForgeRegistries.ITEMS.getValue(new ResourceLocation("tfmg", "lead_ingot")))),

    BLUE_AURENE_GLASS = create("blue_aurene_glass",
            b -> b.requiresHeat(HeatCondition.SUPERHEATED)
                    .require(NorthstarItems.RAW_GALENA)
                    .require(Items.GLASS)
                    .require(NorthstarFluids.HYDROCHLORIC_ACID.get(), 550)
                    .output(NorthstarBlocks.BLUE_AURENE_GLASS)),

    PORCELAIN_SKELETON = create("porcelain_skeleton",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(Items.SKELETON_SKULL)
                    .require(NorthstarItems.HECTORITE_BALL)
                    .output(NorthstarBlocks.PORCELAIN_SKELETON)
                    .output(Items.SKELETON_SKULL)),

    PORCELAIN_WITHER = create("porcelain_wither",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(Items.WITHER_SKELETON_SKULL)
                    .require(NorthstarItems.HECTORITE_BALL)
                    .output(NorthstarBlocks.PORCELAIN_WITHER)
                    .output(Items.WITHER_SKELETON_SKULL)),


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
            b -> b.require(FluidIngredient.fromFluidStack(PotionFluid.of(50, Potions.STRONG_REGENERATION, PotionFluid.BottleType.REGULAR)))
                    .require(NorthstarItems.ENRICHED_GLOWSTONE_ORE)
                    .output(PotionFluid.of(50, NorthstarPotions.ENHANCED_REGENERATION.get(), PotionFluid.BottleType.REGULAR))),

    POTION_HEALING_3 = create("potion_healing_3",
            b -> b.require(FluidIngredient.fromFluidStack(PotionFluid.of(50, Potions.STRONG_HEALING, PotionFluid.BottleType.REGULAR)))
                    .require(NorthstarItems.ENRICHED_GLOWSTONE_ORE)
                    .output(PotionFluid.of(50, NorthstarPotions.ENHANCED_HEALING.get(), PotionFluid.BottleType.REGULAR))),

    POTION_STRENGTH_3 = create("potion_strength_3",
            b -> b.require(FluidIngredient.fromFluidStack(PotionFluid.of(50, Potions.STRONG_STRENGTH, PotionFluid.BottleType.REGULAR)))
                    .require(NorthstarItems.ENRICHED_GLOWSTONE_ORE)
                    .output(PotionFluid.of(50, NorthstarPotions.ENHANCED_STRENGTH.get(), PotionFluid.BottleType.REGULAR))),

    STRAWBERRY_ICE_CREAM = create("strawberry_ice_cream",
            b -> b.require(NorthstarFluidTags.C_VANILLA_ICE_CREAM.tag, 200)
                    .require(NorthstarItems.MARTIAN_STRAWBERRY)
                    .output(NorthstarFluids.CHOCOLATE_ICE_CREAM.get(), 250)),

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

    public NorthstarMixingRecipeGen(PackOutput output) {
        super(output, Northstar.MOD_ID);
    }

}
