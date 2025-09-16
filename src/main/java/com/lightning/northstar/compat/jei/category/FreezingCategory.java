package com.lightning.northstar.compat.jei.category;

import com.lightning.northstar.block.tech.ice_box.FreezingRecipe;
import com.lightning.northstar.compat.jei.animations.AnimatedIceBox;
import com.lightning.northstar.util.BackportUtil;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.Pair;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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

            builder.addSlot(RecipeIngredientRole.INPUT, 17 + xOffset + (i % 3) * 19, 51 - (i / 3) * 19)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addItemStacks(stacks);
            i++;
        }
        for (FluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
            BackportUtil.addFluidSlot(builder, 17 + xOffset + (i % 3) * 19, 51 - (i / 3) * 19, fluidIngredient);
            i++;
        }

        size = recipe.getRollableResults().size() + recipe.getFluidResults().size();
        i = 0;

        for (ProcessingOutput result : recipe.getRollableResults()) {
            int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int yPosition = -19 * (i / 2) + 51;

            builder
                    .addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                    .setBackground(getRenderedSlot(result), -1, -1)
                    .addItemStack(result.getStack())
                    .addTooltipCallback(CreateRecipeCategory.addStochasticTooltip(result));
            i++;
        }

        for (FluidStack fluidResult : recipe.getFluidResults()) {
            int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int yPosition = -19 * (i / 2) + 51;
            BackportUtil.addFluidSlot(builder, xPosition, yPosition, fluidResult);
            i++;
        }
    }

    @Override
    public void draw(FreezingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 61, 41);
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 54);

        iceBox.draw(graphics, getBackground().getWidth() / 2 - 17, 22);

        int vRows = (1 + recipe.getFluidResults().size() + recipe.getRollableResults().size()) / 2;

        String text = -recipe.getProcessingDuration() + " C°";
        Minecraft minecraft = Minecraft.getInstance();
        Font fontRenderer = minecraft.font;
        int stringCenter = fontRenderer.width(text) / 2;
        graphics.drawString(fontRenderer, text, (getBackground().getWidth() / 2) + 2 - stringCenter, 62, 0xFFFFFF);

        if (vRows <= 2)
            AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 136, -19 * (vRows - 1) + 32);
    }

}