package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;

public class NorthstarPois {

    private static final DeferredRegister<PoiType> REGISTER = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, Northstar.MOD_ID);

    public static final RegistryObject<PoiType> ROCKET_WAYPOINT = REGISTER.register("rocket_waypoint", () -> new PoiType(new HashSet<>(NorthstarBlocks.ROCKET_WAYPOINT.get().getStateDefinition().getPossibleStates()), 0, 1));

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

}
