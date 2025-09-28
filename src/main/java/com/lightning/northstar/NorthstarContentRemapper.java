package com.lightning.northstar;

import com.lightning.northstar.content.NorthstarBlocks;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Northstar.MOD_ID, bus = Bus.FORGE)
public class NorthstarContentRemapper {

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
    }

    public static void remap(String oldValue, RegistryEntry<?> newValue) {
        remap(Northstar.asResource(oldValue), newValue);
    }

    public static void remap(ResourceLocation oldValue, RegistryEntry<?> newValue) {
        remapped.put(oldValue, newValue.getId());
    }

    @SubscribeEvent
    public static void onRemapContent(MissingMappingsEvent event) {
        remapContent(event, Registries.BLOCK, ForgeRegistries.BLOCKS);
        remapContent(event, Registries.BLOCK_ENTITY_TYPE, ForgeRegistries.BLOCK_ENTITY_TYPES);
        remapContent(event, Registries.ITEM, ForgeRegistries.ITEMS);
    }

    private static <T> void remapContent(MissingMappingsEvent event, ResourceKey<Registry<T>> registry, IForgeRegistry<T> forgeRegistry) {
        for (MissingMappingsEvent.Mapping<T> mapping : event.getAllMappings(registry)) {
            ResourceLocation remappedId = remapped.get(mapping.getKey());
            if (remappedId != null) {
                Northstar.LOGGER.warn("Remapping '{}' to '{}'", mapping.getKey(), remappedId);
                mapping.remap(forgeRegistry.getValue(remappedId));
            }
        }
    }

}
