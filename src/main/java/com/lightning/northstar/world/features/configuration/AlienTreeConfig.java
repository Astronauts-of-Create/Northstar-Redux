package com.lightning.northstar.world.features.configuration;

import com.google.common.collect.ImmutableList;
import com.lightning.northstar.world.features.trunkplacers.ArgyreTrunkPlacer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Blocks;
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

    public static class AlienTreeConfigBuilder {
        public final BlockStateProvider trunkProvider;
        public final BlockStateProvider glowProvider;
        private final ArgyreTrunkPlacer trunkPlacer;
        public final BlockStateProvider foliageProvider;
        private final FoliagePlacer foliagePlacer;
        private final Optional<RootPlacer> rootPlacer;
        private BlockStateProvider dirtProvider;
        private final FeatureSize minimumSize;
        private List<TreeDecorator> decorators = ImmutableList.of();
        private boolean ignoreVines;
        private boolean forceDirt;

        public AlienTreeConfigBuilder(BlockStateProvider pTrunkProvider, BlockStateProvider glow, ArgyreTrunkPlacer pTrunkPlacer, BlockStateProvider pFoliageProvider, FoliagePlacer pFoliagePlacer, Optional<RootPlacer> pRootPlacer, FeatureSize pMinimumSize) {
            this.trunkProvider = pTrunkProvider;
            this.glowProvider = glow;
            this.trunkPlacer = pTrunkPlacer;
            this.foliageProvider = pFoliageProvider;
            this.dirtProvider = BlockStateProvider.simple(Blocks.DIRT);
            this.foliagePlacer = pFoliagePlacer;
            this.rootPlacer = pRootPlacer;
            this.minimumSize = pMinimumSize;
        }

        public AlienTreeConfigBuilder(BlockStateProvider pTrunkProvider, BlockStateProvider glow, ArgyreTrunkPlacer pTrunkPlacer, BlockStateProvider pFoliageProvider, FoliagePlacer pFoliagePlacer, FeatureSize pMinimumSize) {
            this(pTrunkProvider, glow, pTrunkPlacer, pFoliageProvider, pFoliagePlacer, Optional.empty(), pMinimumSize);
        }

        public AlienTreeConfig.AlienTreeConfigBuilder dirt(BlockStateProvider pDirtProvider) {
            this.dirtProvider = pDirtProvider;
            return this;
        }

        public AlienTreeConfig.AlienTreeConfigBuilder decorators(List<TreeDecorator> pDecorators) {
            this.decorators = pDecorators;
            return this;
        }

        public AlienTreeConfig.AlienTreeConfigBuilder ignoreVines() {
            this.ignoreVines = true;
            return this;
        }

        public AlienTreeConfig.AlienTreeConfigBuilder forceDirt() {
            this.forceDirt = true;
            return this;
        }

        public AlienTreeConfig build() {
            return new AlienTreeConfig(this.trunkProvider, this.glowProvider, this.trunkPlacer, this.foliageProvider, this.foliagePlacer, this.rootPlacer, this.dirtProvider, this.minimumSize, this.decorators, this.ignoreVines, this.forceDirt);
        }
    }
}
