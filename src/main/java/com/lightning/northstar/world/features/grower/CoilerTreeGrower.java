package com.lightning.northstar.world.features.grower;

import com.lightning.northstar.data.worldgen.NorthstarConfiguredFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public class CoilerTreeGrower extends AbstractTreeGrower {

    @Override
    protected @Nullable ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pHasFlowers) {
        return NorthstarConfiguredFeatures.COILER;
    }

}
