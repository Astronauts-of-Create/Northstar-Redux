package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.lightning.northstar.item.NorthstarPotions;
import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

public class NorthstarMixingRecipeGen extends MixingRecipeGen {

    GeneratedRecipe
            $ = null,

    BRINE = create("brine",
            b -> b.require(Fluids.WATER, 500)
                    .require(NorthstarItems.SALT)
                    .output(NorthstarFluids.BRINE.get(), 550)),

    BRINE_TO_SALT = create("brine_to_salt",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(NorthstarFluidTags.COMMON_BRINE.tag, 750)
                    .output(NorthstarItems.SALT)),

    CHOCOLATE_ICE_CREAM = create("chocolate_ice_cream",
            b -> b.require(NorthstarFluids.VANILLA_ICE_CREAM.get(), 200)
                    .require(AllFluids.CHOCOLATE.get(), 50)
                    .output(NorthstarFluids.CHOCOLATE_ICE_CREAM.get(), 250)),


    HYDROCARBON_FROM_CARBON = create("hydrocarbon_from_carbon",
            b -> b.requiresHeat(HeatCondition.SUPERHEATED)
                    .require(NorthstarFluids.CARBON.get(), 500)
                    .require(Fluids.WATER, 500)
                    .require(NorthstarItems.SODIUM_CATALYST)
                    .require(NorthstarItems.SODIUM_CATALYST)
                    .require(NorthstarItems.SODIUM_CATALYST)
                    .output(NorthstarFluids.HYDROCARBON.get(), 500)),

    HYDROCARBON_FROM_SODIUM = create("hydrocarbon_from_sodium",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(NorthstarFluids.CARBON.get(), 750)
                    .require(NorthstarFluids.SODIUM_HYDROXIDE.get(), 100)
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

    SALT_FROM_SODIUM = create("salt_from_sodium",
            b -> b.requiresHeat(HeatCondition.HEATED)
                    .require(NorthstarFluids.SODIUM_HYDROXIDE.get(), 250)
                    .output(NorthstarItems.SALT)),

    STRAWBERRY_ICE_CREAM = create("strawberry_ice_cream",
            b -> b.require(NorthstarFluids.VANILLA_ICE_CREAM.get(), 200)
                    .require(NorthstarItems.MARTIAN_STRAWBERRY)
                    .output(NorthstarFluids.CHOCOLATE_ICE_CREAM.get(), 250)),

    //Easy enough?
    TITANIUM1 = create("titanium1",
            b -> b.requiresHeat(HeatCondition.SUPERHEATED)
                    .require(NorthstarItems.RUTILE_CONCENTRATE)
                    .require(NorthstarItems.RUTILE_CONCENTRATE)
                    .require(NorthstarItems.SODIUM_CATALYST)
                    .require(AllItems.ZINC_INGOT)
                    .require(NorthstarFluids.CARBON.get(), 200)
                    .output(NorthstarFluids.TITANIUM_TETRACHLORIDE.get(), 750)),

    TITANIUM2 = create("titanium2",
            b -> b.requiresHeat(HeatCondition.SUPERHEATED)
                    .require(NorthstarItems.RUTILE_CONCENTRATE)
                    .require(NorthstarItems.RUTILE_CONCENTRATE)
                    .require(NorthstarItems.RUTILE_CONCENTRATE)
                    .require(AllItems.ZINC_INGOT)
                    .require(NorthstarFluids.CARBON.get(), 1000)
                    .require(NorthstarFluids.CHLORINE.get(), 1000)
                    .output(NorthstarFluids.TITANIUM_TETRACHLORIDE.get(), 3000)),

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
