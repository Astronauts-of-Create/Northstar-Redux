package com.lightning.northstar.data.util;

import com.lightning.northstar.block.simple.GrateBlock;
import com.lightning.northstar.block.simple.VerticalSlabBlock;
import com.lightning.northstar.data.Tags;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;

public class NorthstarDataGenRecipes {

    public static NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateRecipeProvider> plank(Tags.Tag<Item> log) {
        return plank(log.tag());
    }

    public static NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateRecipeProvider> plank(TagKey<Item> log) {
        return (c, p) -> RegistrateRecipeProvider.planksFromLog(p, c.get(), log, 4);
    }

    public static NonNullBiConsumer<DataGenContext<Block, SlabBlock>, RegistrateRecipeProvider> slab(BlockEntry<Block> source) {
        return (c, p) -> {
            RegistrateRecipeProvider.slab(p, RecipeCategory.BUILDING_BLOCKS, () -> c.get().asItem(), () -> source.get().asItem());
            p.stonecutting(DataIngredient.items(source.get()), RecipeCategory.BUILDING_BLOCKS, c, 2);
        };
    }

    public static NonNullBiConsumer<DataGenContext<Block, VerticalSlabBlock>, RegistrateRecipeProvider> verticalSlab(BlockEntry<Block> source) {
        return (c, p) -> {
            Item material = source.get().asItem();
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, c.get(), 6)
                    .define('#', material)
                    .pattern("#")
                    .pattern("#")
                    .pattern("#")
                    .unlockedBy(RegistrateRecipeProvider.getHasName(material), RegistrateRecipeProvider.has(material))
                    .save(p);
            p.stonecutting(DataIngredient.items(source.get()), RecipeCategory.BUILDING_BLOCKS, c, 2);
        };
    }

    public static NonNullBiConsumer<DataGenContext<Block, StairBlock>, RegistrateRecipeProvider> stair(BlockEntry<Block> source) {
        return (c, p) -> {
            Item material = source.get().asItem();
            RegistrateRecipeProvider.stairBuilder(() -> c.get().asItem(), Ingredient.of(material))
                    .unlockedBy(RegistrateRecipeProvider.getHasName(material), RegistrateRecipeProvider.has(material))
                    .save(p);
            p.stonecutting(DataIngredient.items(source.get()), RecipeCategory.BUILDING_BLOCKS, c, 1); // 1 to 1
        };
    }

    public static NonNullBiConsumer<DataGenContext<Block, WallBlock>, RegistrateRecipeProvider> wall(BlockEntry<Block> source) {
        return (c, p) -> {
            RegistrateRecipeProvider.wall(p, RecipeCategory.BUILDING_BLOCKS, () -> c.get().asItem(), () -> source.get().asItem());
            p.stonecutting(DataIngredient.items(source.get()), RecipeCategory.BUILDING_BLOCKS, c, 1); // 1 to 1
        };
    }

    public static NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateRecipeProvider> sheetmetal(Tags.Tag<Item> sheet) {
        return sheetmetal(sheet.tag());
    }

    public static NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateRecipeProvider> sheetmetal(TagKey<Item> sheet) {
        return commonFourToFour(sheet);
    }

    public static NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateRecipeProvider> plating(Tags.Tag<Item> ingot) {
        return plating(ingot.tag());
    }

    public static NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateRecipeProvider> plating(TagKey<Item> sheet) {
        return commonFourToFour(sheet);
    }

    public static <B extends Block> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateRecipeProvider> commonFourToFour(TagKey<Item> tag) {
        return (c, p) -> {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, c.get(), 4)
                    .unlockedBy("has_item", RegistrateRecipeProvider.has(tag))
                    .define('#', tag)
                    .pattern("##")
                    .pattern("##")
                    .save(p);
            p.stonecutting(DataIngredient.tag(tag), RecipeCategory.BUILDING_BLOCKS, c, 1); // 1 to 1
        };
    }

    public static NonNullBiConsumer<DataGenContext<Block, RotatedPillarBlock>, RegistrateRecipeProvider> pillar(Tags.Tag<Item> material) {
        return pillar(material.tag());
    }

    public static NonNullBiConsumer<DataGenContext<Block, RotatedPillarBlock>, RegistrateRecipeProvider> pillar(TagKey<Item> material) {
        return (c, p) -> {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, c.get(), 2)
                    .unlockedBy("has_item", RegistrateRecipeProvider.has(material))
                    .define('#', material)
                    .pattern("#")
                    .pattern("#")
                    .save(p);
            p.stonecutting(DataIngredient.tag(material), RecipeCategory.BUILDING_BLOCKS, c, 1);
        };
    }

    public static NonNullBiConsumer<DataGenContext<Block, RotatedPillarBlock>, RegistrateRecipeProvider> pillar(ItemLike material) {
        return (c, p) -> {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, c.get(), 2)
                    .unlockedBy("has_item", RegistrateRecipeProvider.has(material))
                    .define('#', material)
                    .pattern("#")
                    .pattern("#")
                    .save(p);
            p.stonecutting(DataIngredient.items(material), RecipeCategory.BUILDING_BLOCKS, c, 1);
        };
    }

    public static NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateRecipeProvider> chiseled(ItemLike slab) {
        return (c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, c.get(), 1)
                .unlockedBy("has_item", RegistrateRecipeProvider.has(slab)) // TODO: should it unlock from the base block rather than the slab?
                .define('#', slab)
                .pattern("#")
                .pattern("#")
                .save(p);
    }

    public static NonNullBiConsumer<DataGenContext<Block, GrateBlock>, RegistrateRecipeProvider> grate(Tags.Tag<Item> material) {
        return (c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, c.get(), 4)
                .unlockedBy("has_item", RegistrateRecipeProvider.has(material.tag()))
                .define('#', material.tag())
                .pattern("###")
                .pattern("# #")
                .pattern("###")
                .save(p);
    }

}
