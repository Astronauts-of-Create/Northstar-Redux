package com.lightning.northstar.compat.jei.category;

import com.lightning.northstar.block.tech.ice_box.FreezingRecipe;
import com.lightning.northstar.compat.jei.animations.AnimatedIceBox;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarBlocks;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class FreezingCategory extends CreateRecipeCategory<FreezingRecipe> {

    private final AnimatedIceBox iceBox = new AnimatedIceBox();

    public FreezingCategory(Info<FreezingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FreezingRecipe recipe, IFocusGroup focuses) {
        List<Pair<Ingredient, MutableInt>> condensedIngredients = ItemHelper.condenseIngredients(recipe.getIngredients());

        int size = condensedIngredients.size() + recipe.getFluidIngredients().size();
        int xOffset = size < 3 ? (3 - size) * 19 / 2 : 0;
        int i = 0;

        for (Pair<Ingredient, MutableInt> pair : condensedIngredients) {
            List<ItemStack> stacks = new ArrayList<>();
            for (ItemStack itemStack : pair.getFirst().getItems()) {
                ItemStack copy = itemStack.copy();
                copy.setCount(pair.getSecond().getValue());
                stacks.add(copy);
            }

            builder.addSlot(RecipeIngredientRole.INPUT, 17 + xOffset + (i % 3) * 19, 20 - (i / 3) * 19)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addItemStacks(stacks);
            i++;
        }
        for (FluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
            addFluidSlot(builder, 17 + xOffset + (i % 3) * 19, 20 - (i / 3) * 19, fluidIngredient);
            i++;
        }

        size = recipe.getRollableResults().size() + recipe.getFluidResults().size();
        i = 0;

        for (ProcessingOutput result : recipe.getRollableResults()) {
            int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int yPosition = -19 * (i / 2) + 20;

            builder.addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                    .setBackground(getRenderedSlot(result), -1, -1)
                    .addItemStack(result.getStack())
                    .addRichTooltipCallback(CreateRecipeCategory.addStochasticTooltip(result));
            i++;
        }

        for (FluidStack fluidResult : recipe.getFluidResults()) {
            int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int yPosition = -19 * (i / 2) + 20;
            addFluidSlot(builder, xPosition, yPosition, fluidResult);
            i++;
        }

        builder.addSlot(RecipeIngredientRole.CATALYST, 153, 48)
                .addItemStack(NorthstarBlocks.TEMPERATURE_REGULATOR.asStack());
    }

    @Override
    public void draw(FreezingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 81, 38);
        iceBox.draw(graphics, 92, 41);

        Component text;
        if (recipe.getMaxTemperature() == Integer.MAX_VALUE) {
            text = Component.translatable("northstar.recipe.freezing.hotter_than", formatTemperature(recipe.getMinTemperature()));
        } else if (recipe.getMinTemperature() == Integer.MIN_VALUE) {
            text = Component.translatable("northstar.recipe.freezing.colder_than", formatTemperature(recipe.getMaxTemperature()));
        } else {
            text = Component.translatable("northstar.recipe.freezing.within",
                    formatTemperature(recipe.getMinTemperature()),
                    formatTemperature(recipe.getMaxTemperature()));
        }

        AllGuiTextures.JEI_NO_HEAT_BAR.render(graphics, 4, 47);
        graphics.drawString(Minecraft.getInstance().font, text, 9, 53, 0xFFFFFF);
    }

    private static String formatTemperature(int temperature) {
        return NorthstarConfigs.client().temperatureUnit.get().format(temperature);
    }

}
