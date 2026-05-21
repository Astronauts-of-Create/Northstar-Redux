package com.lightning.northstar.world.gen.feature.grower;

import com.lightning.northstar.content.world.planet.core.NorthstarVegetationConfiguredFeatures;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ArgyreSaplingTreeGrower extends AbstractTreeGrower {

    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
        return NorthstarVegetationConfiguredFeatures.ARGYRE; // argyre_small
    }

}
