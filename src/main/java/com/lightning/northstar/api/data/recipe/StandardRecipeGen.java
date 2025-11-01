package com.lightning.northstar.api.data.recipe;

import com.google.common.base.Supplier;
import com.lightning.northstar.Northstar;
import com.lightning.northstar.data.Mod;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public abstract class StandardRecipeGen extends BaseRecipeProvider {

    public void createMaterial(ItemLike nuggets, ItemLike ingot, ItemLike block) {
        if (nuggets != null) {
            create(() -> nuggets)
                    .returns(9)
                    .unlockedBy(() -> ingot)
                    .withSuffix("_from_ingot")
                    .viaShapeless(b -> b.requires(ingot));

            create(() -> ingot)
                    .unlockedBy(() -> nuggets)
                    .withSuffix("_from_nuggets")
                    .viaShapeless(b -> b.requires(nuggets, 9));
        }
        if (block != null) {
            create(() -> ingot)
                    .returns(9)
                    .unlockedBy(() -> block)
                    .withSuffix("_from_block")
                    .viaShapeless(b -> b.requires(block));

            create(() -> block)
                    .unlockedBy(() -> ingot)
                    .withSuffix("_from_ingot")
                    .viaShapeless(b -> b.requires(ingot, 9));
        }
    }

    public void interchangeable(ItemLike item1, ItemLike item2) {
        create(() -> item1)
                .unlockedBy(() -> item2)
                .withSuffix("_from_" + RegisteredObjectsHelper.getKeyOrThrow(item2.asItem()).getPath())
                .viaShapeless(b -> b.requires(item2));
        create(() -> item2)
                .unlockedBy(() -> item1)
                .withSuffix("_from_" + RegisteredObjectsHelper.getKeyOrThrow(item1.asItem()).getPath())
                .viaShapeless(b -> b.requires(item1));
    }

    public void createOre(Supplier<TagKey<Item>> ore, Supplier<? extends ItemLike> ingot, float xp, int smeltDuration) {
        create(ingot)
                .unlockedByTag(ore)
                .viaCookingTag(ore)
                .rewardXP(xp)
                .forDuration(smeltDuration)
                .inBlastFurnace();
    }

    protected Builder create(Supplier<? extends ItemLike> result) {
        return new Builder(result);
    }

    protected Builder create(ItemProviderEntry<? extends ItemLike, ? extends ItemLike> result) {
        return create(result::get);
    }

    public class Builder {

        private Supplier<? extends ItemLike> result;
        private int amount;
        private String suffix;
        private List<ICondition> conditions;
        private Supplier<ItemPredicate> unlockedBy;

        public Builder(Supplier<? extends ItemLike> result) {
            this.result = result;
            this.amount = 1;
            this.suffix = "";
            this.conditions = new ArrayList<>();
            this.unlockedBy = null;
        }

        public Builder returns(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder withSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public Builder unlockedBy(Supplier<? extends ItemLike> item) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                    .of(item.get())
                    .build();
            return this;
        }

        public Builder unlockedByTag(Supplier<TagKey<Item>> tag) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                    .of(tag.get())
                    .build();
            return this;
        }

        public Builder whenModLoaded(Mod mod) {
            return whenModLoaded(mod.getModId());
        }

        public Builder whenModLoaded(String modId) {
            return withCondition(new ModLoadedCondition(modId));
        }

        public Builder whenModMissing(Mod mod) {
            return whenModMissing(mod.getModId());
        }

        public Builder whenModMissing(String modId) {
            return withCondition(new NotCondition(new ModLoadedCondition(modId)));
        }

        public Builder withCondition(ICondition condition) {
            conditions.add(condition);
            return this;
        }

        public GeneratedRecipe viaShaped(UnaryOperator<ShapedRecipeBuilder> builder) {
            return register(recipe -> {
                ShapedRecipeBuilder b = builder.apply(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(recipe.withConditions(conditions.toArray(new ICondition[0])), createLocation("crafting"));
            });
        }

        public GeneratedRecipe viaShapeless(UnaryOperator<ShapelessRecipeBuilder> builder) {
            return register(recipe -> {
                ShapelessRecipeBuilder b = builder.apply(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(recipe.withConditions(conditions.toArray(new ICondition[0])), createLocation("crafting"));
            });
        }

        public Cooking viaCooking(Supplier<? extends ItemLike> item) {
            return unlockedBy(item).viaCookingIngredient(() -> Ingredient.of(item.get()));
        }

        public Cooking viaCookingTag(Supplier<TagKey<Item>> tag) {
            return unlockedByTag(tag).viaCookingIngredient(() -> Ingredient.of(tag.get()));
        }

        public Cooking viaCookingIngredient(Supplier<Ingredient> ingredient) {
            return new Cooking(ingredient);
        }

        private ResourceLocation createLocation(String recipeType) {
            return Northstar.asResource(recipeType + "/" + getRegistryName().getPath() + suffix);
        }

        private ResourceLocation getRegistryName() {
            return RegisteredObjectsHelper.getKeyOrThrow(result.get().asItem());
        }

        public class Cooking {

            private Supplier<Ingredient> ingredient;
            private int duration;
            private float xp;

            private Cooking(Supplier<Ingredient> ingredient) {
                this.ingredient = ingredient;
                this.duration = 200;
                this.xp = 0f;
            }

            public Cooking forDuration(int duration) {
                this.duration = duration;
                return this;
            }

            public Cooking rewardXP(float xp) {
                this.xp = xp;
                return this;
            }

            public GeneratedRecipe inFurnace() {
                return inFurnace(b -> b);
            }

            public GeneratedRecipe inFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                return create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1);
            }

            public GeneratedRecipe inSmoker() {
                return inSmoker(b -> b);
            }

            public GeneratedRecipe inSmoker(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1);
                create(RecipeSerializer.CAMPFIRE_COOKING_RECIPE, builder, CampfireCookingRecipe::new, 3);
                return create(RecipeSerializer.SMOKING_RECIPE, builder, SmokingRecipe::new, .5f);
            }

            public GeneratedRecipe inBlastFurnace() {
                return inBlastFurnace(b -> b);
            }

            public GeneratedRecipe inBlastFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1);
                return create(RecipeSerializer.BLASTING_RECIPE, builder, BlastingRecipe::new, .5f);
            }

            private <T extends AbstractCookingRecipe> GeneratedRecipe create(RecipeSerializer<T> serializer,
                                                                             UnaryOperator<SimpleCookingRecipeBuilder> builder,
                                                                             AbstractCookingRecipe.Factory<T> factory, float cookingTimeModifier) {
                return register(consumer -> {
                    SimpleCookingRecipeBuilder b = builder.apply(SimpleCookingRecipeBuilder.generic(ingredient.get(),
                            RecipeCategory.MISC,
                            result.get(),
                            xp,
                            (int) (duration * cookingTimeModifier),
                            serializer, factory));

                    if (unlockedBy != null)
                        b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));

                    b.save(consumer, createLocation(RegisteredObjectsHelper.getKeyOrThrow(serializer).getPath()));
                });
            }
        }
    }

    public StandardRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

}
