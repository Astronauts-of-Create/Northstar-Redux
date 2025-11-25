package com.lightning.northstar.world.features;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.world.features.configuration.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FossilFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NorthstarFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, Northstar.MOD_ID);

    //public static final Feature<MarsRootsConfig> MARS_ROOTS = register("mars_roots", new MarsRoots(MarsRootsConfig.CODEC));
    public static final DeferredHolder<Feature<?>, ArgyreFeature> NATURAL_ARGYRE = FEATURES.register("natural_argyre", () -> new ArgyreFeature(AlienTreeConfig.CODEC));
    public static final DeferredHolder<Feature<?>, StoneClusterFeature> STONE_CLUSTER = FEATURES.register("stone_cluster", () -> new StoneClusterFeature(StoneClusterConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, StoneColumnFeature> STONE_COLUMN = FEATURES.register("stone_column", () -> new StoneColumnFeature(StoneColumnConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, PointedStoneClusterFeature> POINTED_STONE_CLUSTER = FEATURES.register("pointed_stone_cluster", () -> new PointedStoneClusterFeature(PointedStoneClusterConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, SphereFeature> SPHERE = FEATURES.register("sphere", () -> new SphereFeature(SphereConfig.CODEC));
    public static final DeferredHolder<Feature<?>, CraterFeature> CRATER = FEATURES.register("crater", () -> new CraterFeature(CraterConfig.CODEC));
    public static final DeferredHolder<Feature<?>, GlowstoneBranchFeature> GLOWSTONE_BRANCH = FEATURES.register("glowstone_branch", () -> new GlowstoneBranchFeature(GlowstoneBranchConfig.CODEC));
    public static final DeferredHolder<Feature<?>, GlowstoneUpsideDownBranchFeature> GLOWSTONE_UPSIDE_DOWN_BRANCH = FEATURES.register("glowstone_upside_down_branch", () -> new GlowstoneUpsideDownBranchFeature(GlowstoneBranchConfig.CODEC));
    public static final DeferredHolder<Feature<?>, GlowingOreFeature> GLOWING_ORE = FEATURES.register("glowing_ore", () -> new GlowingOreFeature(OreConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, GiantSkeletonFeature> GIANT_SKELETON = FEATURES.register("giant_skeleton", () -> new GiantSkeletonFeature(FossilFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, StructureFeature> STRUCTURE_FEATURE = FEATURES.register("structure_feature", () -> new StructureFeature(StructureFeatureConfig.CODEC));
    public static final DeferredHolder<Feature<?>, BlockPileFeature> BLOCK_PILE = FEATURES.register("block_pile", () -> new BlockPileFeature(BlockPileConfig.CODEC));
    public static final DeferredHolder<Feature<?>, SmallRockFeature> SMALL_ROCK = FEATURES.register("small_rock", () -> new SmallRockFeature(BlockStateConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, WormNestFeature> WORM_NEST = FEATURES.register("worm_nest", () -> new WormNestFeature(StructureFeatureConfig.CODEC));
    public static final DeferredHolder<Feature<?>, MarsRootsFeature> MARS_ROOTS = FEATURES.register("mars_roots", () -> new MarsRootsFeature(MultifaceGrowthConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, MultifaceGrowthCustomFeature> MULTIFACE_GROWTH_CUSTOM = FEATURES.register("multiface_growth_custom", () -> new MultifaceGrowthCustomFeature(MultifaceGrowthConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, RoofVinesFeature> ROOF_VINES = FEATURES.register("roof_vines", () -> new RoofVinesFeature(RoofVinesConfig.CODEC));
    public static final DeferredHolder<Feature<?>, MercuryCactusFeature> MERCURY_CACTUS = FEATURES.register("mercury_cactus", () -> new MercuryCactusFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, MercuryShelvesFeature> MERCURY_LARGE_SHELVES = FEATURES.register("mercury_large_shelves", () -> new MercuryShelvesFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, MercuryShelvesSmallFeature> MERCURY_SMALL_SHELVES = FEATURES.register("mercury_small_shelves", () -> new MercuryShelvesSmallFeature(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }

}
