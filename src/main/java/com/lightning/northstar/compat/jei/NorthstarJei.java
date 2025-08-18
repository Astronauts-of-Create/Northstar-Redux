package com.lightning.northstar.compat.jei;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.circuit_engraver.EngravingRecipe;
import com.lightning.northstar.block.tech.electrolysis_machine.ElectrolysisRecipe;
import com.lightning.northstar.block.tech.ice_box.FreezingRecipe;
import com.lightning.northstar.compat.jei.category.ElectrolysisCategory;
import com.lightning.northstar.compat.jei.category.EngravingCategory;
import com.lightning.northstar.compat.jei.category.FreezingCategory;
import com.lightning.northstar.content.NorthstarTechBlocks;
import com.lightning.northstar.item.NorthstarRecipeTypes;
import com.simibubi.create.AllFluids;
import com.simibubi.create.compat.jei.*;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.equipment.blueprint.BlueprintScreen;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.logistics.filter.AbstractFilterScreen;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerScreen;
import com.simibubi.create.content.trains.schedule.ScheduleScreen;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class NorthstarJei implements IModPlugin {

    private static final ResourceLocation ID = Northstar.asResource("jei_plugin");

    private final List<CreateRecipeCategory<?>> northstarCategories = new ArrayList<>();
    private IIngredientManager ingredientManager;

    private void loadCategories() {
        northstarCategories.clear();

        CreateRecipeCategory<?>
                engraving = builder(EngravingRecipe.class)
                        .addTypedRecipes(NorthstarRecipeTypes.ENGRAVING)
                        .catalyst(NorthstarTechBlocks.CIRCUIT_ENGRAVER::get)
                        .itemIcon(NorthstarTechBlocks.CIRCUIT_ENGRAVER.get())
                        .emptyBackground(177, 70)
                        .build("engraving", EngravingCategory::new),

                freezing = builder(FreezingRecipe.class)
                        .addTypedRecipes(NorthstarRecipeTypes.FREEZING)
                        .catalyst(NorthstarTechBlocks.ICE_BOX::get)
                        .itemIcon(NorthstarTechBlocks.ICE_BOX.get())
                        .emptyBackground(177, 70)
                        .build("freezing", FreezingCategory::new),

                electrolysis = builder(ElectrolysisRecipe.class)
                        //.addRecipes(() -> ElectrolysisCategory.RECIPES)
                        .catalyst(NorthstarTechBlocks.ELECTROLYSIS_MACHINE::get)
                        .itemIcon(NorthstarTechBlocks.ELECTROLYSIS_MACHINE.get())
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
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ingredientManager = registration.getIngredientManager();

        northstarCategories.forEach(c -> c.registerRecipes(registration));

        registration.addRecipes(RecipeTypes.CRAFTING, ToolboxColoringRecipeMaker.createRecipes().toList());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        northstarCategories.forEach(c -> c.registerCatalysts(registration));
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new BlueprintTransferHandler(), RecipeTypes.CRAFTING);
    }

    @Override
    public <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> platformFluidHelper) {
        PotionFluidSubtypeInterpreter interpreter = new PotionFluidSubtypeInterpreter();
        PotionFluid potionFluid = AllFluids.POTION.get();
        registration.registerSubtypeInterpreter(NeoForgeTypes.FLUID_STACK, potionFluid.getSource(), interpreter);
        registration.registerSubtypeInterpreter(NeoForgeTypes.FLUID_STACK, potionFluid.getFlowing(), interpreter);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(AbstractSimiContainerScreen.class, new SlotMover());

        registration.addGhostIngredientHandler(AbstractFilterScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(BlueprintScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(LinkedControllerScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(ScheduleScreen.class, new GhostIngredientHandler());
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
