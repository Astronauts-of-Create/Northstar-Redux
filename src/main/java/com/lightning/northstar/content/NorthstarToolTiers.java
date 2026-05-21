package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

public class NorthstarToolTiers {

    public static final Tier MARTIAN_STEEL;

    static {
        MARTIAN_STEEL = TierSortingRegistry.registerTier(
                new ForgeTier(4,
                        1800,
                        9f,
                        5f,
                        30,
                        Tags.Blocks.NEEDS_NETHERITE_TOOL,
                        () -> Ingredient.of(NorthstarItems.MARTIAN_STEEL_INGOT.get())),
                Northstar.asResource("martian_steel"),
                List.of(Tiers.DIAMOND),
                List.of(Tiers.NETHERITE)
        );
    }

}
