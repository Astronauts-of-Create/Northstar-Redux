package com.lightning.northstar.data;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.item.NorthstarEnchantments;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

public class NorthstarDataGen {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(generator.getPackOutput(),
                event.getLookupProvider(),
                new RegistrySetBuilder()
                        .add(Registries.ENCHANTMENT, NorthstarEnchantments::bootstrap)
                        .add(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, NorthstarEnchantments::bootstrapEffects)
                        .add(Registries.CONFIGURED_FEATURE, NorthstarConfiguredFeatures::bootstrap),
                Set.of(Northstar.MOD_ID)));
    }

}
