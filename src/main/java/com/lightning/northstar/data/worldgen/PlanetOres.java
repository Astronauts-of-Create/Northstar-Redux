package com.lightning.northstar.data.worldgen;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class PlanetOres {

    /**
     * The moon
     */
    private final static TagMatchTest moonStoneReplaceable = new TagMatchTest(BlockTags.create(Northstar.asResource("moon_stone_replaceable")));
    private final static TagMatchTest moonDeepStoneReplaceable = new TagMatchTest(BlockTags.create(Northstar.asResource("moon_deep_stone_replaceable")));

    private final static List<OreConfiguration.TargetBlockState> moonCopperBlock = List.of(
            OreConfiguration.target(moonStoneReplaceable, NorthstarBlocks.MOON_COPPER_ORE.get().defaultBlockState()),
            OreConfiguration.target(moonDeepStoneReplaceable, NorthstarBlocks.MOON_DEEP_COPPER_ORE.get().defaultBlockState())
    );

    private final static List<OreConfiguration.TargetBlockState> moonDiamondBlock = List.of(
            OreConfiguration.target(moonStoneReplaceable, NorthstarBlocks.MOON_DIAMOND_ORE.get().defaultBlockState()),
            OreConfiguration.target(moonDeepStoneReplaceable, NorthstarBlocks.MOON_DEEP_DIAMOND_ORE.get().defaultBlockState())
    );

    private final static List<OreConfiguration.TargetBlockState> moonGlowstoneBlock = List.of(
            OreConfiguration.target(moonStoneReplaceable, NorthstarBlocks.MOON_GLOWSTONE_ORE.get().defaultBlockState()),
            OreConfiguration.target(moonDeepStoneReplaceable, NorthstarBlocks.MOON_DEEP_GLOWSTONE_ORE.get().defaultBlockState())
    );

    private final static List<OreConfiguration.TargetBlockState> moonGoldBlock = List.of(
            OreConfiguration.target(moonStoneReplaceable, NorthstarBlocks.MOON_GOLD_ORE.get().defaultBlockState()),
            OreConfiguration.target(moonDeepStoneReplaceable, NorthstarBlocks.MOON_DEEP_GOLD_ORE.get().defaultBlockState())
    );

    private final static List<OreConfiguration.TargetBlockState> moonIronBlock = List.of(
            OreConfiguration.target(moonStoneReplaceable, NorthstarBlocks.MOON_IRON_ORE.get().defaultBlockState()),
            OreConfiguration.target(moonDeepStoneReplaceable, NorthstarBlocks.MOON_DEEP_IRON_ORE.get().defaultBlockState())
    );

    private final static List<OreConfiguration.TargetBlockState> moonIronSmallBlock = List.of(
            OreConfiguration.target(moonStoneReplaceable, NorthstarBlocks.MOON_IRON_ORE.get().defaultBlockState())
    );

    private final static List<OreConfiguration.TargetBlockState> moonLapisBlock = List.of(
            OreConfiguration.target(moonStoneReplaceable, NorthstarBlocks.MOON_LAPIS_ORE.get().defaultBlockState()),
            OreConfiguration.target(moonDeepStoneReplaceable, NorthstarBlocks.MOON_DEEP_LAPIS_ORE.get().defaultBlockState())
    );

    private final static List<OreConfiguration.TargetBlockState> moonRedstoneBlock = List.of(
            OreConfiguration.target(moonStoneReplaceable, NorthstarBlocks.MOON_REDSTONE_ORE.get().defaultBlockState()),
            OreConfiguration.target(moonDeepStoneReplaceable, NorthstarBlocks.MOON_DEEP_REDSTONE_ORE.get().defaultBlockState())
    );

    private final static List<OreConfiguration.TargetBlockState> moonTitaniumBlock = List.of(
            OreConfiguration.target(moonStoneReplaceable, NorthstarBlocks.MOON_TITANIUM_ORE.get().defaultBlockState()),
            OreConfiguration.target(moonDeepStoneReplaceable, NorthstarBlocks.MOON_DEEP_TITANIUM_ORE.get().defaultBlockState())
    );

    private final static List<OreConfiguration.TargetBlockState> moonZincBlock = List.of(
            OreConfiguration.target(moonStoneReplaceable, NorthstarBlocks.MOON_ZINC_ORE.get().defaultBlockState()),
            OreConfiguration.target(moonDeepStoneReplaceable, NorthstarBlocks.MOON_DEEP_ZINC_ORE.get().defaultBlockState())
    );

    private final static List<OreConfiguration.TargetBlockState> moonZincLargeBlock = List.of(
            OreConfiguration.target(moonStoneReplaceable, NorthstarBlocks.MOON_ZINC_ORE.get().defaultBlockState())
    );



    public final static PlanetOre MOON_ORE_COPPER = new PlanetOre(
            Northstar.asResource("moon_copper_ore"),
            //Configured Feature
            new OreConfiguration(moonCopperBlock, 4, 0.0f),
            //Placed Feature
            List.of(
                    CountPlacement.of(20),  // count: 20 veins per chunk
                    InSquarePlacement.spread(), // in_square
                    HeightRangePlacement.of(
                            TrapezoidHeight.of(
                                    VerticalAnchor.absolute(-16),
                                    VerticalAnchor.absolute(112)
                            )
                    ),
                    BiomeFilter.biome())
    );


    public static void bootstrapConfiguredFeatures(BootstapContext<ConfiguredFeature<?, ?>> context) {
        MOON_ORE_COPPER.bootstrapConfiguredFeature(context);
    }

    public static void bootstrapPlacedFeatures(HolderGetter<ConfiguredFeature<?, ?>> configured, BootstapContext<PlacedFeature> context) {
        MOON_ORE_COPPER.bootstrapPlacedFeature(context, configured);
    }
}
