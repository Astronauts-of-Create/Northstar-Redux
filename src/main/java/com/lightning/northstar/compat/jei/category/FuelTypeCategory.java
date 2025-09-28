package com.lightning.northstar.compat.jei.category;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.contraption.FuelType;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.common.util.RegistryUtil;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class FuelTypeCategory extends AbstractRecipeCategory<FuelType> {

    public static final RecipeType<FuelType> RECIPE_TYPE = RecipeType.create(Northstar.MOD_ID, "fuel_type", FuelType.class);
    private static final int WIDTH = 142;
    private static final int HEIGHT = 110;

    public FuelTypeCategory(IGuiHelper guiHelper) {
        super(RECIPE_TYPE, Component.literal("Fuel Type"), guiHelper.createDrawableItemLike(NorthstarBlocks.JET_ENGINE), WIDTH, HEIGHT);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FuelType recipe, IFocusGroup focuses) {
        List<FluidStack> fluids = RegistryUtil.getRegistryAccess()
                .registryOrThrow(Registries.FLUID)
                .stream()
                .filter(recipe::supports)
                .map(fluid -> new FluidStack(fluid, 1))
                .toList();

        CreateRecipeCategory.addFluidSlot(builder, 5, 5, RecipeIngredientRole.INPUT)
                .addIngredients(ForgeTypes.FLUID_STACK, fluids);
    }

    @Override
    public void draw(FuelType recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        Font font = Minecraft.getInstance().font;

        graphics.drawString(font, "Combustion engine:", 23, 5, 0xFFFFFFFF);
        graphics.drawString(font, "  Usage: " + recipe.combustionEngineEfficiency() + "mB/t", 23, 15, 0xFFFFFFFF);
        graphics.drawString(font, "  Speed: " + LangNumberFormat.format(recipe.combustionEngineRpm()) + " RPM", 23, 25, 0xFFFFFFFF);
        graphics.drawString(font, "Rocket engine:", 23, 35, 0xFFFFFFFF);
        graphics.drawString(font, "  Energy: " + LangNumberFormat.format(recipe.gjPerMb()) + "gJ/mB", 23, 45, 0xFFFFFFFF);
    }
}
