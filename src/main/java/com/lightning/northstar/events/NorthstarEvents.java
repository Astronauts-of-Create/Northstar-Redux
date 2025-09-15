package com.lightning.northstar.events;

import com.lightning.northstar.block.tech.rocket_controls.RocketControlsServerHandler;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber
public class NorthstarEvents {

    @SubscribeEvent
    public static void onServerWorldTick(LevelTickEvent.Post event) {
        Level world = event.getLevel();
        RocketControlsServerHandler.tick(world);
    }
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
    // VenusWeather.register(event.getDispatcher());
    // MarsWeather.register(event.getDispatcher());
    }
}
