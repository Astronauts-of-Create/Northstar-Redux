package com.lightning.northstar.content.world;

import com.lightning.northstar.Northstar;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;

public class NorthstarDensityFunctions {

    public static final ResourceKey<DensityFunction>
            DEPTH = key("depth"),
            MERCURY = key("mercury");

    private static ResourceKey<DensityFunction> key(String path) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, Northstar.asResource(path));
    }

    public static void bootstrap(BootstapContext<DensityFunction> context) {
        HolderGetter<DensityFunction> densityFunctions = context.lookup(Registries.DENSITY_FUNCTION);

        context.register(
                DEPTH,
                DensityFunctions.add(
                        DensityFunctions.yClampedGradient(
                                -64,
                                320,
                                2,
                                -1.5
                        ),
                        new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(NoiseRouterData.OFFSET))
                )
        );

        context.register(
                MERCURY,
                BlendedNoise.createUnseeded(
                        0.25,
                        0.125,
                        80,
                        160,
                        8
                )
        );
    }

}
