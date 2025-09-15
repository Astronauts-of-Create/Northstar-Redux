package com.lightning.northstar.events;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.combustion_engine.CombustionEngineBlockEntity;
import com.lightning.northstar.block.tech.electrolysis_machine.ElectrolysisMachineBlockEntity;
import com.lightning.northstar.block.tech.ice_box.IceBoxBlockEntity;
import com.lightning.northstar.block.tech.oxygen_concentrator.OxygenConcentratorBlockEntity;
import com.lightning.northstar.block.tech.oxygen_filler.OxygenFillerBlockEntity;
import com.lightning.northstar.block.tech.oxygen_sealer.OxygenSealerBlockEntity;
import com.lightning.northstar.block.tech.rocket_controls.RocketControlsServerHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = Northstar.MOD_ID)
public class NorthstarCommonEvents {

    @SubscribeEvent
    public static void onServerWorldTick(LevelTickEvent.Post event) {
        RocketControlsServerHandler.tick(event.getLevel());
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
//        VenusWeather.register(event.getDispatcher());
//        MarsWeather.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        CombustionEngineBlockEntity.registerCapabilities(event);
        ElectrolysisMachineBlockEntity.registerCapabilities(event);
        IceBoxBlockEntity.registerCapabilities(event);
        OxygenConcentratorBlockEntity.registerCapabilities(event);
        OxygenFillerBlockEntity.registerCapabilities(event);
        OxygenSealerBlockEntity.registerCapabilities(event);
    }

}
