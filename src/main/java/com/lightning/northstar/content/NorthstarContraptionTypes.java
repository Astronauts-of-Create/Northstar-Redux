package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.contraption.rocket.RocketContraption;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NorthstarContraptionTypes {

    private static final DeferredRegister<ContraptionType> REGISTER = DeferredRegister.create(CreateRegistries.CONTRAPTION_TYPE, Northstar.MOD_ID);

    public static final DeferredHolder<ContraptionType, ContraptionType> ROCKET = REGISTER.register("rocket", () -> new ContraptionType(RocketContraption::new));

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

}
