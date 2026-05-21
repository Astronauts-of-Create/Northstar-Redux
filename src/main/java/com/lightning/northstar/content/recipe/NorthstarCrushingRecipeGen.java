package com.lightning.northstar.content.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarItems;
import com.simibubi.create.api.data.recipe.CrushingRecipeGen;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

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

    public NorthstarCrushingRecipeGen(PackOutput generator) {
        super(generator, Northstar.MOD_ID);
    }

}
