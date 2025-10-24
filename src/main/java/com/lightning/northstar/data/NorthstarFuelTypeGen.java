package com.lightning.northstar.data;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarRegistries;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.lightning.northstar.contraption.FuelType;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

public class NorthstarFuelTypeGen {

    public static void bootstrap(BootstapContext<FuelType> context) {
        context.register(key("biofuel"), new FuelType(
                NorthstarFluidTags.C_BIOFUEL.tag,
                0.25f,
                4, 16));

        context.register(key("cdg_biodiesel"), new FuelType(
                NorthstarFluidTags.COMPAT_CDG_BIODIESEL.tag,
                0.25f,
                4, 16));

        context.register(key("tfmg_kerosene"), new FuelType(
                NorthstarFluidTags.COMPAT_TFMG_KEROSENE.tag,
                1f,
                2, 16));

        context.register(key("tfmg_diesel"), new FuelType(
                NorthstarFluidTags.COMPAT_TFMG_DIESEL.tag,
                2f,
                5, 32));

        context.register(key("tfmg_naphtha"), new FuelType(
                NorthstarFluidTags.COMPAT_TFMG_NAPHTHA.tag,
                1.5f,
                4, 16));


        context.register(key("hydrocarbon"), new FuelType(
                NorthstarFluidTags.C_HYDROCARBON.tag,
                1,
                4, 16));

        context.register(key("hydrogen"), new FuelType(
                NorthstarFluidTags.C_HYDROGEN.tag,
                1.5f,
                4, 32));

        context.register(key("liquid_hydrogen"), new FuelType(
                NorthstarFluidTags.C_LIQUID_HYDROGEN.tag,
                4,
                1, 64));

        context.register(key("methane"), new FuelType(
                NorthstarFluidTags.C_METHANE.tag,
                2,
                2, 32));
    }

    private static ResourceKey<FuelType> key(String name) {
        return ResourceKey.create(NorthstarRegistries.FUEL, Northstar.asResource(name));
    }

}
