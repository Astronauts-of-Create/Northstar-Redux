package com.lightning.northstar.content.world.planet.core;

import com.lightning.northstar.Northstar;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.*;

import static com.lightning.northstar.content.world.planet.core.NorthstarPlacedFeatures.register;

public class NorthstarVegetationPlacedFeatures {

    public static final ResourceKey<PlacedFeature>
            BLOOM_FUNGUS = key("bloom_fungus"),
            BLOOM_FUNGUS_ROOF = key("bloom_fungus_roof"),
            CALORIAN_VINES = key("calorian_vines"),
            HUGE_BLOOM_FUNGUS = key("huge_bloom_fungus"),
            HUGE_BLOOM_FUNGUS_ROOF = key("huge_bloom_fungus_roof"),
            HUGE_BLOOM_FUNGUS_SURFACE = key("huge_bloom_fungus_surface"),
            HUGE_PLATE_FUNGUS = key("huge_plate_fungus"),
            HUGE_PLATE_FUNGUS_ROOF = key("huge_plate_fungus_roof"),
            HUGE_SPIKE_FUNGUS = key("huge_spike_fungus"),
            HUGE_SPIKE_FUNGUS_SURFACE = key("huge_spike_fungus_surface"),
            HUGE_TOWER_FUNGUS = key("huge_tower_fungus"),
            HUGE_TOWER_FUNGUS_ROOF = key("huge_tower_fungus_roof"),
            PLATE_FUNGUS = key("plate_fungus"),
            PLATE_FUNGUS_ROOF = key("plate_fungus_roof"),
            SPIKE_FUNGUS = key("spike_fungus"),
            SPIKE_FUNGUS_ROOF = key("spike_fungus_roof"),
            TOWER_FUNGUS = key("tower_fungus"),
            TOWER_FUNGUS_ROOF = key("tower_fungus_roof");

    private static ResourceKey<PlacedFeature> key(String path) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource(path));
    }

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        register(
                context,
                BLOOM_FUNGUS,
                NorthstarVegetationConfiguredFeatures.BLOOM_FUNGUS,
                CountPlacement.of(40),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP),
                BiomeFilter.biome()
        );

        register(
                context,
                BLOOM_FUNGUS_ROOF,
                NorthstarVegetationConfiguredFeatures.BLOOM_FUNGUS_ROOF,
                CountPlacement.of(32),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP),
                BiomeFilter.biome()
        );

        register(
                context,
                CALORIAN_VINES,
                NorthstarVegetationConfiguredFeatures.CALORIAN_VINES,
                RarityFilter.onAverageOnceEvery(8),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.aboveBottom(75)),
                EnvironmentScanPlacement.scanningFor(
                        Direction.DOWN,
                        BlockPredicate.solid(),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        32
                ),
                RandomOffsetPlacement.vertical(
                        ConstantInt.of(1)
                ),
                BiomeFilter.biome()
        );

        register(
                context,
                HUGE_BLOOM_FUNGUS,
                NorthstarVegetationConfiguredFeatures.HUGE_BLOOM_FUNGUS,
                CountPlacement.of(6),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                EnvironmentScanPlacement.scanningFor(
                        Direction.DOWN,
                        BlockPredicate.solid(),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        32
                ),
                BiomeFilter.biome()
        );

        register(
                context,
                HUGE_BLOOM_FUNGUS_ROOF,
                NorthstarVegetationConfiguredFeatures.HUGE_BLOOM_FUNGUS_ROOF,
                CountPlacement.of(3),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                EnvironmentScanPlacement.scanningFor(
                        Direction.UP,
                        BlockPredicate.solid(),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        32
                ),
                BiomeFilter.biome()
        );

        register(
                context,
                HUGE_BLOOM_FUNGUS_SURFACE,
                NorthstarVegetationConfiguredFeatures.HUGE_BLOOM_FUNGUS,
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome()
        );

        register(
                context,
                HUGE_PLATE_FUNGUS,
                NorthstarVegetationConfiguredFeatures.HUGE_PLATE_FUNGUS,
                CountPlacement.of(8),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(
                        VerticalAnchor.BOTTOM,
                        VerticalAnchor.absolute(256)
                ),
                EnvironmentScanPlacement.scanningFor(
                        Direction.DOWN,
                        BlockPredicate.solid(),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        32
                ),
                BiomeFilter.biome()
        );

        register(
                context,
                HUGE_PLATE_FUNGUS_ROOF,
                NorthstarVegetationConfiguredFeatures.HUGE_PLATE_FUNGUS_ROOF,
                CountPlacement.of(6),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(
                        VerticalAnchor.BOTTOM,
                        VerticalAnchor.absolute(256)
                ),
                EnvironmentScanPlacement.scanningFor(
                        Direction.UP,
                        BlockPredicate.solid(),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        32
                ),
                BiomeFilter.biome()
        );

        register(
                context,
                HUGE_SPIKE_FUNGUS,
                NorthstarVegetationConfiguredFeatures.HUGE_SPIKE_FUNGUS,
                CountPlacement.of(11),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                EnvironmentScanPlacement.scanningFor(
                        Direction.DOWN,
                        BlockPredicate.solid(),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        32
                ),
                BiomeFilter.biome()
        );

        register(
                context,
                HUGE_SPIKE_FUNGUS_SURFACE,
                NorthstarVegetationConfiguredFeatures.HUGE_SPIKE_FUNGUS,
                CountPlacement.of(2),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome()
        );

        register(
                context,
                HUGE_TOWER_FUNGUS,
                NorthstarVegetationConfiguredFeatures.HUGE_TOWER_FUNGUS,
                CountPlacement.of(3),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                EnvironmentScanPlacement.scanningFor(
                        Direction.DOWN,
                        BlockPredicate.solid(),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        32
                ),
                BiomeFilter.biome()
        );

        register(
                context,
                HUGE_TOWER_FUNGUS_ROOF,
                NorthstarVegetationConfiguredFeatures.HUGE_TOWER_FUNGUS_ROOF,
                CountPlacement.of(2),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                EnvironmentScanPlacement.scanningFor(
                        Direction.UP,
                        BlockPredicate.solid(),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        32
                ),
                BiomeFilter.biome()
        );

        register(
                context,
                PLATE_FUNGUS,
                NorthstarVegetationConfiguredFeatures.PLATE_FUNGUS,
                CountPlacement.of(24),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP),
                BiomeFilter.biome()
        );

        register(
                context,
                PLATE_FUNGUS_ROOF,
                NorthstarVegetationConfiguredFeatures.PLATE_FUNGUS_ROOF,
                CountPlacement.of(24),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP),
                BiomeFilter.biome()
        );

        register(
                context,
                SPIKE_FUNGUS,
                NorthstarVegetationConfiguredFeatures.SPIKE_FUNGUS,
                CountPlacement.of(40),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP),
                BiomeFilter.biome()
        );

        register(
                context,
                SPIKE_FUNGUS_ROOF,
                NorthstarVegetationConfiguredFeatures.SPIKE_FUNGUS_ROOF,
                CountPlacement.of(40),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP),
                BiomeFilter.biome()
        );

        register(
                context,
                TOWER_FUNGUS,
                NorthstarVegetationConfiguredFeatures.TOWER_FUNGUS,
                CountPlacement.of(12),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP),
                BiomeFilter.biome()
        );

        register(
                context,
                TOWER_FUNGUS_ROOF,
                NorthstarVegetationConfiguredFeatures.TOWER_FUNGUS_ROOF,
                CountPlacement.of(12),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP),
                BiomeFilter.biome()
        );
    }

}
