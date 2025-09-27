package com.lightning.northstar.compat.jei;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.circuit_engraver.EngravingRecipe;
import com.lightning.northstar.block.tech.electrolysis_machine.ElectrolysisRecipe;
import com.lightning.northstar.block.tech.ice_box.FreezingRecipe;
import com.lightning.northstar.compat.jei.category.ElectrolysisCategory;
import com.lightning.northstar.compat.jei.category.EngravingCategory;
import com.lightning.northstar.compat.jei.category.FreezingCategory;
import com.lightning.northstar.compat.jei.category.FuelTypeCategory;
import com.lightning.northstar.content.NorthstarRegistries;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.item.NorthstarRecipeTypes;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class NorthstarJEI implements IModPlugin {

    private static final ResourceLocation ID = Northstar.asResource("jei_plugin");

    private final List<CreateRecipeCategory<?>> northstarCategories = new ArrayList<>();

    private void loadCategories() {
        northstarCategories.clear();

        CreateRecipeCategory<?>
                start = null,
                engraving = builder(EngravingRecipe.class)
                        .addTypedRecipes(NorthstarRecipeTypes.ENGRAVING)
                        .catalyst(NorthstarBlocks.CIRCUIT_ENGRAVER::get)
                        .itemIcon(NorthstarBlocks.CIRCUIT_ENGRAVER.get())
                        .emptyBackground(177, 70)
                        .build("engraving", EngravingCategory::new),

                freezing = builder(FreezingRecipe.class)
                        .addTypedRecipes(NorthstarRecipeTypes.FREEZING)
                        .catalyst(NorthstarBlocks.ICE_BOX::get)
                        .itemIcon(NorthstarBlocks.ICE_BOX.get())
                        .emptyBackground(177, 70)
                        .build("freezing", FreezingCategory::new),

                electrolysis = builder(ElectrolysisRecipe.class)
                        .addTypedRecipes(NorthstarRecipeTypes.ELECTROLYSIS)
                        .catalyst(NorthstarBlocks.ELECTROLYSIS_MACHINE::get)
                        .itemIcon(NorthstarBlocks.ELECTROLYSIS_MACHINE.get())
                        .emptyBackground(177, 70)
                        .build("electrolysis", ElectrolysisCategory::new);
    }

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        registration.addRecipeCategories(northstarCategories.toArray(IRecipeCategory[]::new));

        registration.addRecipeCategories(new FuelTypeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        northstarCategories.forEach(c -> c.registerRecipes(registration));

        RegistryAccess registryAccess = RegistryUtil.getRegistryAccess();
        Registry<Fluid> fluids = registryAccess.registryOrThrow(Registries.FLUID);
        registration.addRecipes(FuelTypeCategory.RECIPE_TYPE, registryAccess
                .registryOrThrow(NorthstarRegistries.FUEL)
                .stream()
                .filter(fuel -> fluids.stream().anyMatch(fuel::supports))
                .toList());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        northstarCategories.forEach(c -> c.registerCatalysts(registration));
        registration.addRecipeCatalysts(FuelTypeCategory.RECIPE_TYPE, NorthstarBlocks.JET_ENGINE, NorthstarBlocks.COMBUSTION_ENGINE);
    }

    private class CategoryBuilder<T extends Recipe<?>> extends CreateRecipeCategory.Builder<T> {
        public CategoryBuilder(Class<? extends T> recipeClass) {
            super(recipeClass);
        }

        @Override
        public CreateRecipeCategory<T> build(ResourceLocation id, CreateRecipeCategory.Factory<T> factory) {
            CreateRecipeCategory<T> category = super.build(id, factory);
            northstarCategories.add(category);
            return category;
        }
    }

    public static void consumeAllRecipes(Consumer<RecipeHolder<?>> consumer) {
        Minecraft.getInstance()
                .getConnection()
                .getRecipeManager()
                .getRecipes()
                .forEach(consumer);
    }

    public static boolean doInputsMatch(Recipe<?> recipe1, Recipe<?> recipe2) {
        if (recipe1.getIngredients()
                .isEmpty()
                || recipe2.getIngredients()
                .isEmpty()) {
            return false;
        }
        ItemStack[] matchingStacks = recipe1.getIngredients()
                .get(0)
                .getItems();
        if (matchingStacks.length == 0) {
            return false;
        }
        return recipe2.getIngredients()
                .get(0)
                .test(matchingStacks[0]);
    }

    public static boolean doOutputsMatch(Recipe<?> recipe1, Recipe<?> recipe2) {
        RegistryAccess registry = Minecraft.getInstance().level.registryAccess();
        return ItemStack.isSameItem(recipe1.getResultItem(registry), recipe2.getResultItem(registry));
    }

}