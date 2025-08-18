package com.lightning.northstar.world.features.trunkplacers;

import com.lightning.northstar.Northstar;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NorthstarTrunkPlacerTypes<P extends TrunkPlacer> {

    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPES = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, Northstar.MOD_ID);

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<?>> ARGYRE_TRUNK_PLACER = TRUNK_PLACER_TYPES
            .register("argyre_trunk_placer", () -> new TrunkPlacerType<>(ArgyreTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<?>> ARGYRE_CEILING_TRUNK_PLACER = TRUNK_PLACER_TYPES
            .register("argyre_ceiling_trunk_placer", () -> new TrunkPlacerType<>(ArgyreTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<?>> BLOOM_FUNGUS_TRUNK_PLACER = TRUNK_PLACER_TYPES
            .register("bloom_fungus_trunk_placer", () -> new TrunkPlacerType<>(BloomTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<?>> ROOF_BLOOM_FUNGUS_TRUNK_PLACER = TRUNK_PLACER_TYPES
            .register("roof_bloom_fungus_trunk_placer", () -> new TrunkPlacerType<>(RoofBloomTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<?>> SPIKE_FUNGUS_TRUNK_PLACER = TRUNK_PLACER_TYPES
            .register("spike_fungus_trunk_placer", () -> new TrunkPlacerType<>(SpikeTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<?>> PLATE_FUNGUS_TRUNK_PLACER = TRUNK_PLACER_TYPES
            .register("plate_fungus_trunk_placer", () -> new TrunkPlacerType<>(PlateTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<?>> ROOF_PLATE_FUNGUS_TRUNK_PLACER = TRUNK_PLACER_TYPES
            .register("roof_plate_fungus_trunk_placer", () -> new TrunkPlacerType<>(RoofPlateTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<?>> TOWER_FUNGUS_TRUNK_PLACER = TRUNK_PLACER_TYPES
            .register("tower_fungus_trunk_placer", () -> new TrunkPlacerType<>(TowerTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<?>> ROOF_TOWER_FUNGUS_TRUNK_PLACER = TRUNK_PLACER_TYPES
            .register("roof_tower_fungus_trunk_placer", () -> new TrunkPlacerType<>(RoofTowerTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<?>> CALORIAN_VINES_TRUNK_PLACER = TRUNK_PLACER_TYPES
            .register("calorian_vines_trunk_placer", () -> new TrunkPlacerType<>(TestSaplingTrunkPlacer.CODEC));

    public static void register(IEventBus eventBus) {
        TRUNK_PLACER_TYPES.register(eventBus);
    }

}
