package com.lightning.northstar.compat.jei.category;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class HeatShieldingCategory extends AbstractRecipeCategory<NorthstarBlockTags> {

    public static final RecipeType<NorthstarBlockTags> RECIPE_TYPE = RecipeType.create(Northstar.MOD_ID, "heat_shielding", NorthstarBlockTags.class);
    public static final int WIDTH = 177;
    public static final int HEIGHT = 110;

    public HeatShieldingCategory(IGuiHelper guiHelper) {
        super(RECIPE_TYPE, Component.translatable("northstar.recipe.heat_shielding"), guiHelper.createDrawableItemLike(NorthstarBlocks.ROCKET_STATION), WIDTH, HEIGHT);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, NorthstarBlockTags recipe, IFocusGroup focuses) {
        RegistryUtil.getRegistryAccess()
                .registryOrThrow(Registries.BLOCK)
                .stream()
                .filter(recipe::matches)
                .forEach(block -> builder.addInputSlot().addItemLike(block));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, NorthstarBlockTags recipe, IFocusGroup focuses) {
        builder.addText(Services.PLATFORM.getRenderHelper().getName(recipe.tag()), getWidth(), 10)
                .setColor(0x555555)
                .setTextAlignment(VerticalAlignment.CENTER)
                .setTextAlignment(HorizontalAlignment.CENTER);

        builder.addScrollGridWidget(builder.getRecipeSlots().getSlots(RecipeIngredientRole.INPUT), 9, 5)
                .setPosition(0, 10, getWidth(), getHeight() - 10, HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
    }

}
