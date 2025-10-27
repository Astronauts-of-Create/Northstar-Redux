package com.lightning.northstar.util;

import com.lightning.northstar.block.simple.GrateBlock;
import com.lightning.northstar.block.simple.VerticalSlabBlock;
import com.lightning.northstar.block.simple.VerticalSlabType;
import com.lightning.northstar.data.Tags;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class NorthstarDataGenHelper {

    public static <I extends Item> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelProvider> itemGeneratedItem() {
        return (c, p) -> p.generated(c::get, p.modLoc("item/" + c.getName()));
    }

    public static <I extends Item> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelProvider> itemGeneratedBlock() {
        return (c, p) -> p.generated(c::get, p.modLoc("block/" + c.getName()));
    }

    public static <I extends Item> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelProvider> itemGeneratedBlock(String... suffix) {
        return (c, p) -> p.generated(c::get, p.modLoc("block/" + c.getName() + String.join("", suffix)));
    }

    // temporary placeholders nothingness to bypass data generation errors, those will need to be done one day
    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> manualModel() {
        return (c, p) -> {
        };
    }

    public static NonNullBiConsumer<DataGenContext<Block, SlabBlock>, RegistrateBlockstateProvider> simpleSlab() {
        return (c, p) -> {
            //ResourceLocation name = p.modLoc("block/" + c.getName().replaceFirst("_slab$", ""));
            //p.slabBlock(c.get(), name, name);
        };
    }

    public static NonNullBiConsumer<DataGenContext<Block, VerticalSlabBlock>, RegistrateBlockstateProvider> simpleVerticalSlab() {
        return (c, p) -> {
            /*ResourceLocation name = p.modLoc("block/" + c.getName().replaceFirst("_slab$", ""));

            BlockModelBuilder slab = p.models().withExistingParent(c.getName(), p.modLoc("block/vertical_slab"))
                    .texture("side", name)
                    .texture("bottom", name)
                    .texture("top", name);

            p.getVariantBuilder(c.get())
                    .partialState()
                    .with(VerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE)
                    .addModels(new ConfiguredModel(p.models().getExistingFile(name)))
                    .partialState()
                    .with(VerticalSlabBlock.TYPE, VerticalSlabType.WEST)
                    .addModels(new ConfiguredModel(slab, 0, 0, false))
                    .partialState()
                    .with(VerticalSlabBlock.TYPE, VerticalSlabType.NORTH)
                    .addModels(new ConfiguredModel(slab, 0, 90, false))
                    .partialState()
                    .with(VerticalSlabBlock.TYPE, VerticalSlabType.EAST)
                    .addModels(new ConfiguredModel(slab, 0, 180, false))
                    .partialState()
                    .with(VerticalSlabBlock.TYPE, VerticalSlabType.SOUTH)
                    .addModels(new ConfiguredModel(slab, 0, 270, false));*/
        };
    }

    public static NonNullBiConsumer<RegistrateBlockLootTables, VerticalSlabBlock> verticalSlabLoot() {
        return (c, b) -> c.add(b, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(c.applyExplosionDecay(b, LootItem.lootTableItem(b)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))
                                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b)
                                                .setProperties(Builder.properties().hasProperty(VerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE))))))));
    }

    public static <B extends Block> NonNullBiConsumer<RegistrateBlockLootTables, B> cropLoot(ItemLike result, ItemLike seeds, int maxAge) {
        return (c, b) -> {
            LootItemCondition.Builder condition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(b)
                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, maxAge));
            c.add(b, c.createCropDrops(b, result.asItem(), seeds.asItem(), condition));
        };
    }

    public static NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateRecipeProvider> plankRecipe(Tags.Tag<Item> log) {
        return plankRecipe(log.tag());
    }

    public static NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateRecipeProvider> plankRecipe(TagKey<Item> log) {
        return (c, p) -> RegistrateRecipeProvider.planksFromLog(p, c.get(), log, 4);
    }

    public static NonNullBiConsumer<DataGenContext<Block, SlabBlock>, RegistrateRecipeProvider> slabRecipe(BlockEntry<Block> source) {
        return (c, p) -> {
            RegistrateRecipeProvider.slab(p, RecipeCategory.BUILDING_BLOCKS, () -> c.get().asItem(), () -> source.get().asItem());
            p.stonecutting(DataIngredient.items(source.get()), RecipeCategory.BUILDING_BLOCKS, c, 2);
        };
    }

    public static NonNullBiConsumer<DataGenContext<Block, VerticalSlabBlock>, RegistrateRecipeProvider> verticalSlabRecipe(BlockEntry<Block> source) {
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

    public static NonNullBiConsumer<DataGenContext<Block, StairBlock>, RegistrateRecipeProvider> stairRecipe(BlockEntry<Block> source) {
        return (c, p) -> {
            Item material = source.get().asItem();
            RegistrateRecipeProvider.stairBuilder(() -> c.get().asItem(), Ingredient.of(material))
                    .unlockedBy(RegistrateRecipeProvider.getHasName(material), RegistrateRecipeProvider.has(material))
                    .save(p);
            p.stonecutting(DataIngredient.items(source.get()), RecipeCategory.BUILDING_BLOCKS, c, 1); // 1 to 1
        };
    }

    public static NonNullBiConsumer<DataGenContext<Block, WallBlock>, RegistrateRecipeProvider> wallRecipe(BlockEntry<Block> source) {
        return (c, p) -> {
            RegistrateRecipeProvider.wall(p, RecipeCategory.BUILDING_BLOCKS, () -> c.get().asItem(), () -> source.get().asItem());
            p.stonecutting(DataIngredient.items(source.get()), RecipeCategory.BUILDING_BLOCKS, c, 1); // 1 to 1
        };
    }

    public static NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateRecipeProvider> sheetmetalRecipe(Tags.Tag<Item> sheet) {
        return sheetmetalRecipe(sheet.tag());
    }

    public static NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateRecipeProvider> sheetmetalRecipe(TagKey<Item> sheet) {
        return commonFourToFourRecipe(sheet);
    }

    public static NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateRecipeProvider> platingRecipe(Tags.Tag<Item> ingot) {
        return platingRecipe(ingot.tag());
    }

    public static NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateRecipeProvider> platingRecipe(TagKey<Item> sheet) {
        return commonFourToFourRecipe(sheet);
    }

    public static <B extends Block> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateRecipeProvider> commonFourToFourRecipe(TagKey<Item> tag) {
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

    public static NonNullBiConsumer<DataGenContext<Block, RotatedPillarBlock>, RegistrateRecipeProvider> pillarRecipe(Tags.Tag<Item> material) {
        return pillarRecipe(material.tag());
    }

    public static NonNullBiConsumer<DataGenContext<Block, RotatedPillarBlock>, RegistrateRecipeProvider> pillarRecipe(TagKey<Item> material) {
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

    public static NonNullBiConsumer<DataGenContext<Block, RotatedPillarBlock>, RegistrateRecipeProvider> pillarRecipe(ItemLike material) {
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

    public static NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateRecipeProvider> chiseledRecipe(ItemLike slab) {
        return (c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, c.get(), 1)
                .unlockedBy("has_item", RegistrateRecipeProvider.has(slab)) // TODO: should it unlock from the base block rather than the slab?
                .define('#', slab)
                .pattern("#")
                .pattern("#")
                .save(p);
    }

    public static NonNullBiConsumer<DataGenContext<Block, GrateBlock>, RegistrateRecipeProvider> grateRecipe(Tags.Tag<Item> material) {
        return (c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, c.get(), 4)
                .unlockedBy("has_item", RegistrateRecipeProvider.has(material.tag()))
                .define('#', material.tag())
                .pattern("###")
                .pattern("# #")
                .pattern("###")
                .save(p);
    }

}
