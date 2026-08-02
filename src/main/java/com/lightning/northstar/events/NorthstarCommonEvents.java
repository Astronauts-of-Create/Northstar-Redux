package com.lightning.northstar.events;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.atmospheric_concentrator.AtmosphericConcentratorBlockEntity;
import com.lightning.northstar.block.tech.combustion_engine.CombustionEngineBlockEntity;
import com.lightning.northstar.block.tech.electrolysis_machine.ElectrolysisMachineBlockEntity;
import com.lightning.northstar.block.tech.ice_box.IceBoxBlockEntity;
import com.lightning.northstar.block.tech.oxygen_filler.OxygenFillerBlockEntity;
import com.lightning.northstar.block.tech.oxygen_sealer.OxygenSealerBlockEntity;
import com.lightning.northstar.item.DrinkableBucketItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

@EventBusSubscriber(modid = Northstar.MOD_ID)
public class NorthstarCommonEvents {

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        AtmosphericConcentratorBlockEntity.registerCapabilities(event);
        CombustionEngineBlockEntity.registerCapabilities(event);
        ElectrolysisMachineBlockEntity.registerCapabilities(event);
        IceBoxBlockEntity.registerCapabilities(event);
        OxygenFillerBlockEntity.registerCapabilities(event);
        OxygenSealerBlockEntity.registerCapabilities(event);

        // See CapabilityHooks#registerFallbackVanillaProviders
        for (Item item : BuiltInRegistries.ITEM) {
            if (item.getClass() == DrinkableBucketItem.class) {
                event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), item);
            }
        }
    }

}
