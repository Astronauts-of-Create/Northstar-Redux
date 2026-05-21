package com.lightning.northstar.world.gen.feature.configuration;

import com.lightning.northstar.world.gen.feature.trunkplacers.ArgyreTrunkPlacer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;

import java.util.List;
import java.util.Optional;

public record AlienTreeConfig(BlockStateProvider trunkProvider,
                              BlockStateProvider glowProvider,
                              ArgyreTrunkPlacer trunkPlacer,
                              BlockStateProvider foliageProvider,
                              FoliagePlacer foliagePlacer,
                              Optional<RootPlacer> rootPlacer,
                              BlockStateProvider dirtProvider,
                              FeatureSize minimumSize,
                              List<TreeDecorator> decorators,
                              boolean ignoreVines,
                              boolean forceDirt) implements FeatureConfiguration {

    public static final Codec<AlienTreeConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
            BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter(AlienTreeConfig::trunkProvider),
            BlockStateProvider.CODEC.fieldOf("glow_provider").forGetter(AlienTreeConfig::glowProvider),
            ArgyreTrunkPlacer.CODEC.fieldOf("trunk_placer").forGetter(AlienTreeConfig::trunkPlacer),
            BlockStateProvider.CODEC.fieldOf("foliage_provider").forGetter(AlienTreeConfig::foliageProvider),
            FoliagePlacer.CODEC.fieldOf("foliage_placer").forGetter(AlienTreeConfig::foliagePlacer),
            RootPlacer.CODEC.optionalFieldOf("root_placer").forGetter(AlienTreeConfig::rootPlacer),
            BlockStateProvider.CODEC.fieldOf("dirt_provider").forGetter(AlienTreeConfig::dirtProvider),
            FeatureSize.CODEC.fieldOf("minimum_size").forGetter(AlienTreeConfig::minimumSize),
            TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter(AlienTreeConfig::decorators),
            Codec.BOOL.fieldOf("ignore_vines").orElse(false).forGetter(AlienTreeConfig::ignoreVines),
            Codec.BOOL.fieldOf("force_dirt").orElse(false).forGetter(AlienTreeConfig::forceDirt)
    ).apply(i, AlienTreeConfig::new));

}
