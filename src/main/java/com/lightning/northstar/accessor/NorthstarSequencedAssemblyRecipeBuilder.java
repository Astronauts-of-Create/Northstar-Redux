package com.lightning.northstar.accessor;

import com.lightning.northstar.data.Tags;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

public interface NorthstarSequencedAssemblyRecipeBuilder {

    // Create, didn't you forget something by any chance?
    default SequencedAssemblyRecipeBuilder northstar$withCondition(ICondition condition) {
        throw new RuntimeException("This should be implemented by a mixin!");
    }

    default SequencedAssemblyRecipeBuilder northstar$whenModLoaded(Tags.Mod mod) {
        return northstar$withCondition(new ModLoadedCondition(mod.getModId()));
    }

    default SequencedAssemblyRecipeBuilder northstar$whenModLoaded(String modId) {
        return northstar$withCondition(new ModLoadedCondition(modId));
    }

}
