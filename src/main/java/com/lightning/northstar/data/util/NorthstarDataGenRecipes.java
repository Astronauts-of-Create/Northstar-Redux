package com.lightning.northstar.data.util;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.simple.GrateBlock;
import com.lightning.northstar.block.simple.VerticalSlabBlock;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.data.Tags;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;

import java.util.function.Supplier;

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

    public record OreType(Supplier<ItemLike> crushedItem, float expectedAmount, int crushingDuration,
                          Supplier<ItemLike> smeltedItem, float smeltingXp, int smeltingDuration) {
        public OreType(Supplier<ItemLike> crushedItem, float expectedAmount, int crushingDuration) {
            this(crushedItem, expectedAmount, crushingDuration, null, 0, 0);
        }

        public OreType with(float expectedAmount, int crushingDuration) {
            return new OreType(crushedItem, expectedAmount, crushingDuration, smeltedItem, smeltingXp, smeltingDuration);
        }

        // Create/Vanilla values are used for existing ores
        public static final OreType
                COAL = new OreType(() -> Items.COAL, 1.75f, 150, () -> Items.COAL, 0.1f, 200),
                COPPER = new OreType(AllItems.CRUSHED_COPPER::get, 5.25f, 250, () -> Items.COPPER_INGOT, 0.7f, 200),
                DIAMOND = new OreType(() -> Items.DIAMOND, 1.75f, 350, () -> Items.DIAMOND, 1.0f, 200),
                EMERALD = new OreType(() -> Items.EMERALD, 1.75f, 350, () -> Items.EMERALD, 1.0f, 200),
                GLOWSTONE = new OreType(NorthstarItems.RAW_GLOWSTONE_ORE::get, 1.75f, 250, () -> Items.GLOWSTONE_DUST, 1.0f, 200),
                GOLD = new OreType(AllItems.CRUSHED_GOLD::get, 1.75f, 250, () -> Items.GOLD_INGOT, 1.0f, 200),
                IRON = new OreType(AllItems.CRUSHED_IRON::get, 1.75f, 250, () -> Items.IRON_INGOT, 0.7f, 200),
                LAPIS = new OreType(() -> Items.LAPIS_LAZULI, 10.5f, 250, () -> Items.LAPIS_LAZULI, 0.2f, 200),
                MARTIAN_IRON = new OreType(NorthstarItems.RAW_MARTIAN_IRON_ORE::get, 1.75f, 250, () -> Items.IRON_INGOT, 0.7f, 200),
                QUARTZ = new OreType(() -> Items.QUARTZ, 2.25f, 350, () -> Items.QUARTZ, 0.2f, 200),
                REDSTONE = new OreType(() -> Items.REDSTONE, 6.5f, 250, () -> Items.REDSTONE, 0.7f, 200),
                TITANIUM = new OreType(NorthstarItems.RAW_TITANIUM_ORE::get, 1.75f, 350, NorthstarItems.TITANIUM_INGOT::get, 1.0f, 200),
                TUNGSTEN = new OreType(NorthstarItems.CRUSHED_RAW_TUNGSTEN::get, 1.75f, 450, NorthstarItems.TUNGSTEN_INGOT::get, 1.0f, 200),
                ZINC = new OreType(() -> Items.IRON_INGOT, 1.75f, 250),
                DEEP_COAL = COAL.with(2.25f, 300),
                DEEP_COPPER = COPPER.with(7.25f, 350),
                DEEP_DIAMOND = DIAMOND.with(2.25f, 450),
                DEEP_EMERALD = EMERALD.with(2.25f, 450),
                DEEP_GLOWSTONE = GLOWSTONE.with(2.25f, 350),
                DEEP_GOLD = GOLD.with(2.25f, 350),
                DEEP_IRON = IRON.with(2.25f, 350),
                DEEP_LAPIS = LAPIS.with(12.5f, 350),
                DEEP_MARTIAN_IRON = MARTIAN_IRON.with(2.25f, 350),
                DEEP_QUARTZ = QUARTZ.with(2.75f, 350),
                DEEP_REDSTONE = REDSTONE.with(7.5f, 350),
                DEEP_TITANIUM = TITANIUM.with(2.25f, 450),
                DEEP_TUNGSTEN = TUNGSTEN.with(2.25f, 550),
                DEEP_ZINC = ZINC.with(2.25f, 350);
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateRecipeProvider> ore(ItemLike stone, OreType type) {
        return (c, p) -> {
            String oreName = RegisteredObjects.getKeyOrThrow(c.get().asItem()).getPath();

            if (type.crushedItem != null) {
                ProcessingRecipeBuilder<?> builder = new ProcessingRecipeBuilder<>(
                        AllRecipeTypes.CRUSHING.<ProcessingRecipeSerializer<?>>getSerializer().getFactory(),
                        Northstar.asResource(oreName))
                        .require(c.get())
                        .duration(type.crushingDuration)
                        .output(type.crushedItem.get(), Mth.floor(type.expectedAmount));
                float extra = type.expectedAmount - Mth.floor(type.expectedAmount);
                if (extra > 0)
                    builder.output(extra, type.crushedItem.get(), 1);
                builder.output(.75f, AllItems.EXP_NUGGET.get(), type.crushedItem.get() == AllItems.CRUSHED_GOLD.get() ? 2 : 1)
                        .output(.125f, stone)
                        .build(p);
            }

            if (type.smeltedItem != null) {
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(c.get()), RecipeCategory.MISC, type.smeltedItem.get(), type.smeltingXp, type.smeltingDuration)
                        .unlockedBy("has_item", RegistrateRecipeProvider.has(c.get()))
                        .save(p, Northstar.asResource("smelting/" + oreName));

                SimpleCookingRecipeBuilder.blasting(Ingredient.of(c.get()), RecipeCategory.MISC, type.smeltedItem.get(), type.smeltingXp, type.smeltingDuration / 2)
                        .unlockedBy("has_item", RegistrateRecipeProvider.has(c.get()))
                        .save(p, Northstar.asResource("blasting/" + oreName));
            }
        };
    }

}
