package com.lightning.northstar.block.tech.circuit_engraver;

import com.lightning.northstar.compat.jei.subcategory.AssemblyEngraving;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.item.NorthstarRecipeTypes;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EngravingRecipe extends ProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {

    public EngravingRecipe(ProcessingRecipeParams params) {
        super(NorthstarRecipeTypes.ENGRAVING, params);
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level worldIn) {
        return ingredients.get(0).test(inv.getItem(0));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getDescriptionForAssembly() {
        return Component.translatable("northstar.recipe.assembly.engraving");
    }

    @Override
    public void addRequiredMachines(Set<ItemLike> list) {
        list.add(NorthstarBlocks.CIRCUIT_ENGRAVER.get());
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> AssemblyEngraving::new;
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

}
