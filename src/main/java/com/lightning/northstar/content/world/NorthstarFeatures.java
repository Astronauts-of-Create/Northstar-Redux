package com.lightning.northstar.content.world;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.world.gen.feature.*;
import com.lightning.northstar.world.gen.feature.configuration.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FossilFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class NorthstarFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, Northstar.MOD_ID);

    public static final RegistryObject<ArgyreFeature> NATURAL_ARGYRE = FEATURES
            .register("natural_argyre", () -> new ArgyreFeature(TreeConfiguration.CODEC));
    public static final RegistryObject<StoneClusterFeature> STONE_CLUSTER = FEATURES
            .register("stone_cluster", () -> new StoneClusterFeature(StoneClusterConfiguration.CODEC));
    public static final RegistryObject<StoneColumnFeature> STONE_COLUMN = FEATURES
            .register("stone_column", () -> new StoneColumnFeature(StoneColumnConfiguration.CODEC));
    public static final RegistryObject<PointedStoneClusterFeature> POINTED_STONE_CLUSTER = FEATURES
            .register("pointed_stone_cluster", () -> new PointedStoneClusterFeature(PointedStoneClusterConfiguration.CODEC));
    public static final RegistryObject<SphereFeature> SPHERE = FEATURES
            .register("sphere", () -> new SphereFeature(GeodeConfiguration.CODEC));
    public static final RegistryObject<CraterFeature> CRATER = FEATURES
            .register("crater", () -> new CraterFeature(CraterConfig.CODEC));
    public static final RegistryObject<GlowstoneBranchFeature> GLOWSTONE_BRANCH = FEATURES
            .register("glowstone_branch", () -> new GlowstoneBranchFeature(GlowstoneBranchConfig.CODEC));
    public static final RegistryObject<GiantSkeletonFeature> GIANT_SKELETON = FEATURES
            .register("giant_skeleton", () -> new GiantSkeletonFeature(FossilFeatureConfiguration.CODEC));
    public static final RegistryObject<StructureFeature> STRUCTURE_FEATURE = FEATURES
            .register("structure_feature", () -> new StructureFeature(StructureFeatureConfig.CODEC));
    public static final RegistryObject<BlockPileFeature> BLOCK_PILE = FEATURES
            .register("block_pile", () -> new BlockPileFeature(BlockPileConfig.CODEC));
    public static final RegistryObject<SmallRockFeature> SMALL_ROCK = FEATURES
            .register("small_rock", () -> new SmallRockFeature(BlockStateConfiguration.CODEC));
    public static final RegistryObject<WormNestFeature> WORM_NEST = FEATURES
            .register("worm_nest", () -> new WormNestFeature(StructureFeatureConfig.CODEC));
    public static final RegistryObject<MarsRootsFeature> MARS_ROOTS = FEATURES
            .register("mars_roots", () -> new MarsRootsFeature(MultifaceGrowthConfiguration.CODEC));
    public static final RegistryObject<MultifaceGrowthCustomFeature> MULTIFACE_GROWTH_CUSTOM = FEATURES
            .register("multiface_growth_custom", () -> new MultifaceGrowthCustomFeature(MultifaceGrowthConfiguration.CODEC));
    public static final RegistryObject<RoofVinesFeature> ROOF_VINES = FEATURES
            .register("roof_vines", () -> new RoofVinesFeature(RoofVinesConfig.CODEC));
    public static final RegistryObject<MercuryCactusFeature> MERCURY_CACTUS = FEATURES
            .register("mercury_cactus", () -> new MercuryCactusFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<MercuryShelvesFeature> MERCURY_LARGE_SHELVES = FEATURES
            .register("mercury_large_shelves", () -> new MercuryShelvesFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<MercuryShelvesSmallFeature> MERCURY_SMALL_SHELVES = FEATURES
            .register("mercury_small_shelves", () -> new MercuryShelvesSmallFeature(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }

}
