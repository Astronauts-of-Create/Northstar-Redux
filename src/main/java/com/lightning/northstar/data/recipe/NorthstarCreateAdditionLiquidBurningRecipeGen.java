package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarFluids;
import com.mrh0.createaddition.datagen.RecipeGen.LiquidBurningRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NorthstarCreateAdditionLiquidBurningRecipeGen extends LiquidBurningRecipeGen {

    GeneratedRecipe
            $ = null,

    BIOFUEL = create("biofuel", NorthstarFluids.BIOFUEL.get(), false),

    HYDROGEN = create("hydrogen", NorthstarFluids.HYDROGEN.get(), false),

    LIQUID_HYDROGEN = create("liquid_hydrogen", NorthstarFluids.LIQUID_HYDROGEN.get(), true),

    METHANE = create("methane", NorthstarFluids.METHANE.get(), false),

    HYDROCARBON = create("hydrocarbon", NorthstarFluids.HYDROCARBON.get(), false);

    public NorthstarCreateAdditionLiquidBurningRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Northstar.MOD_ID);
    }

    @Override
    public @NotNull String getName() {
        return modid + "'s create addition liquid burning recipes";
    }

    public GeneratedRecipe create(String name, FlowingFluid fluid, boolean superheated) {
        // values are the same as they would be for buckets, see somewhere inside BlazeBurnerBlockEntity
        return create(name, fluid, superheated ? 3200 : 1600, superheated);
    }

    public GeneratedRecipe create(String name, FlowingFluid fluid, int burnTime, boolean superheated) {
        return create(name, b -> {
            if (superheated)
                b.superheated();
            return b.require(fluid, 1000)
                    .burnTime(burnTime)
                    .whenModLoaded("createaddition");
        });
    }

}
