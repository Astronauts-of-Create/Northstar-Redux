package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.CrushingRecipeGen;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class NorthstarCrushingRecipeGen extends CrushingRecipeGen {

    GeneratedRecipe
            $ = null,

    BASALT = create(() -> Blocks.BASALT,
            b -> b.duration(1000)
                    .output(NorthstarItems.SALT, 2)
                    .output(0.75f, NorthstarItems.SALT)
                    .output(0.25f, NorthstarItems.SALT)),

    SAND = create("sand",
            b -> b.duration(200)
                    .require(ItemTags.SAND)
                    .output(0.25f, NorthstarItems.RUTILE_CONCENTRATE)),

    LIMESTONE = create(() -> AllPaletteStoneTypes.LIMESTONE.baseBlock.get(),
            b -> b.duration(800)
                    .output(0.5f, NorthstarItems.RUTILE_CONCENTRATE, 2)),

    MARS_GRAVEL = create(() -> NorthstarBlocks.MARS_GRAVEL,
            b -> b.duration(500)
                    .output(NorthstarBlocks.MARS_SAND)
                    .output(0.75f, NorthstarItems.SALT, 4)
                    .output(0.25f, NorthstarItems.SALT, 2)),

    MARS_STONE = create(() -> NorthstarBlocks.MARS_STONE,
            b -> b.duration(500)
                    .output(NorthstarBlocks.MARS_GRAVEL)),

    MOON_STONE = create(() -> NorthstarBlocks.MOON_STONE,
            b -> b.duration(500)
                    .output(NorthstarBlocks.MOON_SAND)),

    GLOWSTONE = create(() -> NorthstarItems.ENRICHED_GLOWSTONE_ORE,
            b -> b.duration(300)
                    .output(Items.GLOWSTONE_DUST, 2)),

    SPACE_COPPER_ORE = create("space_copper_ore",
            b -> b.duration(350)
                    .require(NorthstarItemTags.SPACE_ORE_COPPER.tag)
                    .output(AllItems.CRUSHED_COPPER, 2)
                    .output(0.25f, AllItems.CRUSHED_COPPER)
                    .output(0.75f, AllItems.EXP_NUGGET)),

    SPACE_GOLD_ORE = create("space_gold_ore",
            b -> b.duration(350)
                    .require(NorthstarItemTags.SPACE_ORE_GOLD.tag)
                    .output(AllItems.CRUSHED_GOLD, 2)
                    .output(0.25f, AllItems.CRUSHED_GOLD)
                    .output(0.75f, AllItems.EXP_NUGGET)),

    SPACE_IRON_ORE = create("space_iron_ore",
            b -> b.duration(350)
                    .require(NorthstarItemTags.SPACE_ORE_IRON.tag)
                    .output(AllItems.CRUSHED_IRON, 3)
                    .output(0.25f, AllItems.CRUSHED_IRON)
                    .output(0.75f, AllItems.EXP_NUGGET)),

    SPACE_TITANIUM_ORE = create("space_titanium_ore",
            b -> b.duration(500)
                    .require(NorthstarItemTags.SPACE_ORE_TITANIUM.tag)
                    .output(NorthstarItems.RAW_TITANIUM)
                    .output(0.75f, NorthstarItems.RAW_TITANIUM)
                    .output(0.75f, AllItems.EXP_NUGGET)),

    SPACE_TUNGSTEN_ORE = create("space_tungsten_ore",
            b -> b.duration(500)
                    .require(NorthstarItemTags.SPACE_ORE_TUNGSTEN.tag)
                    .output(NorthstarItems.CRUSHED_RAW_TUNGSTEN)
                    .output(0.75f, NorthstarItems.CRUSHED_RAW_TUNGSTEN)
                    .output(0.75f, AllItems.EXP_NUGGET)),

    SPACE_ZINC_ORE = create("space_zinc_ore",
            b -> b.duration(250)
                    .require(NorthstarItemTags.SPACE_ORE_ZINC.tag)
                    .output(AllItems.CRUSHED_ZINC)
                    .output(0.75f, AllItems.CRUSHED_ZINC)
                    .output(0.75f, AllItems.EXP_NUGGET)),

    SPACE_DIAMOND_ORE = create("space_diamond_ore",
            b -> b.duration(350)
                    .require(NorthstarItemTags.SPACE_ORE_DIAMOND.tag)
                    .output(Items.DIAMOND, 2)
                    .output(0.75f, Items.DIAMOND)
                    .output(0.75f, AllItems.EXP_NUGGET)),

    SPACE_LAPIS_ORE = create("space_lapis_ore",
            b -> b.duration(350)
                    .require(NorthstarItemTags.SPACE_ORE_LAPIS.tag)
                    .output(Items.LAPIS_LAZULI, 12)
                    .output(0.5f, Items.LAPIS_LAZULI, 4)
                    .output(0.75f, AllItems.EXP_NUGGET)),

    SPACE_QUARTZ_ORE = create("space_quartz_ores",
            b -> b.duration(350)
                    .require(NorthstarItemTags.SPACE_ORE_QUARTZ.tag)
                    .output(Items.QUARTZ, 3)
                    .output(0.5f, Items.QUARTZ)
                    .output(0.75f, AllItems.EXP_NUGGET)),

    SPACE_REDSTONE_ORE = create("space_redstone_ores",
            b -> b.duration(350)
                    .require(NorthstarItemTags.SPACE_ORE_REDSTONE.tag)
                    .output(Items.REDSTONE, 6)
                    .output(0.5f, Items.REDSTONE, 2)
                    .output(0.75f, AllItems.EXP_NUGGET)),

    LUNAR_SAPPHIRE = create(() -> NorthstarBlocks.LUNAR_SAPPHIRE_BLOCK,
            b -> b.duration(150)
                    .output(NorthstarItems.LUNAR_SAPPHIRE_SHARD, 3)
                    .output(0.5f, NorthstarItems.LUNAR_SAPPHIRE_SHARD)),

    LUNAR_SAPPHIRE_CLUSTER = create(() -> NorthstarBlocks.LUNAR_SAPPHIRE_CLUSTER,
            b -> b.duration(150)
                    .output(NorthstarItems.LUNAR_SAPPHIRE_SHARD, 3)
                    .output(0.5f, NorthstarItems.LUNAR_SAPPHIRE_SHARD)),

    VENUS_GRAVEL = create(() -> NorthstarBlocks.VENUS_GRAVEL,
            b -> b.duration(500)
                    .output(NorthstarItems.RUTILE_CONCENTRATE)
                    .output(0.5f, NorthstarItems.RUTILE_CONCENTRATE)
                    .output(0.25f, NorthstarItems.RUTILE_CONCENTRATE));

    public NorthstarCrushingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Northstar.MOD_ID);
    }

}
