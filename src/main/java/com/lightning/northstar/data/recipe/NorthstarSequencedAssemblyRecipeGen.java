package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.accessor.NorthstarSequencedAssemblyRecipeBuilder;
import com.lightning.northstar.block.tech.circuit_engraver.EngravingRecipe;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.data.ModCompat;
import com.lightning.northstar.item.NorthstarPotions;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

import java.util.concurrent.CompletableFuture;

public class NorthstarSequencedAssemblyRecipeGen extends SequencedAssemblyRecipeGen {

    GeneratedRecipe
            $ = null,

    ADVANCED_CIRCUIT = create("advanced_circuit",
            b -> b.require(NorthstarItems.CIRCUIT)
                    .transitionTo(NorthstarItems.UNFINISHED_ADVANCED_CIRCUIT)
                    .addOutput(NorthstarItems.ADVANCED_CIRCUIT, 1)
                    .loops(5)
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItems.ADVANCED_CIRCUIT))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItems.POLISHED_LUNAR_SAPPHIRE))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItems.MARTIAN_STEEL_SHEET))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItemTags.COMMON_SHEETS_GOLD.tag))
                    .addStep(EngravingRecipe::new, r -> r)),

    CIRCUIT = create("circuit",
            b -> b.require(NorthstarItemTags.COMMON_SHEETS_IRON.tag)
                    .transitionTo(NorthstarItems.UNFINISHED_CIRCUIT)
                    .addOutput(NorthstarItems.CIRCUIT, 70)
                    .addOutput(Items.IRON_NUGGET, 20)
                    .addOutput(AllItems.COPPER_NUGGET, 10)
                    .loops(5)
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItems.POLISHED_AMETHYST))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItemTags.COMMON_SHEETS_COPPER.tag))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItemTags.COMMON_SHEETS_GOLD.tag))
                    .addStep(EngravingRecipe::new, r -> r)),

    DORMANT_MARTIAN_SAPLING = create("dormant_martian_sapling",
            b -> b.require(NorthstarItems.DORMANT_MARTIAN_SAPLING)
                    .transitionTo(NorthstarItems.DORMANT_MARTIAN_SAPLING_SEQUENCED)
                    .addOutput(NorthstarBlocks.COILER_SAPLING, 1)
                    .loops(2)
                    .addStep(FillingRecipe::new, r -> r.require(Fluids.WATER, 500))
                    .addStep(FillingRecipe::new, r -> r.require(PotionFluidHandler.potionIngredient(NorthstarPotions.ENHANCED_HEALING, 25)))
                    .addStep(FillingRecipe::new, r -> r.require(PotionFluidHandler.potionIngredient(NorthstarPotions.ENHANCED_STRENGTH, 25)))
                    .addStep(FillingRecipe::new, r -> r.require(PotionFluidHandler.potionIngredient(NorthstarPotions.ENHANCED_REGENERATION, 25)))),

    DORMANT_MARTIAN_SEED = create("dormant_martian_seed",
            b -> b.require(NorthstarItems.DORMANT_MARTIAN_SEED)
                    .transitionTo(NorthstarItems.DORMANT_MARTIAN_SEED_SEQUENCED)
                    .addOutput(NorthstarItems.MARS_SPROUT_SEEDS, 1)
                    .addOutput(NorthstarItems.MARS_PALM_SEEDS, 1)
                    .addOutput(NorthstarItems.MARS_TULIP_SEEDS, 1)
                    .loops(2)
                    .addStep(FillingRecipe::new, r -> r.require(Fluids.WATER, 500))
                    .addStep(FillingRecipe::new, r -> r.require(PotionFluidHandler.potionIngredient(NorthstarPotions.ENHANCED_HEALING, 25)))
                    .addStep(FillingRecipe::new, r -> r.require(PotionFluidHandler.potionIngredient(NorthstarPotions.ENHANCED_STRENGTH, 25)))
                    .addStep(FillingRecipe::new, r -> r.require(PotionFluidHandler.potionIngredient(NorthstarPotions.ENHANCED_REGENERATION, 25)))),

    HARDENED_PRECISION_MECHANISM = create("hardened_precision_mechanism",
            b -> b.require(NorthstarItemTags.COMMON_SHEETS_TITANIUM.tag)
                    .transitionTo(NorthstarItems.INCOMPLETE_HARDENED_PRECISION_MECHANISM)
                    .addOutput(NorthstarItems.HARDENED_PRECISION_MECHANISM, 80)
                    .addOutput(Items.IRON_NUGGET, 10)
                    .addOutput(NorthstarBlocks.IRON_COGWHEEL, 10)
                    .loops(8)
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(Items.IRON_NUGGET))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarBlocks.IRON_LARGE_COGWHEEL))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarBlocks.IRON_COGWHEEL))),

    TARGETING_COMPUTER = create("targeting_computer",
            b -> b.require(NorthstarItemTags.COMMON_SHEETS_IRON.tag)
                    .transitionTo(NorthstarItems.UNFINISHED_TARGETING_COMPUTER)
                    .addOutput(NorthstarItems.TARGETING_COMPUTER, 1)
                    .loops(8)
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItems.POLISHED_DIAMOND))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItems.HARDENED_PRECISION_MECHANISM))
                    .addStep(EngravingRecipe::new, r -> r)
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(NorthstarItems.CIRCUIT))
                    .addStep(EngravingRecipe::new, r -> r)),

    TITANIUM = create("titanium",
            b -> b.require(AllItems.STURDY_SHEET)
                    .transitionTo(NorthstarItems.INCOMPLETE_TITANIUM_INGOT)
                    .addOutput(NorthstarItems.TITANIUM_INGOT, 1)
                    .loops(2)
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(Items.IRON_INGOT))
                    .addStep(PressingRecipe::new, r -> r)
                    .addStep(FillingRecipe::new, r -> r.require(NorthstarFluids.TITANIUM_TETRACHLORIDE.get(), 500))
                    .addStep(PressingRecipe::new, r -> r)
                    .addStep(FillingRecipe::new, r -> r.require(Fluids.WATER, 1000))),

    TITANIUM_CBC = create("titanium_compat_cbc",
            b -> ((NorthstarSequencedAssemblyRecipeBuilder) b)
                    .northstar$whenModLoaded(ModCompat.CBC)
                    .require(AllItems.STURDY_SHEET)
                    .transitionTo(NorthstarItems.INCOMPLETE_TITANIUM_INGOT)
                    .addOutput(NorthstarItems.TITANIUM_INGOT, 1)
                    .loops(2)
                    .addStep(FillingRecipe::new, r -> r.require(NorthstarFluidTags.CBC_MOLTEN_CAST_IRON.tag, 250))
                    .addStep(FillingRecipe::new, r -> r.require(NorthstarFluids.TITANIUM_TETRACHLORIDE.get(), 500))
                    .addStep(PressingRecipe::new, r -> r)
                    .addStep(PressingRecipe::new, r -> r)
                    .addStep(FillingRecipe::new, r -> r.require(Fluids.WATER, 1000)));

    public NorthstarSequencedAssemblyRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Northstar.MOD_ID);
    }

}
