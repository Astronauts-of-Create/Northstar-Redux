package com.lightning.northstar.world.gen.feature;

import com.lightning.northstar.world.gen.feature.configuration.StoneClusterConfiguration;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ClampedNormalFloat;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.Optional;
import java.util.OptionalInt;

public class StoneClusterFeature extends Feature<StoneClusterConfiguration> {

    public StoneClusterFeature(Codec<StoneClusterConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<StoneClusterConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        StoneClusterConfiguration config = context.config();
        RandomSource randomsource = context.random();
        /*if (!DripstoneUtils.isEmptyOrWater(level, pos)) {
            return false;
        }*/

        int height = config.height().sample(randomsource);
        float wetness = config.wetness().sample(randomsource);
        float density = config.density().sample(randomsource);
        int radiusX = config.radius().sample(randomsource);
        int radiusZ = config.radius().sample(randomsource);
        float lavaness = config.lavaness().sample(randomsource);
        float thirdthingness = config.thirdthingness().sample(randomsource);

        for (int x = -radiusX; x <= radiusX; ++x) {
            for (int z = -radiusZ; z <= radiusZ; ++z) {
                double stalagmiteChance = getChanceOfStalagmiteOrStalactite(radiusX, radiusZ, x, z, config);
                BlockPos placementPos = pos.offset(x, 0, z);
                placeColumn(level, randomsource, placementPos, x, z, wetness, lavaness, thirdthingness, stalagmiteChance, height, density, config);
            }
        }

        return true;

    }

    private void placeColumn(WorldGenLevel level, RandomSource random, BlockPos pos, int x, int y,
                             float wetnenss, float lavaness, float thirdthingness, double chance,
                             int height, float density, StoneClusterConfiguration config) {
        Optional<Column> optional = Column.scan(level, pos, config.floorToCeilingSearchRange(), block -> !block.isSolidRender(level, pos), blockdeny -> blockdeny.isSolidRender(level, pos));
        if (optional.isPresent()) {
            OptionalInt optionalint = optional.get().getCeiling();
            OptionalInt optionalint1 = optional.get().getFloor();
            if (optionalint.isPresent() || optionalint1.isPresent()) {
                boolean water_flag = random.nextFloat() < wetnenss;
                boolean lava_flag = random.nextFloat() < lavaness;
                boolean thirdthing_flag = random.nextFloat() < thirdthingness;
                Column column;
                if (water_flag && optionalint1.isPresent()) {
                    // && this.canPlacePool(level, pos.atY(optionalint1.getAsInt())
                    int i = optionalint1.getAsInt();
                    column = optional.get().withFloor(OptionalInt.of(i - 1));
                    level.setBlock(pos.atY(i), Blocks.WATER.defaultBlockState(), 2);
                    this.markAboveForPostProcessing(level, pos.atY(i).below());
                } else {
                    column = optional.get();
                }
                if (lava_flag && optionalint1.isPresent() && optionalint.isPresent()) {
                    // && this.canPlacePool(level, pos.atY(optionalint1.getAsInt())
                    int i = optionalint1.getAsInt();
                    column = optional.get().withFloor(OptionalInt.of(i - 1));
                    level.setBlock(pos.atY(i), Blocks.LAVA.defaultBlockState(), 2);
                    this.markAboveForPostProcessing(level, pos.atY(i).below());
                }
                if (thirdthing_flag && optionalint1.isPresent()) {
                    // && this.canPlacePool(level, pos.atY(optionalint1.getAsInt())
                    int i = optionalint1.getAsInt();
                    column = optional.get().withFloor(OptionalInt.of(i - 1));
                    level.setBlock(pos.atY(i), config.fluidProvider().createLegacyBlock(), 2);
                    this.markAboveForPostProcessing(level, pos.atY(i).below());
                }

                OptionalInt optionalint2 = column.getFloor();
                boolean flag1 = random.nextDouble() < chance;
                int j;
                if (optionalint.isPresent() && flag1 && !this.isLava(level, pos.atY(optionalint.getAsInt()))) {
                    int k = config.dripstoneBlockLayerThickness().sample(random);
                    this.replaceBlocksWithStoneBlocks(level, pos.atY(optionalint.getAsInt()), k, Direction.UP);
                    int l;
                    if (optionalint2.isPresent()) {
                        l = Math.min(height, optionalint.getAsInt() - optionalint2.getAsInt());
                    } else {
                        l = height;
                    }

                    j = this.getStoneHeight(random, x, y, density, l, config);
                } else {
                    j = 0;
                }


                // not working :(((((
                boolean flag2 = random.nextDouble() < chance;
                int i3;
                if (optionalint2.isPresent() && flag2 && !this.isLava(level, pos.atY(optionalint2.getAsInt()))) {
                    int i1 = config.dripstoneBlockLayerThickness().sample(random);
                    this.replaceBlocksWithStoneBlocks(level, pos.atY(optionalint2.getAsInt()), i1, Direction.DOWN);
                    if (optionalint.isPresent()) {
                        i3 = Math.max(0, j + Mth.randomBetweenInclusive(random, -config.maxStalagmiteStalactiteHeightDiff(), config.maxStalagmiteStalactiteHeightDiff()));
                    } else {
                        i3 = this.getStoneHeight(random, x, y, density, height, config);
                    }
                } else {
                    i3 = 0;
                }

                int j1;
                int j3;
                if (optionalint.isPresent() && optionalint2.isPresent() && optionalint.getAsInt() - j <= optionalint2.getAsInt() + i3) {
                    int k1 = optionalint2.getAsInt();
                    int l1 = optionalint.getAsInt();
                    int i2 = Math.max(l1 - j, k1 + 1);
                    int j2 = Math.min(k1 + i3, l1 - 1);
                    int k2 = Mth.randomBetweenInclusive(random, i2, j2 + 1);
                    int l2 = k2 - 1;
                    j3 = l1 - k2;
                    j1 = l2 - k1;
                } else {
                    j3 = j;
                    j1 = i3;
                }

                boolean flag3 = random.nextBoolean() && j3 > 0 && j1 > 0 && column.getHeight().isPresent() && j3 + j1 == column.getHeight().getAsInt();
                if (optionalint.isPresent()) {
                    growPointyStone(level, pos.atY(optionalint.getAsInt() - 1), Direction.DOWN, j3, flag3, config);
                }

                if (optionalint2.isPresent()) {
                    growPointyStone(level, pos.atY(optionalint2.getAsInt() + 1), Direction.UP, j1, flag3, config);
                }

            }
        }
    }


    private void replaceBlocksWithStoneBlocks(WorldGenLevel pLevel, BlockPos pPos, int pThickness, Direction pDirection) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = pPos.mutable();

        for (int i = 0; i < pThickness; ++i) {
            blockpos$mutableblockpos.move(pDirection);
        }
    }

    private double getChanceOfStalagmiteOrStalactite(int radiusX, int radiusZ, int x, int z, StoneClusterConfiguration config) {
        int dx = radiusX - Math.abs(x);
        int dy = radiusZ - Math.abs(z);
        int distance = Math.min(dx, dy);
        return Mth.clampedMap(distance, 0.0F, config.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn(), config.chanceOfDripstoneColumnAtMaxDistanceFromCenter(), 1.0F);
    }

    private int getStoneHeight(RandomSource pRandom, int pX, int pZ, float pChance, int pHeight, StoneClusterConfiguration config) {
        if (pRandom.nextFloat() > pChance) {
            return 0;
        } else {
            int i = Math.abs(pX) + Math.abs(pZ);
            float f = (float) Mth.clampedMap(i, 0.0D, config.maxDistanceFromCenterAffectingHeightBias(), (double) pHeight / 2.0D, 0.0D);
            return (int) randomBetweenBiased(pRandom, 0.0F, pHeight, f, config.heightDeviation());
        }
    }

    private static float randomBetweenBiased(RandomSource random, float min, float max, float mean, float deviation) {
        return ClampedNormalFloat.sample(random, mean, deviation, min, max);
    }

    private boolean isLava(LevelReader pLevel, BlockPos pPos) {
        return pLevel.getBlockState(pPos).is(Blocks.LAVA);
    }

    protected BlockPos scan(Direction dir, BlockPos pos, WorldGenLevel level, int scanDist) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        if (level.getBlockState(pos).isAir() && level.getBlockState(mutable.relative(dir)).isSolidRender(level, pos)) {
            return pos;
        }
        for (int i = 0; i < scanDist; i++) {
            mutable.move(Direction.UP);
            if (level.getBlockState(mutable).isAir() && level.getBlockState(mutable.relative(dir)).isSolidRender(level, pos)) {
                return new BlockPos(mutable.getX(), mutable.getY(), mutable.getZ());
            }
        }
        mutable = pos.mutable();
        for (int i = 0; i < scanDist; i++) {
            mutable.move(dir);
            if (level.getBlockState(mutable.relative(dir)).isSolidRender(level, pos) && level.getBlockState(mutable.relative(dir.getOpposite())).isAir()) {
                return new BlockPos(mutable.getX(), mutable.getY(), mutable.getZ());
            }
        }

        return pos;
    }

    protected static void growPointyStone(LevelAccessor pLevel, BlockPos pPos, Direction pDirection, int pHeight, boolean pMergeTip, StoneClusterConfiguration config) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = pPos.mutable();
        //  buildBaseToTipColumn(pDirection, pPos, pHeight, pMergeTip, (p_190846_) -> {
        //    p_190846_ = (BlockState) (AllPaletteStoneTypes.CRIMSITE.baseBlock);
        for (int l = 0; l == pHeight; ++l) {
            if (!pLevel.getBlockState(blockpos$mutableblockpos).isSolidRender(pLevel, blockpos$mutableblockpos)) {
                pLevel.setBlock(blockpos$mutableblockpos, config.stoneProvider().getState(RandomSource.create(), blockpos$mutableblockpos), 2);
            }
            blockpos$mutableblockpos.move(pDirection);
        }
        //  }, config);
    }


}
