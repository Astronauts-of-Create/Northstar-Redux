package com.lightning.northstar.block.tech.ice_box;

import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public abstract class FreezingRecipeGen extends BaseRecipeProvider {

    public FreezingRecipeGen(PackOutput generator, String defaultNamespace) {
        super(generator, defaultNamespace);
    }

    protected GeneratedRecipe create(String name, Consumer<FreezingRecipeBuilder> transform) {
        FreezingRecipeBuilder builder = new FreezingRecipeBuilder(FreezingRecipe::new, asResource(name));
        transform.accept(builder);
        GeneratedRecipe generatedRecipe = builder::build;
        all.add(generatedRecipe);
        return generatedRecipe;
    }

    @Override
    public String getName() {
        return modid + "'s freezing recipes";
    }

    public static class FreezingRecipeBuilder extends ProcessingRecipeBuilder<FreezingRecipe> {

        private FreezingRecipe.Params params;

        public FreezingRecipeBuilder(ProcessingRecipeFactory<FreezingRecipe> factory, ResourceLocation recipeId) {
            super(factory, recipeId);
            super.params = params = new FreezingRecipe.Params(recipeId);
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
    }

}
