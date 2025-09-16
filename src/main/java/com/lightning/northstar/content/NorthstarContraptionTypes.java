package com.lightning.northstar.content;

import com.lightning.northstar.contraption.rocket.RocketContraption;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

import static com.simibubi.create.AllContraptionTypes.BY_LEGACY_NAME;

public class NorthstarContraptionTypes {

    public static final ContraptionType ROCKET = register("northstar:rocket", RocketContraption::new).value();

    private static Holder.Reference<ContraptionType> register(String name, Supplier<? extends Contraption> factory) {
        ContraptionType type = new ContraptionType(factory);
        BY_LEGACY_NAME.put(name, type);
        return Registry.registerForHolder(CreateBuiltInRegistries.CONTRAPTION_TYPE, ResourceLocation.parse(name), type);
    }

    public static void register() {
    }

}
