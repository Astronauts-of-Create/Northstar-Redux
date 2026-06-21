package com.lightning.northstar.content.world.planet.moon;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.world.gen.OreHelper;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.*;

import static com.lightning.northstar.content.world.planet.core.NorthstarPlacedFeatures.register;

public class MoonPlacedFeatures {

    public static final ResourceKey<PlacedFeature>
            BLOB_ANDESITE = key("blob_andesite"),
            BLOB_ASURINE = key("blob_asurine"),
            BLOB_ASURINE_LARGE = key("blob_asurine_large"),
            BLOB_BASALT = key("blob_basalt"),
            BLOB_BLACKSTONE = key("blob_blackstone"),
            BLOB_BLUE_ICE = key("blob_blue_ice"),
            BLOB_GLOWSTONE = key("blob_glowstone"),
            BLOB_ICE = key("blob_ice"),
            BLOB_ICE_SMALL = key("blob_ice_small"),
            BLOB_OBSIDIAN = key("blob_obsidian"),
            BLOB_TUFF = key("blob_tuff"),
            CRATER = key("crater"),
            CRATER_BIG = key("crater_big"),
            LUNAR_SAPPHIRE_GEODE = key("lunar_sapphire_geode"),
            ORE_COPPER = key("ore_copper"),
            ORE_DIAMOND = key("ore_diamond"),
            ORE_GLOWSTONE = key("ore_glowstone"),
            ORE_GOLD = key("ore_gold"),
            ORE_IRON = key("ore_iron"),
            ORE_IRON_LARGE = key("ore_iron_large"),
            ORE_LAPIS = key("ore_lapis"),
            ORE_REDSTONE = key("ore_redstone"),
            ORE_TITANIUM = key("ore_titanium"),
            ORE_ZINC = key("ore_zinc"),
            ORE_ZINC_LARGE = key("ore_zinc_large");

    private static ResourceKey<PlacedFeature> key(String path) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Northstar.asResource("moon_" + path));
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        OreHelper ores = new OreHelper(context, NorthstarBlockTags.MOON_STONE_REPLACEABLE, NorthstarBlockTags.MOON_DEEP_STONE_REPLACEABLE);

        ores.builder(ORE_COPPER)
                .sized(12, 0)
                .ores(NorthstarBlocks.MOON_COPPER_ORE, NorthstarBlocks.MOON_DEEP_COPPER_ORE)
                .trianglePlacement(20, VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112))
                .register();

        ores.builder(ORE_DIAMOND)
                .sized(4, 0.25f)
                .ores(NorthstarBlocks.MOON_DIAMOND_ORE, NorthstarBlocks.MOON_DEEP_DIAMOND_ORE)
                .trianglePlacement(7, VerticalAnchor.BOTTOM, VerticalAnchor.aboveBottom(80))
                .register();

        ores.builder(ORE_GLOWSTONE)
                .sized(7, 0)
                .ores(NorthstarBlocks.MOON_GLOWSTONE_ORE, NorthstarBlocks.MOON_DEEP_GLOWSTONE_ORE)
                .trianglePlacement(5, VerticalAnchor.absolute(-28), VerticalAnchor.absolute(112))
                .register();

        ores.builder(ORE_GOLD)
                .sized(6, 0)
                .ores(NorthstarBlocks.MOON_GOLD_ORE, NorthstarBlocks.MOON_DEEP_GOLD_ORE)
                .trianglePlacement(7, VerticalAnchor.absolute(-64), VerticalAnchor.absolute(48))
                .register();

        ores.builder(ORE_IRON)
                .sized(4, 0)
                .ores(NorthstarBlocks.MOON_IRON_ORE, NorthstarBlocks.MOON_DEEP_IRON_ORE)
                .uniformPlacement(8, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(72))
                .register();

        ores.builder(ORE_IRON_LARGE)
                .sized(9, 0)
                .ores(NorthstarBlocks.MOON_IRON_ORE, NorthstarBlocks.MOON_DEEP_IRON_ORE)
                .trianglePlacement(12, VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))
                .register();

        ores.builder(ORE_LAPIS)
                .sized(12, 0)
                .ores(NorthstarBlocks.MOON_LAPIS_ORE, NorthstarBlocks.MOON_DEEP_LAPIS_ORE)
                .uniformPlacement(8, VerticalAnchor.absolute(-50), VerticalAnchor.aboveBottom(50))
                .register();

        ores.builder(ORE_REDSTONE)
                .sized(10, 0)
                .ores(NorthstarBlocks.MOON_REDSTONE_ORE, NorthstarBlocks.MOON_DEEP_REDSTONE_ORE)
                .uniformPlacement(7, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(0))
                .register();

        ores.builder(ORE_TITANIUM)
                .sized(6, 0)
                .ores(NorthstarBlocks.MOON_TITANIUM_ORE, NorthstarBlocks.MOON_DEEP_TITANIUM_ORE)
                .trianglePlacement(7, VerticalAnchor.absolute(-64), VerticalAnchor.absolute(48))
                .register();

        ores.builder(ORE_ZINC)
                .sized(9, 0)
                .ores(NorthstarBlocks.MOON_ZINC_ORE, NorthstarBlocks.MOON_DEEP_ZINC_ORE)
                .trianglePlacement(12, VerticalAnchor.absolute(-64), VerticalAnchor.absolute(48))
                .register();

        ores.builder(ORE_ZINC_LARGE)
                .sized(15, 0)
                .ores(NorthstarBlocks.MOON_ZINC_ORE, NorthstarBlocks.MOON_DEEP_ZINC_ORE)
                .trianglePlacement(6, VerticalAnchor.absolute(-64), VerticalAnchor.absolute(48))
                .register();


        OreHelper blobs = new OreHelper(context, NorthstarBlockTags.BASE_STONE_MOON, NorthstarBlockTags.BASE_STONE_MOON);

        blobs.builder(BLOB_ANDESITE)
                .blobSize()
                .ores(Blocks.ANDESITE, null)
                .uniformPlacement(1, VerticalAnchor.absolute(0), VerticalAnchor.absolute(72))
                .register();

        blobs.builder(BLOB_ASURINE)
                .blobSize()
                .ores(AllPaletteStoneTypes.ASURINE.baseBlock.get(), null)
                .placement(
                        RarityFilter.onAverageOnceEvery(4),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(72)),
                        BiomeFilter.biome()
                )
                .register();

        blobs.builder(BLOB_ASURINE_LARGE)
                .blobSize()
                .ores(AllPaletteStoneTypes.ASURINE.baseBlock.get(), null)
                .uniformPlacement(2, VerticalAnchor.absolute(-64), VerticalAnchor.absolute(72))
                .register();

        blobs.builder(BLOB_BASALT)
                .blobSize()
                .ores(Blocks.BASALT, null)
                .uniformPlacement(5, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(8))
                .register();

        blobs.builder(BLOB_BLACKSTONE)
                .blobSize()
                .ores(Blocks.BLACKSTONE, null)
                .uniformPlacement(1, VerticalAnchor.BOTTOM, VerticalAnchor.absolute(8))
                .register();

        register(
                context,
                BLOB_GLOWSTONE,
                NetherFeatures.GLOWSTONE_EXTRA,
                CountPlacement.of(100),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)),
                BiomeFilter.biome()
        );

        blobs.builder(BLOB_BLUE_ICE)
                .blobSize()
                .ores(Blocks.BLUE_ICE, null)
                .placement(
                        RarityFilter.onAverageOnceEvery(3),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.triangle(VerticalAnchor.absolute(-28), VerticalAnchor.absolute(112))
                )
                .register();

        blobs.builder(BLOB_ICE)
                .blobSize()
                .ores(Blocks.ICE, null)
                .placement(
                        CountPlacement.of(12),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.triangle(VerticalAnchor.absolute(-28), VerticalAnchor.absolute(112)),
                        BiomeFilter.biome()
                )
                .register();

        blobs.builder(BLOB_ICE_SMALL)
                .blobSize()
                .ores(Blocks.ICE, null)
                .placement(
                        InSquarePlacement.spread(),
                        HeightRangePlacement.triangle(VerticalAnchor.absolute(-28), VerticalAnchor.absolute(112)),
                        BiomeFilter.biome()
                )
                .register();

        blobs.builder(BLOB_OBSIDIAN)
                .blobSize()
                .ores(Blocks.OBSIDIAN, null)
                .trianglePlacement(12, VerticalAnchor.absolute(-28), VerticalAnchor.absolute(112))
                .register();

        blobs.builder(BLOB_TUFF)
                .blobSize()
                .ores(Blocks.TUFF, null)
                .placement(
                        RarityFilter.onAverageOnceEvery(2),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32)),
                        BiomeFilter.biome()
                )
                .register();

        register(
                context,
                CRATER,
                MoonConfiguredFeatures.CRATER,
                RarityFilter.onAverageOnceEvery(6),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                BiomeFilter.biome()
        );

        register(
                context,
                CRATER_BIG,
                MoonConfiguredFeatures.CRATER_BIG,
                RarityFilter.onAverageOnceEvery(10),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                BiomeFilter.biome()
        );

        register(
                context,
                LUNAR_SAPPHIRE_GEODE,
                MoonConfiguredFeatures.LUNAR_SAPPHIRE_GEODE,
                RarityFilter.onAverageOnceEvery(12),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.absolute(5)),
                BiomeFilter.biome()
        );
    }

}
