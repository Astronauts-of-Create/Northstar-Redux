package com.lightning.northstar.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lightning.northstar.Northstar;
import com.lightning.northstar.advancements.NorthstarAdvancements;
import com.lightning.northstar.content.NorthstarDamageTypes;
import com.lightning.northstar.data.recipe.*;
import com.lightning.northstar.item.NorthstarEnchantments;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@EventBusSubscriber(modid = Northstar.MOD_ID)
public class NorthstarDataGen {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGatherDataHighPriority(GatherDataEvent event) {
        if (!event.getMods().contains(Northstar.MOD_ID))
            return;
        NorthstarTagGen.register();
        Northstar.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> provideDefaultLang("base", provider::add));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGatherData(GatherDataEvent event) {
        if (!event.getMods().contains(Northstar.MOD_ID))
            return;
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        RegistrySetBuilder builder = new RegistrySetBuilder()
                // TODO: those don't match the existing ones, which one are the real ones?
                //.add(Registries.CONFIGURED_FEATURE, NorthstarConfiguredFeatures::bootstrap)
                .add(Registries.DAMAGE_TYPE, NorthstarDamageTypes::bootstrap)
                .add(Registries.ENCHANTMENT, NorthstarEnchantments::bootstrap);

        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(output, lookupProvider, builder, Set.of(Northstar.MOD_ID)));

        generator.addProvider(event.includeServer(), new NorthstarAdvancements(output, lookupProvider));

        // Recipes:
        generator.addProvider(event.includeServer(), new NorthstarCompactingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new NorthstarCrushingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new NorthstarElectrolysisRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new NorthstarEngravingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new NorthstarFillingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new NorthstarFreezingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new NorthstarMechanicalCraftingGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new NorthstarMixingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new NorthstarPolishingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new NorthstarPressingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new NorthstarSequencedAssemblyRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new NorthstarStandardRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new NorthstarWashingRecipeGen(output, lookupProvider));
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
