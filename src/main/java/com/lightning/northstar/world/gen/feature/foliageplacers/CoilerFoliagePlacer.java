package com.lightning.northstar.world.gen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

import javax.annotation.ParametersAreNonnullByDefault;

// TODO: unused
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CoilerFoliagePlacer extends FoliagePlacer {

    public static final Codec<CoilerFoliagePlacer> CODEC = RecordCodecBuilder.create(i -> foliagePlacerParts(i)
            .apply(i, CoilerFoliagePlacer::new));

    public CoilerFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return FoliagePlacerType.ACACIA_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(LevelSimulatedReader level, FoliagePlacer.FoliageSetter blocksetter, RandomSource pRandom, TreeConfiguration pConfig, int int1, FoliagePlacer.FoliageAttachment foliage, int int2, int int3, int int4) {
        boolean flag = foliage.doubleTrunk();
        BlockPos blockpos = foliage.pos().above(int4);
        this.placeLeavesRow(level, blocksetter, pRandom, pConfig, blockpos, int3 + foliage.radiusOffset(), -1 - int2, flag);
        this.placeLeavesRow(level, blocksetter, pRandom, pConfig, blockpos, int3 - 1, -int2, flag);
        this.placeLeavesRow(level, blocksetter, pRandom, pConfig, blockpos, int3 - 1, -2 - int2, flag);
        this.placeLeavesRow(level, blocksetter, pRandom, pConfig, blockpos, int3 - 3, 1 - int2, flag);
    }

    @Override
    public int foliageHeight(RandomSource pRandom, int p_225496_, TreeConfiguration p_225497_) {
        return 0;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        if (localY == 0) {
            return (localX > 1 || localZ > 1) && localX != 0 && localZ != 0;
        } else {
            return localX == range && localZ == range && range > 0;
        }
    }
}