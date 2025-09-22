package com.lightning.northstar.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lightning.northstar.Northstar;
import com.lightning.northstar.advancements.NorthstarAdvancements;
import com.lightning.northstar.content.NorthstarDamageTypes;
import com.lightning.northstar.data.recipe.*;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@EventBusSubscriber(modid = Northstar.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NorthstarDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        NorthstarTagGen.register();
        Northstar.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> provideDefaultLang("base", provider::add));

        RegistrySetBuilder builder = new RegistrySetBuilder()
                // TODO: those don't match the existing ones, which one are the real ones?
                //.add(Registries.CONFIGURED_FEATURE, NorthstarConfiguredFeatures::bootstrap)
                .add(Registries.DAMAGE_TYPE, NorthstarDamageTypes::bootstrap);

        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(output, lookupProvider, builder, Set.of(Northstar.MOD_ID)));

        generator.addProvider(event.includeServer(), new NorthstarAdvancements(output));

        // Recipes:
        generator.addProvider(event.includeServer(), new NorthstarCompactingRecipeGen(output));
        generator.addProvider(event.includeServer(), new NorthstarCrushingRecipeGen(output));
        generator.addProvider(event.includeServer(), new NorthstarElectrolysisRecipeGen(output));
        generator.addProvider(event.includeServer(), new NorthstarEngravingRecipeGen(output));
        generator.addProvider(event.includeServer(), new NorthstarFillingRecipeGen(output));
        generator.addProvider(event.includeServer(), new NorthstarFreezingRecipeGen(output));
        generator.addProvider(event.includeServer(), new NorthstarMechanicalCraftingGen(output));
        generator.addProvider(event.includeServer(), new NorthstarMixingRecipeGen(output));
        generator.addProvider(event.includeServer(), new NorthstarPolishingRecipeGen(output));
        generator.addProvider(event.includeServer(), new NorthstarPressingRecipeGen(output));
        generator.addProvider(event.includeServer(), new NorthstarSequencedAssemblyRecipeGen(output));
        generator.addProvider(event.includeServer(), new NorthstarStandardRecipeGen(output));
        generator.addProvider(event.includeServer(), new NorthstarWashingRecipeGen(output));
    }

    private static void provideDefaultLang(String name, BiConsumer<String, String> consumer) {
        String path = "assets/northstar/lang/default/" + name + ".json";
        JsonElement json = FilesHelper.loadJsonResource(path);
        if (json == null)
            throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue().getAsString());
        }
    }

}
