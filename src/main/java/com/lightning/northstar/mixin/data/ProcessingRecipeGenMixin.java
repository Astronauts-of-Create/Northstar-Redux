package com.lightning.northstar.mixin.data;

import com.lightning.northstar.Northstar;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;

@Mixin(ProcessingRecipeGen.class)
public abstract class ProcessingRecipeGenMixin extends BaseRecipeProvider {

    public ProcessingRecipeGenMixin(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @ModifyExpressionValue(
            method = "create(Ljava/util/function/Supplier;Ljava/util/function/UnaryOperator;)Lcom/simibubi/create/api/data/recipe/BaseRecipeProvider$GeneratedRecipe;",
            at = @At(
                    value = "CONSTANT",
                    args = "stringValue=create"
            )
    )
    private String northstar$modifyModId(String original) {
        if (original.equals("create") && modid.equals(Northstar.MOD_ID)) {
            return modid;
        }
        return original;
    }

}
