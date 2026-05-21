package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.contraption.rocket.RocketContraption;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class NorthstarContraptionTypes {

    private static final DeferredRegister<ContraptionType> REGISTER = DeferredRegister.create(CreateRegistries.CONTRAPTION_TYPE, Northstar.MOD_ID);

    public static final RegistryObject<ContraptionType> ROCKET = REGISTER.register("rocket", () -> new ContraptionType(RocketContraption::new));

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

}
