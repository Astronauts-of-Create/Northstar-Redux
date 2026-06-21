package com.lightning.northstar.compat.jei.category;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.compat.jei.animations.AnimatedOxygenFiller;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OxygenFillerCategory extends AbstractRecipeCategory<ItemLike> {

    public static final RecipeType<ItemLike> RECIPE_TYPE = new RecipeType<>(Northstar.asResource("oxygen_filler"), ItemLike.class);
    public static final int WIDTH = 177;
    public static final int HEIGHT = 100;

    private final AnimatedOxygenFiller filler = new AnimatedOxygenFiller();

    public OxygenFillerCategory(IGuiHelper guiHelper) {
        super(RECIPE_TYPE, Component.translatable("northstar.recipe.oxygen_filler"), guiHelper.createDrawableItemLike(NorthstarBlocks.OXYGEN_FILLER), WIDTH, HEIGHT);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ItemLike recipe, IFocusGroup focuses) {
        CreateRecipeCategory.addFluidSlot(builder, 30, 32, SizedFluidIngredient.of(NorthstarFluidTags.BREATHABLE.tag(), 1));

        builder.addSlot(RecipeIngredientRole.INPUT, 30, 50)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addItemLike(recipe);

        ItemStack filled = new ItemStack(recipe);
        filled.set(NorthstarDataComponents.OXYGEN, NorthstarConfigs.server().spacesuitBaseOxygen.get());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 42 + 16)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addItemStack(filled);
    }

    @Override
    public void draw(ItemLike recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);

        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 114, 42);

        filler.withItem(new ItemStack(recipe)).draw(graphics, 88 + 20, 50 + 20);
    }

}
