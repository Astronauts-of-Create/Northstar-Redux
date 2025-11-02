package com.lightning.northstar.content;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.common.Tags;

public class NorthstarToolTiers {

    public static final Tier MARTIAN_STEEL;

    static {
        MARTIAN_STEEL = new SimpleTier(
                Tags.Blocks.NEEDS_NETHERITE_TOOL,
                1800,
                9f,
                5f,
                30,
                () -> Ingredient.of(NorthstarItems.MARTIAN_STEEL.get()));
    }

}
