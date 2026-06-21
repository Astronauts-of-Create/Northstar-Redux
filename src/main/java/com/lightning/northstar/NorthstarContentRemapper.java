package com.lightning.northstar;

import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.world.NorthstarDimensionTypes;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Northstar.MOD_ID)
public class NorthstarContentRemapper {

    private static final Map<ResourceLocation, Map<ResourceLocation, ResourceLocation>> mappings = new HashMap<>();
    private static final Map<ResourceLocation, ResourceLocation> remapped = new HashMap<>();

    static {
        // 0.3.0: Replace iron with titanium (as part of #7/#10)
        remap("iron_sheetmetal", NorthstarBlocks.TITANIUM_SHEETMETAL);
        remap("iron_sheetmetal_slab", NorthstarBlocks.TITANIUM_SHEETMETAL_SLAB);
        remap("iron_sheetmetal_vertical_slab", NorthstarBlocks.TITANIUM_SHEETMETAL_VERTICAL_SLAB);
        remap("iron_plating", NorthstarBlocks.TITANIUM_PLATING);
        remap("iron_plating_slab", NorthstarBlocks.TITANIUM_PLATING_SLAB);
        remap("iron_plating_vertical_slab", NorthstarBlocks.TITANIUM_PLATING_VERTICAL_SLAB);
        remap("iron_plating_stairs", NorthstarBlocks.TITANIUM_PLATING_STAIRS);
        remap("iron_pillar", NorthstarBlocks.TITANIUM_PILLAR);
        remap("iron_grate", NorthstarBlocks.TITANIUM_GRATE);
        remap("iron_space_door", NorthstarBlocks.TITANIUM_SPACE_DOOR);
        // 0.3.0: Rename the oxygen_generator to oxygen_sealer
        remap("oxygen_generator", NorthstarBlocks.OXYGEN_SEALER);
        // 0.5.0
        remap("vent_block", NorthstarBlocks.VENT);
        remap("oxygen_concentrator", NorthstarBlocks.ATMOSPHERIC_CONCENTRATOR);
        // 0.6.0
        remap("martian_steel", NorthstarItems.MARTIAN_STEEL_INGOT);
        remap("jet_engine", NorthstarBlocks.ROCKET_THRUSTER);

        remap(Registries.DIMENSION_TYPE, Northstar.asResource("earth_orbit"), NorthstarDimensionTypes.ORBIT.location());
    }

    public static void remap(String oldValue, RegistryEntry<?, ?> newValue) {
        remap(Northstar.asResource(oldValue), newValue);
    }

    public static void remap(ResourceLocation oldValue, RegistryEntry<?, ?> newValue) {
        remapped.put(oldValue, newValue.getId());
    }

    /** Remaps a datapack registry entry */
    public static void remap(ResourceKey<? extends Registry<?>> registry, ResourceLocation oldValue, ResourceLocation newValue) {
        mappings.computeIfAbsent(registry.location(), l -> new HashMap<>()).put(oldValue, newValue);
    }

    @SubscribeEvent
    public static void onRemapContent(RegisterEvent event) {
        Registry<?> registry = event.getRegistry();
        remapped.forEach(registry::addAlias);
    }

    public static <T> ResourceKey<T> remap(ResourceKey<T> original) {
        Map<ResourceLocation, ResourceLocation> mapping = mappings.get(original.registry());
        if (mapping == null) {
            return original;
        }
        ResourceLocation mapped = mapping.get(original.location());
        return mapped == null ? original : ResourceKey.create(ResourceKey.createRegistryKey(original.registry()), mapped);
    }

}
