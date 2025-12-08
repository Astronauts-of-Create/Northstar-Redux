package com.lightning.northstar.compat.jei.category;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.contraption.FuelType;
import com.lightning.northstar.util.NorthstarLang;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.utility.CreateLang;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FuelTypeCategory extends AbstractRecipeCategory<FuelType> {

    public static final RecipeType<FuelType> RECIPE_TYPE = RecipeType.create(Northstar.MOD_ID, "fuel_type", FuelType.class);
    public static final int WIDTH = 142;
    public static final int HEIGHT = 60;

    public FuelTypeCategory(IGuiHelper guiHelper) {
        super(RECIPE_TYPE, Component.translatable("northstar.recipe.fuel_type"), guiHelper.createDrawableItemLike(NorthstarBlocks.JET_ENGINE), WIDTH, HEIGHT);
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
        List<Component> tooltip = new ArrayList<>();

        NorthstarLang.builder()
                .add(NorthstarBlocks.COMBUSTION_ENGINE.get().getName())
                .text(":")
                .forGoggles(tooltip);

        NorthstarLang.translate("recipe.fuel_type.consumption")
                .add(NorthstarLang.number(recipe.combustionEngineUse())
                        .add(NorthstarLang.MB_PER_TICK)
                        .style(ChatFormatting.GOLD))
                .forGoggles(tooltip, 1);

        NorthstarLang.translate("recipe.fuel_type.speed")
                .add(NorthstarLang.number(recipe.combustionEngineRpm())
                        .text(" ")
                        .add(CreateLang.translate("generic.unit.rpm"))
                        .style(ChatFormatting.AQUA))
                .forGoggles(tooltip, 1);

        NorthstarLang.builder()
                .add(NorthstarBlocks.JET_ENGINE.get().getName())
                .text(":")
                .forGoggles(tooltip);

        NorthstarLang.translate("recipe.fuel_type.energy")
                .add(NorthstarLang.number(recipe.gjPerMb())
                        .add(NorthstarLang.GJ_PER_MB)
                        .style(ChatFormatting.AQUA))
                .forGoggles(tooltip, 1);

        int y = 5;
        for (Component line : tooltip) {
            graphics.drawString(font, line, 20, y, 0xFFFFFFFF);
            y += 10;
        }
    }

}
