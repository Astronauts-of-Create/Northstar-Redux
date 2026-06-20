package com.lightning.northstar;

import com.lightning.northstar.fluid.GasFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.FluidBuilder;
import net.minecraft.resources.ResourceLocation;

public class NorthstarRegistrate extends CreateRegistrate {

    protected NorthstarRegistrate(String modid) {
        super(modid);
    }

    public FluidBuilder<GasFluid, CreateRegistrate> gasFluid(String name) {
        return entry(name, c -> new FluidBuilder<>(
                self(),
                self(),
                name,
                c,
                ResourceLocation.fromNamespaceAndPath(getModid(), "fluid/" + name + "_still"),
                ResourceLocation.fromNamespaceAndPath(getModid(), "fluid/" + name + "_flow"),
                CreateRegistrate::defaultFluidType,
                p -> new GasFluid(p, false)
        ).source(p -> new GasFluid(p, true)));
    }


}
