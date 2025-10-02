package com.lightning.northstar.data.worldgen;

import com.lightning.northstar.Northstar;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class PlanetOre {
    private final OreConfiguration oreConfiguration;
    private final List<PlacementModifier> placementModifiers;
    public final ResourceKey<ConfiguredFeature<?, ?>> configuredFeature;
    public final ResourceKey<PlacedFeature> placedFeature;

    public PlanetOre(ResourceLocation resourceKey,
                     OreConfiguration oreConfiguration,
                     List<PlacementModifier> placementModifiers) {

        this.oreConfiguration = oreConfiguration;
        this.placementModifiers = placementModifiers;

        configuredFeature = ResourceKey.create(Registries.CONFIGURED_FEATURE, resourceKey);
        placedFeature = ResourceKey.create(Registries.PLACED_FEATURE, resourceKey);
    }

    /**
     * Register configured feature
     *
     * @param context
     */
    public void bootstrapConfiguredFeature(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(configuredFeature, new ConfiguredFeature<>(Feature.ORE, oreConfiguration));
    }

    /**
     * Register placed feature
     * MUST be called after configured feature is registered
     *
     * @param context
     * @param configured
     */
    public void bootstrapPlacedFeature(BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> configured) {
        context.register(placedFeature, new PlacedFeature(configured.getOrThrow(configuredFeature), placementModifiers));
    }
}
