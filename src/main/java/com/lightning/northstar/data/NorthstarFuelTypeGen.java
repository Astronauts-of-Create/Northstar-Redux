package com.lightning.northstar.data;

import com.drmangotea.tfmg.registry.TFMGFluids;
import com.jesz.createdieselgenerators.CDGFluids;
import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarRegistries;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.lightning.northstar.contraption.FuelType;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

public class NorthstarFuelTypeGen {

    public static void bootstrap(BootstapContext<FuelType> context) {
        context.register(key("biofuel"), FuelType.builder()
                .tag(NorthstarFluidTags.C_BIOFUEL)
                .gjPerMb(0.25f)
                .combustionEngine(0.5f, 16)
                .build());

        context.register(key("hydrocarbon"), FuelType.builder()
                .tag(NorthstarFluidTags.C_HYDROCARBON)
                .gjPerMb(1)
                .combustionEngine(1, 16)
                .build());

        context.register(key("hydrogen"), FuelType.builder()
                .tag(NorthstarFluidTags.C_HYDROGEN)
                .gjPerMb(0.5f)
                .combustionEngine(1, 32)
                .build());

        context.register(key("liquid_hydrogen"), FuelType.builder()
                .tag(NorthstarFluidTags.C_LIQUID_HYDROGEN)
                .gjPerMb(0.75f)
                .combustionEngine(1, 48)
                .build());

        context.register(key("methane"), FuelType.builder()
                .tag(NorthstarFluidTags.C_METHANE)
                .gjPerMb(2)
                .combustionEngine(1, 32)
                .build());

        context.register(key("cdg_biodiesel"), FuelType.builder()
                .fluid(CDGFluids.BIODIESEL.getSource())
                .combustionEngine(1, 16)
                .build());

        context.register(key("cdg_diesel"), FuelType.builder()
                .fluid(CDGFluids.DIESEL.getSource())
                .combustionEngine(1, 32)
                .build());

        context.register(key("cdg_gasoline"), FuelType.builder()
                .fluid(CDGFluids.GASOLINE.getSource())
                .combustionEngine(1, 32)
                .build());

        context.register(key("tfmg_diesel"), FuelType.builder()
                .fluid(TFMGFluids.DIESEL.getSource())
                .combustionEngine(1, 16)
                .build());

        context.register(key("tfmg_gasoline"), FuelType.builder()
                .fluid(TFMGFluids.GASOLINE.getSource())
                .combustionEngine(1, 16)
                .build());

        context.register(key("tfmg_naphtha"), FuelType.builder()
                .fluid(TFMGFluids.NAPHTHA.getSource())
                .gjPerMb(0.75f)
                .build());

        context.register(key("tfmg_kerosene"), FuelType.builder()
                .fluid(TFMGFluids.KEROSENE.getSource())
                .gjPerMb(1.00f)
                .build());
    }

    private static ResourceKey<FuelType> key(String name) {
        return ResourceKey.create(NorthstarRegistries.FUEL, Northstar.asResource(name));
    }

}
