package com.lightning.northstar.content.world.planet.venus;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.world.NorthstarFeatures;
import com.lightning.northstar.world.gen.feature.StructureFeatureConfig;
import com.lightning.northstar.world.gen.feature.configuration.RoofVinesConfig;
import com.lightning.northstar.world.gen.feature.configuration.StoneClusterConfiguration;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FossilFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.ColumnFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class VenusConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>>
            BASALT_PILLARS = key("basalt_pillars"),
            BASALT_PILLARS_LARGE = key("basalt_pillars_large"),
            GRAVEL_CLUSTER = key("gravel_cluster"),
            GRAVEL_SULFUR_CLUSTER = key("gravel_sulfur_cluster"),
            LAVA_LAKE = key("lava_lake"),
            LAVA_SPRING = key("lava_spring"),
            PLUMES = key("plumes"),
            RIB_CAGES = key("rib_cages"),
            SULFURIC_ACID_LAKE = key("sulfuric_acid_lake"),
            SULFURIC_ACID_SPRING = key("sulfuric_acid_spring"),
            VINES = key("vines");

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String path) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Northstar.asResource("venus_" + path));
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<StructureProcessorList> structureProcessors = context.lookup(Registries.PROCESSOR_LIST);

        context.register(
                BASALT_PILLARS,
                new ConfiguredFeature<>(
                        Feature.BASALT_COLUMNS,
                        new ColumnFeatureConfiguration(
                                UniformInt.of(2, 3),
                                UniformInt.of(5, 10)
                        )
                )
        );

        context.register(
                BASALT_PILLARS_LARGE,
                new ConfiguredFeature<>(
                        Feature.BASALT_COLUMNS,
                        new ColumnFeatureConfiguration(
                                ConstantInt.of(3),
                                UniformInt.of(7, 10)
                        )
                )
        );

        context.register(
                GRAVEL_SULFUR_CLUSTER,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STONE_CLUSTER.get(),
                        new StoneClusterConfiguration(
                                36,
                                BlockStateProvider.simple(NorthstarBlocks.VENUS_GRAVEL.get()),
                                NorthstarFluids.SULFURIC_ACID.get().getSource(true),
                                UniformInt.of(3, 6),
                                UniformInt.of(1, 3),
                                1,
                                3,
                                UniformInt.of(1, 2),
                                UniformFloat.of(0.2f, 0.4f),
                                ConstantFloat.of(0),
                                ConstantFloat.of(0),
                                ClampedNormalFloat.of(0.1f, 0.9f, 0.1f, 0.3f),
                                0.1f,
                                3,
                                8
                        )
                )
        );

        context.register(
                GRAVEL_CLUSTER,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STONE_CLUSTER.get(),
                        new StoneClusterConfiguration(
                                36,
                                BlockStateProvider.simple(NorthstarBlocks.VENUS_GRAVEL.get()),
                                Fluids.LAVA.getSource(true),
                                UniformInt.of(3, 6),
                                UniformInt.of(1, 3),
                                1,
                                3,
                                UniformInt.of(1, 2),
                                UniformFloat.of(0.2f, 0.4f),
                                ConstantFloat.of(0),
                                ClampedNormalFloat.of(0.1f, 0.9f, 0.1f, 0.3f),
                                ConstantFloat.of(0),
                                0.1f,
                                3,
                                8
                        )
                )
        );

        context.register(
                LAVA_LAKE,
                new ConfiguredFeature<>(
                        Feature.LAKE,
                        new LakeFeature.Configuration(
                                BlockStateProvider.simple(Blocks.LAVA.defaultBlockState()),
                                BlockStateProvider.simple(NorthstarBlocks.VENUS_STONE.get())
                        )
                )
        );

        context.register(
                LAVA_SPRING,
                new ConfiguredFeature<>(
                        Feature.SPRING,
                        new SpringConfiguration(
                                Fluids.LAVA.getSource(true),
                                true,
                                4,
                                1,
                                HolderSet.direct(
                                        Block::builtInRegistryHolder,
                                        Blocks.STONE,
                                        Blocks.GRANITE,
                                        Blocks.DIORITE,
                                        Blocks.ANDESITE,
                                        Blocks.DEEPSLATE,
                                        Blocks.TUFF,
                                        Blocks.CALCITE,
                                        Blocks.DIRT,
                                        NorthstarBlocks.VENUS_STONE.get(),
                                        NorthstarBlocks.VENUS_DEEP_STONE.get(),
                                        NorthstarBlocks.VOLCANIC_ASH.get(),
                                        AllPaletteStoneTypes.SCORCHIA.baseBlock.get(),
                                        AllPaletteStoneTypes.SCORIA.baseBlock.get(),
                                        AllPaletteStoneTypes.OCHRUM.baseBlock.get()
                                )
                        )
                )
        );

        context.register(
                PLUMES,
                new ConfiguredFeature<>(
                        NorthstarFeatures.STRUCTURE_FEATURE.get(),
                        new StructureFeatureConfig(
                                List.of(
                                        Northstar.asResource("venus_plume/venus_plume_1"),
                                        Northstar.asResource("venus_plume/venus_plume_2"),
                                        Northstar.asResource("venus_plume/venus_plume_3"),
                                        Northstar.asResource("venus_plume/venus_plume_4")
                                ),
                                10
                        )
                )
        );

        context.register(
                RIB_CAGES,
                new ConfiguredFeature<>(
                        NorthstarFeatures.GIANT_SKELETON.get(),
                        new FossilFeatureConfiguration(
                                List.of(
                                        Northstar.asResource("fossil/ribs_1"),
                                        Northstar.asResource("fossil/ribs_2"),
                                        Northstar.asResource("fossil/ribs_3"),
                                        Northstar.asResource("fossil/ribs_4")
                                ),
                                List.of(
                                        Northstar.asResource("fossil/ribs_1_coal"),
                                        Northstar.asResource("fossil/ribs_2_coal"),
                                        Northstar.asResource("fossil/ribs_3_coal"),
                                        Northstar.asResource("fossil/ribs_4_coal")
                                ),
                                structureProcessors.getOrThrow(ProcessorLists.FOSSIL_ROT),
                                structureProcessors.getOrThrow(ProcessorLists.FOSSIL_COAL),
                                4
                        )
                )
        );

        context.register(
                SULFURIC_ACID_LAKE,
                new ConfiguredFeature<>(
                        Feature.LAKE,
                        new LakeFeature.Configuration(
                                BlockStateProvider.simple(NorthstarFluids.SULFURIC_ACID.get().defaultFluidState().createLegacyBlock()),
                                BlockStateProvider.simple(AllPaletteStoneTypes.SCORCHIA.baseBlock.get())
                        )
                )
        );

        context.register(
                SULFURIC_ACID_SPRING,
                new ConfiguredFeature<>(
                        Feature.SPRING,
                        new SpringConfiguration(
                                NorthstarFluids.SULFURIC_ACID.get().getSource(true),
                                true,
                                4,
                                1,
                                HolderSet.direct(
                                        Block::builtInRegistryHolder,
                                        Blocks.STONE,
                                        Blocks.GRANITE,
                                        Blocks.DIORITE,
                                        Blocks.ANDESITE,
                                        Blocks.DEEPSLATE,
                                        Blocks.TUFF,
                                        Blocks.CALCITE,
                                        Blocks.DIRT,
                                        NorthstarBlocks.VENUS_STONE.get(),
                                        NorthstarBlocks.VENUS_DEEP_STONE.get(),
                                        NorthstarBlocks.VOLCANIC_ASH.get(),
                                        AllPaletteStoneTypes.SCORCHIA.baseBlock.get(),
                                        AllPaletteStoneTypes.SCORIA.baseBlock.get(),
                                        AllPaletteStoneTypes.OCHRUM.baseBlock.get()
                                )
                        )
                )
        );

        context.register(
                VINES,
                new ConfiguredFeature<>(
                        NorthstarFeatures.ROOF_VINES.get(),
                        new RoofVinesConfig(
                                BlockStateProvider.simple(NorthstarBlocks.VENUS_VINES.get()),
                                BlockStateProvider.simple(NorthstarBlocks.GLOWING_VENUS_VINES.get()),
                                UniformInt.of(6, 20)
                        )
                )
        );
    }

}
