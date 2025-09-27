package com.lightning.northstar.block.tech.ice_box;

import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class FreezingRecipeGen extends BaseRecipeProvider {

    public FreezingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    protected GeneratedRecipe createGas(String name, int temperature,
                                        TagKey<Fluid> fluidTag, Supplier<? extends Fluid> fluid,
                                        TagKey<Fluid> gasTag, Supplier<? extends Fluid> gas) {
        create(name + "_condensation",
                b -> b.colderThan(temperature)
                        .duration(20)
                        .require(gasTag, 100)
                        .output(fluid.get(), 100));

        return create(name + "_evaporation",
                b -> b.hotterThan(temperature)
                        .duration(20)
                        .require(fluidTag, 100)
                        .output(gas.get(), 100));
    }

    protected GeneratedRecipe create(String name, Consumer<FreezingRecipeBuilder> transform) {
        FreezingRecipeBuilder builder = new FreezingRecipeBuilder(FreezingRecipe::new, asResource(name));
        transform.accept(builder); // cannot be an UnaryOperator because it will end up with a ProcessingRecipeBuilder
        GeneratedRecipe generatedRecipe = builder::build;
        all.add(generatedRecipe);
        return generatedRecipe;
    }

    @Override
    public String getName() {
        return modid + "'s freezing recipes";
    }

    public static class FreezingRecipeBuilder extends ProcessingRecipeBuilder<FreezingRecipe.Params, FreezingRecipe, FreezingRecipeBuilder> {

        public FreezingRecipeBuilder(ProcessingRecipe.Factory<FreezingRecipe.Params, FreezingRecipe> factory, ResourceLocation recipeId) {
            super(factory, recipeId);
        }

        public FreezingRecipeBuilder colderThan(int temp) {
            return withinTemperature(Integer.MIN_VALUE, temp);
        }

        public FreezingRecipeBuilder hotterThan(int temp) {
            return withinTemperature(temp, Integer.MAX_VALUE);
        }

        public FreezingRecipeBuilder withTemperature(int temp) {
            return withinTemperature(temp, temp);
        }

        public FreezingRecipeBuilder withinTemperature(int min, int max) {
            params.minTemperature = min;
            params.maxTemperature = max;
            return this;
        }

        @Override
        protected FreezingRecipe.Params createParams() {
            return new FreezingRecipe.Params();
        }

        @Override
        public FreezingRecipeBuilder self() {
            return this;
        }

    }

}
