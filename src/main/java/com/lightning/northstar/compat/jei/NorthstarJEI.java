package com.lightning.northstar.compat.jei;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.circuit_engraver.EngravingRecipe;
import com.lightning.northstar.block.tech.electrolysis_machine.ElectrolysisRecipe;
import com.lightning.northstar.block.tech.ice_box.FreezingRecipe;
import com.lightning.northstar.compat.jei.category.*;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarRecipeTypes;
import com.lightning.northstar.content.NorthstarRegistries;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.data.ModCompat;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@JeiPlugin
@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NorthstarJEI implements IModPlugin {

    private static final ResourceLocation ID = Northstar.asResource("jei_plugin");
    private static IJeiRuntime runtime;
    private final List<CreateRecipeCategory<?>> northstarCategories = new ArrayList<>();

    private void loadCategories() {
        northstarCategories.clear();

        builder(EngravingRecipe.class)
                .addTypedRecipes(NorthstarRecipeTypes.ENGRAVING)
                .catalyst(NorthstarBlocks.CIRCUIT_ENGRAVER::get)
                .itemIcon(NorthstarBlocks.CIRCUIT_ENGRAVER.get())
                .emptyBackground(177, 70)
                .build("engraving", EngravingCategory::new);

        builder(FreezingRecipe.class)
                .addTypedRecipes(NorthstarRecipeTypes.FREEZING)
                .catalyst(NorthstarBlocks.ICE_BOX::get)
                .itemIcon(NorthstarBlocks.ICE_BOX.get())
                .emptyBackground(177, 70)
                .build("freezing", FreezingCategory::new);

        builder(ElectrolysisRecipe.class)
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
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        NorthstarJEI.runtime = runtime;
        ModCompat.HAS_JEI_RUNTIME = true;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        registration.addRecipeCategories(northstarCategories.toArray(IRecipeCategory[]::new));

        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(
                new AtmosphericConcentratorCategory(guiHelper),
                new FuelTypeCategory(guiHelper),
                new HeatShieldingCategory(guiHelper),
                new OxygenFillerCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        northstarCategories.forEach(c -> c.registerRecipes(registration));
        RegistryAccess registryAccess = RegistryUtil.getRegistryAccess();

        registration.addRecipes(
                AtmosphericConcentratorCategory.RECIPE_TYPE,
                registryAccess.registryOrThrow(NorthstarRegistries.PLANET_DIMENSION)
                        .stream()
                        .filter(PlanetDimension::hasAtmosphere)
                        .toList()
        );

        Registry<Fluid> fluids = registryAccess.registryOrThrow(Registries.FLUID);
        registration.addRecipes(FuelTypeCategory.RECIPE_TYPE, registryAccess
                .registryOrThrow(NorthstarRegistries.FUEL)
                .stream()
                .filter(fuel -> fluids.stream().anyMatch(fuel::supports))
                .toList());

        registryAccess.registryOrThrow(Registries.ITEM)
                .getTagOrEmpty(NorthstarItemTags.OXYGEN_SOURCES.tag)
                .forEach(item -> registration.addRecipes(OxygenFillerCategory.RECIPE_TYPE, List.of(item.value())));

        registration.addRecipes(HeatShieldingCategory.RECIPE_TYPE, List.of(
                NorthstarBlockTags.TIER_1_HEAT_RESISTANCE,
                NorthstarBlockTags.TIER_2_HEAT_RESISTANCE,
                NorthstarBlockTags.TIER_3_HEAT_RESISTANCE
        ));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        northstarCategories.forEach(c -> c.registerCatalysts(registration));

        registration.addRecipeCatalysts(AtmosphericConcentratorCategory.RECIPE_TYPE, NorthstarBlocks.ATMOSPHERIC_CONCENTRATOR);
        registration.addRecipeCatalysts(FuelTypeCategory.RECIPE_TYPE, NorthstarBlocks.ROCKET_THRUSTER, NorthstarBlocks.COMBUSTION_ENGINE);
        registration.addRecipeCatalysts(OxygenFillerCategory.RECIPE_TYPE, NorthstarBlocks.OXYGEN_FILLER);
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

    public static IJeiRuntime getRuntime() {
        return runtime;
    }

}