package com.lightning.northstar.world.gen.feature;

import com.lightning.northstar.content.NorthstarBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import static net.minecraft.core.BlockPos.MutableBlockPos;

public class MercuryCactusFeature extends Feature<NoneFeatureConfiguration> {

    public MercuryCactusFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        MutableBlockPos newPos = pos.mutable();

        if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, newPos).getY() <= newPos.getY()) {
            return false;
        }
        BlockPos caveCeilingPos = scan(Direction.UP, pos, level, 24);
        if (caveCeilingPos == pos) {
            return false;
        }
        int caveHeight = caveCeilingPos.getY() / 2;
        RandomSource random = context.random();
        //int height = random.nextInt(5, 10);

        for (int i = 0; i < caveHeight; ) {
            if (level.getBlockState(newPos).isAir() || level.getBlockState(newPos).canBeReplaced()) {
                level.setBlock(newPos, NorthstarBlocks.MERCURY_CACTUS.get().defaultBlockState(), 3);
            } else {
                i += 99999;
            }
            if (i == caveHeight - 1) {
                placeBranch(level, newPos, random, (int) (caveHeight * 2.2f), Direction.NORTH, random.nextInt(1, 3));
                placeBranch(level, newPos, random, (int) (caveHeight * 2.2f), Direction.SOUTH, random.nextInt(1, 3));
                placeBranch(level, newPos, random, (int) (caveHeight * 2.2f), Direction.EAST, random.nextInt(1, 3));
                placeBranch(level, newPos, random, (int) (caveHeight * 2.2f), Direction.WEST, random.nextInt(1, 3));
            }
            if (random.nextInt(4) == 0 && i > 2) {
                newPos.move(Direction.Plane.HORIZONTAL.getRandomDirection(random));
            } else {
                newPos.move(Direction.UP);
                i++;
            }
        }
        return true;
    }

    public void placeBranch(WorldGenLevel level, BlockPos pos, RandomSource rando, int height, Direction dir, int dist) {
        MutableBlockPos newpos = pos.mutable();
        for (int i = 0; i < height; ++i) {
            if (i <= dist) {
                newpos.move(dir);
            } else if (rando.nextInt(4) != 0) {
                newpos.move(Direction.UP);
            } else {
                newpos.move(dir);
            }
            if (level.getBlockState(newpos).isAir() || level.getBlockState(newpos).canBeReplaced()) {
                level.setBlock(newpos, NorthstarBlocks.MERCURY_CACTUS.get().defaultBlockState(), 3);
                level.scheduleTick(newpos, NorthstarBlocks.MERCURY_CACTUS.get(), 2);
            } else {
                i += 99999;
            }
        }
    }

    protected BlockPos scan(Direction dir, BlockPos pos, WorldGenLevel level, int scanDist) {
        BlockPos newblockpos = pos;
        for (int i = 0; i < scanDist; i++) {
            newblockpos = newblockpos.relative(dir);
            if (level.getBlockState(newblockpos).isAir() && !level.getBlockState(newblockpos.above()).isAir()) {
                return newblockpos;
            }
        }
        return pos;
    }

}