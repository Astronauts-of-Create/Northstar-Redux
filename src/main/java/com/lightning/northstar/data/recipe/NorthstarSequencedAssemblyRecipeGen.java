package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.accessor.NorthstarSequencedAssemblyRecipeBuilder;
import com.lightning.northstar.block.tech.circuit_engraver.EngravingRecipe;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.data.ModCompat;
import com.lightning.northstar.item.NorthstarPotions;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;

public class NorthstarSequencedAssemblyRecipeGen extends SequencedAssemblyRecipeGen {

    GeneratedRecipe
            $ = null,

    ADVANCED_CIRCUIT = create("advanced_circuit",
            b -> b.require(NorthstarItems.CIRCUIT)
                    .transitionTo(NorthstarItems.UNFINISHED_ADVANCED_CIRCUIT)
                    .addOutput(NorthstarItems.ADVANCED_CIRCUIT, 1)
                    .loops(5)
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItems.POLISHED_LUNAR_SAPPHIRE))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItems.MARTIAN_STEEL_SHEET))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItemTags.C_SHEETS_GOLD.tag))
                    .addStep(EngravingRecipe::new, r -> r.duration(200))),

    CIRCUIT = create("circuit",
            b -> b.require(NorthstarItemTags.C_SHEETS_IRON.tag)
                    .transitionTo(NorthstarItems.UNFINISHED_CIRCUIT)
                    .addOutput(NorthstarItems.CIRCUIT, 70)
                    .addOutput(Items.IRON_NUGGET, 20)
                    .addOutput(AllItems.COPPER_NUGGET, 10)
                    .loops(5)
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItems.POLISHED_AMETHYST))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItemTags.C_SHEETS_COPPER.tag))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItemTags.C_SHEETS_GOLD.tag))
                    .addStep(EngravingRecipe::new, r -> r.duration(100))),

    DORMANT_MARTIAN_SAPLING = create("dormant_martian_sapling",
            b -> b.require(NorthstarItems.DORMANT_MARTIAN_SAPLING)
                    .transitionTo(NorthstarItems.DORMANT_MARTIAN_SAPLING_SEQUENCED)
                    .addOutput(NorthstarBlocks.COILER_SAPLING, 1)
                    .loops(2)
                    .addStep(FillingRecipe::new, r -> r.require(FluidTags.WATER, 500))
                    .addStep(FillingRecipe::new, r -> r.require(FluidIngredient.fromFluidStack(PotionFluid.of(25, NorthstarPotions.ENHANCED_HEALING.get(), PotionFluid.BottleType.REGULAR))))
                    .addStep(FillingRecipe::new, r -> r.require(FluidIngredient.fromFluidStack(PotionFluid.of(25, NorthstarPotions.ENHANCED_STRENGTH.get(), PotionFluid.BottleType.REGULAR))))
                    .addStep(FillingRecipe::new, r -> r.require(FluidIngredient.fromFluidStack(PotionFluid.of(25, NorthstarPotions.ENHANCED_REGENERATION.get(), PotionFluid.BottleType.REGULAR))))),

    DORMANT_MARTIAN_SEED = create("dormant_martian_seed",
            b -> b.require(NorthstarItems.DORMANT_MARTIAN_SEED)
                    .transitionTo(NorthstarItems.DORMANT_MARTIAN_SEED_SEQUENCED)
                    .addOutput(NorthstarItems.MARS_SPROUT_SEEDS, 1)
                    .addOutput(NorthstarItems.MARS_PALM_SEEDS, 1)
                    .addOutput(NorthstarItems.MARS_TULIP_SEEDS, 1)
                    .loops(2)
                    .addStep(FillingRecipe::new, r -> r.require(FluidTags.WATER, 500))
                    .addStep(FillingRecipe::new, r -> r.require(FluidIngredient.fromFluidStack(PotionFluid.of(25, NorthstarPotions.ENHANCED_HEALING.get(), PotionFluid.BottleType.REGULAR))))
                    .addStep(FillingRecipe::new, r -> r.require(FluidIngredient.fromFluidStack(PotionFluid.of(25, NorthstarPotions.ENHANCED_STRENGTH.get(), PotionFluid.BottleType.REGULAR))))
                    .addStep(FillingRecipe::new, r -> r.require(FluidIngredient.fromFluidStack(PotionFluid.of(25, NorthstarPotions.ENHANCED_REGENERATION.get(), PotionFluid.BottleType.REGULAR))))),

    HARDENED_PRECISION_MECHANISM = create("hardened_precision_mechanism",
            b -> b.require(AllItems.PRECISION_MECHANISM)
                    .transitionTo(NorthstarItems.INCOMPLETE_HARDENED_PRECISION_MECHANISM)
                    .addOutput(NorthstarItems.HARDENED_PRECISION_MECHANISM, 85)
                    .addOutput(Items.IRON_NUGGET, 5)
                    .addOutput(NorthstarBlocks.IRON_COGWHEEL, 10)
                    .loops(5)
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarTags.NorthstarItemTags.C_INGOTS_TITANIUM.tag))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarBlocks.IRON_COGWHEEL))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarBlocks.IRON_LARGE_COGWHEEL))),

    TARGETING_COMPUTER = create("targeting_computer",
            b -> b.require(NorthstarItemTags.C_SHEETS_IRON.tag)
                    .transitionTo(NorthstarItems.UNFINISHED_TARGETING_COMPUTER)
                    .addOutput(NorthstarItems.TARGETING_COMPUTER, 1)
                    .loops(8)
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItems.POLISHED_DIAMOND))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItems.HARDENED_PRECISION_MECHANISM))
                    .addStep(EngravingRecipe::new, r -> r.duration(50))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItems.CIRCUIT))
                    .addStep(EngravingRecipe::new, r -> r.duration(50))),

    TITANIUM = create("titanium",
            b -> b.require(AllItems.STURDY_SHEET)
                    .transitionTo(NorthstarItems.INCOMPLETE_TITANIUM_INGOT)
                    .addOutput(NorthstarItems.TITANIUM_INGOT, 1)
                    .loops(1)
                    .addStep(FillingRecipe::new, r -> r.require(FluidTags.LAVA, 500))
                    .addStep(PressingRecipe::new, r -> r)
                    .addStep(FillingRecipe::new, r -> r.require(NorthstarFluidTags.C_TITANIUM_TETRACHLORIDE.tag(), 500))
                    .addStep(PressingRecipe::new, r -> r)
                    .addStep(FillingRecipe::new, r -> r.require(FluidTags.WATER, 1000))),

    TITANIUM_CBC = create("titanium_compat_cbc",
            b -> ((NorthstarSequencedAssemblyRecipeBuilder) b)
                    .northstar$whenModLoaded(ModCompat.CBC)
                    .require(AllItems.STURDY_SHEET)
                    .transitionTo(NorthstarItems.INCOMPLETE_TITANIUM_INGOT)
                    .addOutput(NorthstarItems.TITANIUM_INGOT, 1)
                    .loops(2)
                    .addStep(FillingRecipe::new, r -> r.require(NorthstarFluidTags.COMPAT_CBC_MOLTEN_CAST_IRON.tag, 250))
                    .addStep(FillingRecipe::new, r -> r.require(NorthstarFluidTags.C_TITANIUM_TETRACHLORIDE.tag(), 250))
                    .addStep(PressingRecipe::new, r -> r)
                    .addStep(PressingRecipe::new, r -> r)
                    .addStep(FillingRecipe::new, r -> r.require(FluidTags.WATER, 1000)));

    public NorthstarSequencedAssemblyRecipeGen(PackOutput output) {
        super(output, Northstar.MOD_ID);
    }

}
