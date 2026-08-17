package com.lightning.northstar.compat.jei.category;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.accessor.NorthstarLevel;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.planet.Planet;
import com.lightning.northstar.planet.data.AtmosphereFluid;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.lightning.northstar.util.NorthstarLang;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

// TODO: This would be better if the recipes could be grouped by fluid type or dimension
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AtmosphericConcentratorCategory extends AbstractRecipeCategory<AtmosphericConcentratorCategory.Entry> {

    public static final RecipeType<Entry> RECIPE_TYPE = new RecipeType<>(Northstar.asResource("atmospheric_concentrator"), Entry.class);
    public static final int WIDTH = 177;
    public static final int HEIGHT = 47;

    public AtmosphericConcentratorCategory(IGuiHelper guiHelper) {
        super(RECIPE_TYPE, Component.translatable("northstar.recipe.atmospheric_concentrator"), guiHelper.createDrawableItemLike(NorthstarBlocks.ATMOSPHERIC_CONCENTRATOR), WIDTH, HEIGHT);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Entry recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, 67 - 16 - 2, 5)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addItemLike(NorthstarBlocks.ATMOSPHERIC_CONCENTRATOR);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 67 + 42 + 2, 5)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addFluidStack(recipe.fluid().fluid(), 1000, recipe.fluid().fluidData());
    }

    @Override
    public void draw(Entry recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);

        AllGuiTextures.JEI_ARROW.render(graphics, 68, 8);

        Component name;
        Planet planet;
        if (recipe.dimension().planet() != null && (planet = NorthstarLevel.CLIENT_TRACKER.getPlanetById(recipe.dimension().planet().location())) != null) {
            name = planet.getDimensionName(recipe.dimension());
        } else {
            name = NorthstarLang.getDimensionName(recipe.dimension().dimensionId());
        }

        Component rate = Component.translatable(
                "northstar.recipe.atmospheric_concentrator.rate",
                NorthstarLang.number(recipe.fluid().collectionRate())
                        .add(NorthstarLang.MB_PER_TICK)
                        .style(ChatFormatting.AQUA)
                        .component(),
                Component.literal("256 ")
                        .append(Component.translatable("create.generic.unit.rpm"))
                        .withStyle(ChatFormatting.AQUA)
        );

        Font font = Minecraft.getInstance().font;
        graphics.drawCenteredString(font, name, 88, 25, 0xFFFFFF);
        graphics.drawCenteredString(font, rate, 88, 36, 0xFFFFFF);
    }

    public record Entry(PlanetDimension dimension, AtmosphereFluid fluid) {
    }

}
