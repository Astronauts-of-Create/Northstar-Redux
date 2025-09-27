package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.simibubi.create.api.data.recipe.FillingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class NorthstarFillingRecipeGen extends FillingRecipeGen {

    GeneratedRecipe
            $ = null,

    CHOCOLATE_ICE_CREAM = create("chocolate_ice_cream",
            b -> b.require(NorthstarFluidTags.C_CHOCOLATE_ICE_CREAM.tag, 200)
                    .require(NorthstarItems.ICE_CREAM_CONE)
                    .output(NorthstarItems.CHOCOLATE_ICE_CREAM)),

    VANILLA_ICE_CREAM = create("vanilla_ice_cream",
            b -> b.require(NorthstarFluidTags.C_VANILLA_ICE_CREAM.tag, 200)
                    .require(NorthstarItems.ICE_CREAM_CONE)
                    .output(NorthstarItems.VANILLA_ICE_CREAM)),

    STRAWBERRY_ICE_CREAM = create("strawberry_ice_cream",
            b -> b.require(NorthstarFluidTags.C_STRAWBERRY_ICE_CREAM.tag, 200)
                    .require(NorthstarItems.ICE_CREAM_CONE)
                    .output(NorthstarItems.STRAWBERRY_ICE_CREAM)),

    SODIUM_CATALYST = create("sodium_catalyst",
            b -> b.require(FluidTags.LAVA, 500)
                    .require(NorthstarItemTags.C_DUSTS_SALT.tag)
                    .output(NorthstarItems.SODIUM_CATALYST))
    ;

    private void createOxidization(int oxidizeAmount, int reductionAmount, Block... blocks) {
        for (int i = 1; i < blocks.length; i++) {
            Block block1 = blocks[i - 1];
            Block block2 = blocks[i];

            String name1 = BuiltInRegistries.BLOCK.getKey(block1).getPath();
            String name2 = BuiltInRegistries.BLOCK.getKey(block2).getPath();

            create("oxidization/%s_to_%s".formatted(name1, name2),
                    b -> b.require(NorthstarFluidTags.C_LIQUID_OXYGEN.tag, oxidizeAmount)
                            .require(block1)
                            .output(block2));
            create("reduction/%s_to_%s".formatted(name2, name1),
                    b -> b.require(NorthstarFluidTags.C_SODIUM.tag, reductionAmount)
                            .require(block2)
                            .output(block1));
        }
    }

    public NorthstarFillingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Northstar.MOD_ID);

        createOxidization(250, 100, Blocks.COPPER_BLOCK, Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER);
        createOxidization(250, 100, Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER);
        createOxidization(180, 75, Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS);
        createOxidization(125, 50, Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB);
        createOxidization(250, 100, Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_OXIDIZED_COPPER);
        createOxidization(250, 100, Blocks.WAXED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER);
        createOxidization(180, 75, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
        createOxidization(125, 50, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB);
    }

}
