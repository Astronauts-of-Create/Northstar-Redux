package com.lightning.northstar.data;

import com.google.gson.JsonElement;
import com.lightning.northstar.Northstar;
import com.lightning.northstar.advancements.NorthstarAdvancements;
import com.lightning.northstar.content.*;
import com.lightning.northstar.content.recipe.*;
import com.lightning.northstar.content.world.*;
import com.lightning.northstar.content.world.planet.core.*;
import com.lightning.northstar.content.world.planet.mars.MarsBiomes;
import com.lightning.northstar.content.world.planet.mars.MarsConfiguredFeatures;
import com.lightning.northstar.content.world.planet.mars.MarsPlacedFeatures;
import com.lightning.northstar.content.world.planet.mercury.MercuryBiomes;
import com.lightning.northstar.content.world.planet.mercury.MercuryConfiguredFeatures;
import com.lightning.northstar.content.world.planet.mercury.MercuryPlacedFeatures;
import com.lightning.northstar.content.world.planet.moon.MoonBiomes;
import com.lightning.northstar.content.world.planet.moon.MoonConfiguredFeatures;
import com.lightning.northstar.content.world.planet.moon.MoonPlacedFeatures;
import com.lightning.northstar.content.world.planet.venus.VenusBiomes;
import com.lightning.northstar.content.world.planet.venus.VenusConfiguredFeatures;
import com.lightning.northstar.content.world.planet.venus.VenusPlacedFeatures;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = Northstar.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NorthstarDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        NorthstarTagGen.register();
        Northstar.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            provideDefaultLang("base", provider::add);
            provideDefaultLang("tooltips", provider::add);
            provideDefaultLang("tags", provider::add);
            NorthstarAdvancements.provideLangEntries(provider::add);
        });

        RegistrySetBuilder builder = new RegistrySetBuilder()
                .add(Registries.BIOME, context -> {
                    NorthstarBiomes.bootstrap(context);
                    MarsBiomes.bootstrap(context);
                    MercuryBiomes.bootstrap(context);
                    MoonBiomes.bootstrap(context);
                    VenusBiomes.bootstrap(context);
                })
                .add(Registries.CONFIGURED_FEATURE, context -> {
                    NorthstarConfiguredFeatures.bootstrap(context);
                    NorthstarVegetationConfiguredFeatures.bootstrap(context);
                    MarsConfiguredFeatures.bootstrap(context);
                    MercuryConfiguredFeatures.bootstrap(context);
                    MoonConfiguredFeatures.bootstrap(context);
                    VenusConfiguredFeatures.bootstrap(context);
                })
                .add(Registries.DENSITY_FUNCTION, NorthstarDensityFunctions::bootstrap)
                .add(Registries.DAMAGE_TYPE, NorthstarDamageTypes::bootstrap)
                .add(Registries.DIMENSION_TYPE, NorthstarDimensionTypes::bootstrap)
                .add(Registries.LEVEL_STEM, NorthstarDimensions::bootstrapLevelStems)
                .add(Registries.NOISE_SETTINGS, NorthstarNoiseGeneratorSettings::bootstrap)
                .add(Registries.PLACED_FEATURE, context -> {
                    NorthstarPlacedFeatures.bootstrap(context);
                    NorthstarVegetationPlacedFeatures.bootstrap(context);
                    MarsPlacedFeatures.bootstrap(context);
                    MercuryPlacedFeatures.bootstrap(context);
                    MoonPlacedFeatures.bootstrap(context);
                    VenusPlacedFeatures.bootstrap(context);
                })
                .add(Registries.STRUCTURE, NorthstarStructures::bootstrap)
                .add(Registries.STRUCTURE_SET, NorthstarStructures.Sets::bootstrap)
                .add(Registries.TEMPLATE_POOL, NorthstarStructures.Templates::bootstrap)
                .add(NorthstarRegistries.FUEL, NorthstarFuelTypes::bootstrap)
                .add(NorthstarRegistries.PLANET, NorthstarPlanets::bootstrap)
                .add(NorthstarRegistries.PLANET_DIMENSION, NorthstarDimensions::bootstrapDimensions)
                .add(CreateRegistries.POTATO_PROJECTILE_TYPE, NorthstarPotatoCannonProjectiles::boostrap);

        DatapackBuiltinEntriesProvider provider = new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), builder, Set.of(Northstar.MOD_ID));
        generator.addProvider(event.includeServer(), provider);
        CompletableFuture<HolderLookup.Provider> lookupProvider = provider.getRegistryProvider();

        generator.addProvider(true, new PackMetadataGenerator(output).add(PackMetadataSection.TYPE, new PackMetadataSection(
                Component.literal("Northstar's Resources").withStyle(Northstar.PALETTE.primary()),
                DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
                Arrays.stream(PackType.values()).collect(Collectors.toMap(Function.identity(), DetectedVersion.BUILT_IN::getPackVersion))
        )));

        generator.addProvider(event.includeServer(), new NorthstarAdvancements(output));
        generator.addProvider(event.includeServer(), new NorthstarTagGen.BiomeTag(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new NorthstarTagGen.DamageTypeTag(output, lookupProvider, existingFileHelper));

        // Recipes:
        generator.addProvider(event.includeServer(), new NorthstarCompactingRecipeGen(output));
        generator.addProvider(event.includeServer(), new NorthstarCreateAdditionLiquidBurningRecipeGen(output));
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
