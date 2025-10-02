package com.lightning.northstar.data.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

public class NorthstarPlacedFeatures {

//    public static final ResourceKey<PlacedFeature> MOON_ORE_COPPER =
//            ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource("moon_copper_ore"));
//
//    public static final ResourceKey<PlacedFeature> MOON_ORE_DIAMOND =
//            ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource("moon_diamond_ore"));
//
//    public static final ResourceKey<PlacedFeature> MOON_ORE_GLOWSTONE =
//            ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource("moon_glowstone_ore"));
//
//    public static final ResourceKey<PlacedFeature> MOON_ORE_GOLD =
//            ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource("moon_gold_ore"));
//
//    public static final ResourceKey<PlacedFeature> MOON_ORE_IRON_SMALL =
//            ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource("moon_iron_ore_small"));
//
//    public static final ResourceKey<PlacedFeature> MOON_ORE_IRON_LARGE =
//            ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource("moon_iron_ore_large"));
//
//    public static final ResourceKey<PlacedFeature> MOON_ORE_LAPIS =
//            ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource("moon_lapis_ore"));
//
//    public static final ResourceKey<PlacedFeature> MOON_ORE_REDSTONE =
//            ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource("moon_redstone_ore"));
//
//    public static final ResourceKey<PlacedFeature> MOON_ORE_TITANIUM =
//            ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource("moon_titanium_ore"));
//
//    public static final ResourceKey<PlacedFeature> MOON_ORE_ZINC =
//            ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource("moon_zinc_ore"));
//
//    public static final ResourceKey<PlacedFeature> MOON_ORE_ZINC_LARGE =
//            ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource("moon_zinc_ore_large"));


    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);

        PlanetOres.bootstrapPlacedFeatures(configured, context);
    }
}
