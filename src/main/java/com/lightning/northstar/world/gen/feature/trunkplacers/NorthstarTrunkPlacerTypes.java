package com.lightning.northstar.world.gen.feature.trunkplacers;

import com.lightning.northstar.Northstar;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NorthstarTrunkPlacerTypes {

    public static final DeferredRegister<TrunkPlacerType<?>> REGISTER = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, Northstar.MOD_ID);

    public static final DeferredHolder<TrunkPlacerType<?>,TrunkPlacerType<ArgyreTrunkPlacer>> ARGYRE_TRUNK_PLACER = simple("argyre_trunk_placer", ArgyreTrunkPlacer.CODEC);
    public static final DeferredHolder<TrunkPlacerType<?>,TrunkPlacerType<ArgyreTrunkPlacer>> ARGYRE_CEILING_TRUNK_PLACER = simple("argyre_ceiling_trunk_placer", ArgyreTrunkPlacer.CODEC);
    public static final DeferredHolder<TrunkPlacerType<?>,TrunkPlacerType<BloomTrunkPlacer>> BLOOM_FUNGUS_TRUNK_PLACER = simple("bloom_fungus_trunk_placer", BloomTrunkPlacer.CODEC);
    public static final DeferredHolder<TrunkPlacerType<?>,TrunkPlacerType<RoofBloomTrunkPlacer>> ROOF_BLOOM_FUNGUS_TRUNK_PLACER = simple("roof_bloom_fungus_trunk_placer", RoofBloomTrunkPlacer.CODEC);
    public static final DeferredHolder<TrunkPlacerType<?>,TrunkPlacerType<SpikeTrunkPlacer>> SPIKE_FUNGUS_TRUNK_PLACER = simple("spike_fungus_trunk_placer", SpikeTrunkPlacer.CODEC);
    public static final DeferredHolder<TrunkPlacerType<?>,TrunkPlacerType<PlateTrunkPlacer>> PLATE_FUNGUS_TRUNK_PLACER = simple("plate_fungus_trunk_placer", PlateTrunkPlacer.CODEC);
    public static final DeferredHolder<TrunkPlacerType<?>,TrunkPlacerType<RoofPlateTrunkPlacer>> ROOF_PLATE_FUNGUS_TRUNK_PLACER = simple("roof_plate_fungus_trunk_placer", RoofPlateTrunkPlacer.CODEC);
    public static final DeferredHolder<TrunkPlacerType<?>,TrunkPlacerType<TowerTrunkPlacer>> TOWER_FUNGUS_TRUNK_PLACER = simple("tower_fungus_trunk_placer", TowerTrunkPlacer.CODEC);
    public static final DeferredHolder<TrunkPlacerType<?>,TrunkPlacerType<RoofTowerTrunkPlacer>> ROOF_TOWER_FUNGUS_TRUNK_PLACER = simple("roof_tower_fungus_trunk_placer", RoofTowerTrunkPlacer.CODEC);
    public static final DeferredHolder<TrunkPlacerType<?>,TrunkPlacerType<CalorianVinesTrunkPlacer>> CALORIAN_VINES_TRUNK_PLACER = simple("calorian_vines_trunk_placer", CalorianVinesTrunkPlacer.CODEC);

    private static <T extends TrunkPlacer> DeferredHolder<TrunkPlacerType<?>,TrunkPlacerType<T>> simple(String name, MapCodec<T> codec) {
        return REGISTER.register(name, () -> new TrunkPlacerType<>(codec));
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

}
