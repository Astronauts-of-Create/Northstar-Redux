package com.lightning.northstar.content.world;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarTags.NorthstarBiomeTags;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class NorthstarStructures {

    public static final ResourceKey<Structure>
            LUNAR_BASE = key("lunar_base"),
            MARTIAN_BASE = key("martian_base");

    private static ResourceKey<Structure> key(String path) {
        return ResourceKey.create(Registries.STRUCTURE, Northstar.asResource(path));
    }

    public static void bootstrap(BootstrapContext<Structure> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> templatePools = context.lookup(Registries.TEMPLATE_POOL);

        context.register(
                LUNAR_BASE,
                new JigsawStructure(
                        new Structure.StructureSettings(
                                biomes.getOrThrow(NorthstarBiomeTags.MOON_BIOMES.tag),
                                Map.of(),
                                GenerationStep.Decoration.UNDERGROUND_STRUCTURES,
                                TerrainAdjustment.BURY
                        ),
                        templatePools.getOrThrow(Templates.LUNAR_BASE_POOL_START),
                        Optional.of(Northstar.asResource("start_anchor")),
                        7,
                        ConstantHeight.of(VerticalAnchor.absolute(-23)),
                        false,
                        Optional.of(Heightmap.Types.WORLD_SURFACE_WG),
                        80,
                        List.of(),
                        JigsawStructure.DEFAULT_DIMENSION_PADDING,
                        JigsawStructure.DEFAULT_LIQUID_SETTINGS
                )
        );

        context.register(
                MARTIAN_BASE,
                new JigsawStructure(
                        new Structure.StructureSettings(
                                biomes.getOrThrow(NorthstarBiomeTags.HAS_MARS_BASE.tag),
                                Map.of(),
                                GenerationStep.Decoration.UNDERGROUND_STRUCTURES,
                                TerrainAdjustment.BURY
                        ),
                        templatePools.getOrThrow(Templates.MARTIAN_BASE_POOL_START),
                        Optional.of(Northstar.asResource("start_anchor")),
                        7,
                        ConstantHeight.of(VerticalAnchor.absolute(-18)),
                        false,
                        Optional.of(Heightmap.Types.WORLD_SURFACE_WG),
                        80,
                        List.of(),
                        JigsawStructure.DEFAULT_DIMENSION_PADDING,
                        JigsawStructure.DEFAULT_LIQUID_SETTINGS
                )
        );
    }

    public static class Templates {
        public static final ResourceKey<StructureTemplatePool>
                LUNAR_BASE_POOL = key("lunar_base_pool"),
                LUNAR_BASE_POOL_ROOMS = key("lunar_base_pool_rooms"),
                LUNAR_BASE_POOL_START = key("lunar_base_pool_start"),
                MARTIAN_BASE_POOL_START = key("martian_base_pool_start"),
                MARTIAN_BASE_POOL = key("martian_base_pool"),
                MARTIAN_BASE_ROOM_POOL = key("martian_base_room_pool");

        private static ResourceKey<StructureTemplatePool> key(String path) {
            return ResourceKey.create(Registries.TEMPLATE_POOL, Northstar.asResource(path));
        }

        public static void bootstrap(BootstrapContext<StructureTemplatePool> context) {
            HolderGetter<StructureTemplatePool> templatePools = context.lookup(Registries.TEMPLATE_POOL);
            Holder.Reference<StructureTemplatePool> empty = templatePools.getOrThrow(Pools.EMPTY);

            context.register(
                    LUNAR_BASE_POOL_START,
                    new StructureTemplatePool(
                            empty,
                            List.of(
                                    element("lunar_base/entrance", 50)
                            ),
                            StructureTemplatePool.Projection.RIGID
                    )
            );

            context.register(
                    LUNAR_BASE_POOL,
                    new StructureTemplatePool(
                            empty,
                            List.of(
                                    element("lunar_base/entrance", 50),
                                    element("lunar_base/hall", 50),
                                    element("lunar_base/hall_cap", 5)
                            ),
                            StructureTemplatePool.Projection.RIGID
                    )
            );

            context.register(
                    LUNAR_BASE_POOL_ROOMS,
                    new StructureTemplatePool(
                            empty,
                            List.of(
                                    element("lunar_base/hall", 20),
                                    element("lunar_base/hall_wall", 10),
                                    element("lunar_base/hall_turn", 50),
                                    element("lunar_base/oxy_room", 30),
                                    element("lunar_base/brig", 20),
                                    element("lunar_base/mess_hall", 8),
                                    element("lunar_base/steam_room", 8)
                            ),
                            StructureTemplatePool.Projection.RIGID
                    )
            );

            context.register(
                    MARTIAN_BASE_POOL_START,
                    new StructureTemplatePool(
                            empty,
                            List.of(
                                    element("martian_base/entrance", 50)
                            ),
                            StructureTemplatePool.Projection.RIGID
                    )
            );

            context.register(
                    MARTIAN_BASE_POOL,
                    new StructureTemplatePool(
                            empty,
                            List.of(
                                    element("martian_base/entrance", 50),
                                    element("martian_base/connector_start", 50),
                                    element("martian_base/connector", 50),
                                    element("martian_base/hall", 50),
                                    element("martian_base/collapsed_hall_1", 16),
                                    element("martian_base/collapsed_hall_2", 16),
                                    element("martian_base/collapsed_hall_3", 16)
                            ),
                            StructureTemplatePool.Projection.RIGID
                    )
            );

            context.register(
                    MARTIAN_BASE_ROOM_POOL,
                    new StructureTemplatePool(
                            empty,
                            List.of(
                                    element("martian_base/connector", 50),
                                    element("martian_base/experiment_room1", 30),
                                    element("martian_base/experiment_room2", 20),
                                    element("martian_base/experiment_room3", 5),
                                    element("martian_base/oxy_room", 15),
                                    element("martian_base/collapsed_oxy_room", 15),
                                    element("martian_base/server_room", 30),
                                    element("martian_base/storage", 20),
                                    element("martian_base/ice_box", 8),
                                    element("martian_base/boiler_room", 8),
                                    element("martian_base/mess_hall", 12),
                                    element("martian_base/collapsed_mess_hall", 8),
                                    element("martian_base/barracks", 20),
                                    element("martian_base/collapsed_barracks", 10)
                            ),
                            StructureTemplatePool.Projection.RIGID
                    )
            );
        }

        private static Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer> element(String path, int weight) {
            return Pair.of(StructurePoolElement.single(Northstar.MOD_ID + ":" + path), weight);
        }
    }

    public static class Sets {
        public static final ResourceKey<StructureSet>
                LUNAR_BASE = key("lunar_base"),
                MARTIAN_BASE = key("martian_base");

        private static ResourceKey<StructureSet> key(String path) {
            return ResourceKey.create(Registries.STRUCTURE_SET, Northstar.asResource(path));
        }

        public static void bootstrap(BootstrapContext<StructureSet> context) {
            HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);

            context.register(
                    LUNAR_BASE,
                    new StructureSet(
                            List.of(
                                    StructureSet.entry(structures.getOrThrow(NorthstarStructures.LUNAR_BASE), 1)
                            ),
                            new RandomSpreadStructurePlacement(
                                    Vec3i.ZERO,
                                    StructurePlacement.FrequencyReductionMethod.DEFAULT,
                                    1.0f,
                                    30084232,
                                    Optional.empty(),
                                    40,
                                    16,
                                    RandomSpreadType.LINEAR
                            )
                    )
            );

            context.register(
                    MARTIAN_BASE,
                    new StructureSet(
                            List.of(
                                    StructureSet.entry(structures.getOrThrow(NorthstarStructures.MARTIAN_BASE), 1)
                            ),
                            new RandomSpreadStructurePlacement(
                                    Vec3i.ZERO,
                                    StructurePlacement.FrequencyReductionMethod.DEFAULT,
                                    1.0f,
                                    1311454239,
                                    Optional.empty(),
                                    40,
                                    16,
                                    RandomSpreadType.LINEAR
                            )
                    )
            );
        }
    }


}
