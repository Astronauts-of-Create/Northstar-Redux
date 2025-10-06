package com.lightning.northstar.data.worldgen;

import com.lightning.northstar.data.worldgen.biomes.LunarBiomes;
import com.lightning.northstar.data.worldgen.biomes.MarsBiomes;
import com.lightning.northstar.data.worldgen.biomes.MercuryBiomes;
import com.lightning.northstar.data.worldgen.biomes.VenusBiomes;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.Biome;

public class NorthstarBiomeProvider {

    public static void bootstrap(BootstapContext<Biome> context) {
        LunarBiomes.boostrap(context);
        MarsBiomes.boostrap(context);
        VenusBiomes.boostrap(context);
        MercuryBiomes.boostrap(context);
    }
}
