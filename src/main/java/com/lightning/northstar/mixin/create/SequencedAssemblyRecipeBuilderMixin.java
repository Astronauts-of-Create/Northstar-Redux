package com.lightning.northstar.mixin.create;

import com.lightning.northstar.accessor.NorthstarSequencedAssemblyRecipeBuilder;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = SequencedAssemblyRecipeBuilder.class, remap = false)
public class SequencedAssemblyRecipeBuilderMixin implements NorthstarSequencedAssemblyRecipeBuilder {

    @Shadow
    protected List<ICondition> recipeConditions;

    @Override
    public SequencedAssemblyRecipeBuilder northstar$withCondition(ICondition condition) {
        recipeConditions.add(condition);
        return (SequencedAssemblyRecipeBuilder) (Object) this;
    }

}
