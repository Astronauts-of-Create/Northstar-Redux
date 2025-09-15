package com.lightning.northstar.data;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarDamageTypes;
import com.lightning.northstar.item.NorthstarEnchantments;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

@EventBusSubscriber(modid = Northstar.MOD_ID)
public class NorthstarDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        RegistrySetBuilder builder = new RegistrySetBuilder()
                .add(Registries.ENCHANTMENT, NorthstarEnchantments::bootstrap)
                .add(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, NorthstarEnchantments::bootstrapEffects)
                .add(Registries.CONFIGURED_FEATURE, NorthstarConfiguredFeatures::bootstrap)
                .add(Registries.DAMAGE_TYPE, NorthstarDamageTypes::bootstrap);

        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(generator.getPackOutput(), event.getLookupProvider(), builder, Set.of(Northstar.MOD_ID)));
    }

}
