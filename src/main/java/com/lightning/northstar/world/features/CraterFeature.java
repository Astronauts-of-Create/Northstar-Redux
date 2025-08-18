package com.lightning.northstar.world.features;

import com.lightning.northstar.world.features.configuration.CraterConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class CraterFeature extends Feature<CraterConfig> {

    public CraterFeature(Codec<CraterConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<CraterConfig> context) {
        CraterConfig config = context.config();
        BlockPos center = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        boolean flag = false;
        int j = center.getY() + config.halfHeight().sample(random);
        if (j <= 0) // lazy solution for now (prevent generation below Y 0), world gen rewrite is planned anyway
            return false;
        int k = center.getY() - config.halfHeight().sample(random) - 1;
        int l = config.radius().sample(random);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int pp = 0;
        for (int l_old = l; l > l_old / j && j > 0; l = (int) (l / 1.5)) {
            if (pp < config.depth().sample(random)) {
                pp++;
                center = center.below();
            }
            for (BlockPos blockpos1 : BlockPos.betweenClosed(center.offset(-l, 0, -l), center.offset(l, 0, l))) {
                int i1 = blockpos1.getX() - center.getX();
                int j1 = blockpos1.getZ() - center.getZ();
                if (i1 * i1 + j1 * j1 <= l * l - 0.1) {
                    flag |= this.placeColumn(config, level, random, j, k, blockpos$mutableblockpos.set(blockpos1));
                    this.placeColumnBelow(config, level, random, j, k, blockpos$mutableblockpos.set(blockpos1.below()));
                    this.clearAir(config, level, random, j, k, blockpos$mutableblockpos.set(blockpos1));
                }
            }
        }

        return flag;
    }

    protected boolean placeColumn(CraterConfig pConfig, WorldGenLevel pLevel, RandomSource pRandom, int pMaxY, int pMinY, BlockPos.MutableBlockPos pPos) {
        BlockState blockstate = pConfig.airProvider().getState(pRandom, pPos);

        if (pLevel.getBlockState(pPos).is(pConfig.canDelete())) {
            pLevel.setBlock(pPos, blockstate, 2);
        }
        this.markAboveForPostProcessing(pLevel, pPos);

        return true;
    }

    protected boolean clearAir(CraterConfig pConfig, WorldGenLevel pLevel, RandomSource pRandom, int pMaxY, int pMinY, BlockPos.MutableBlockPos pPos) {
        boolean flag = false;
        BlockState blockstate = pConfig.airProvider().getState(pRandom, pPos);
        for (int i = 0; i < 24; i++) {
            if (pLevel.getBlockState(pPos.atY(pPos.getY() + i)).is(pConfig.canDelete())) {
                pLevel.setBlock(pPos.atY(pPos.getY() + i), blockstate, 2);
            }
            flag = true;
        }

        return flag;
    }


    protected boolean placeColumnBelow(CraterConfig pConfig, WorldGenLevel pLevel, RandomSource pRandom, int pMaxY, int pMinY, BlockPos.MutableBlockPos pPos) {
        BlockState blockstate = pConfig.blockProvider().getState(pRandom, pPos);
        if (pLevel.getBlockState(pPos) == Blocks.AIR.defaultBlockState()) {
            return false;
        }
        if (pLevel.getBlockState(pPos).is(pConfig.canDelete())) {
            pLevel.setBlock(pPos, blockstate, 2);
        }
        this.markAboveForPostProcessing(pLevel, pPos);
        return true;
    }

}
