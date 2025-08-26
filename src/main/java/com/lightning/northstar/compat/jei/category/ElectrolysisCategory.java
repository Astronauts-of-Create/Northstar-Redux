package com.lightning.northstar.compat.jei.category;

import com.lightning.northstar.block.tech.electrolysis_machine.ElectrolysisRecipe;
import com.lightning.northstar.compat.jei.animations.AnimatedElectrolysisMachine;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ElectrolysisCategory extends CreateRecipeCategory<ElectrolysisRecipe> {

    private final AnimatedElectrolysisMachine electrolysisMachine = new AnimatedElectrolysisMachine();

    public ElectrolysisCategory(Info<ElectrolysisRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ElectrolysisRecipe recipe, IFocusGroup focuses) {
        addFluidSlot(builder, 30, 50, recipe.getFluidIngredients().get(0));
        addFluidSlot(builder, 127, 50, recipe.getFluidResults().get(0));
        addFluidSlot(builder, 148, 50, recipe.getFluidResults().get(1));
    }

    @Override
    public void draw(ElectrolysisRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(gui, 61, 41);
        AllGuiTextures.JEI_LONG_ARROW.render(gui, 52, 54);
        electrolysisMachine.draw(gui, getBackground().getWidth() / 2 - 17, 22);
    }

}