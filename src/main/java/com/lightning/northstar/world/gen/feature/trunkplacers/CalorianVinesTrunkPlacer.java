package com.lightning.northstar.world.gen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.lightning.northstar.content.NorthstarBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

// TODO: similar to UpwardsBranchingTrunkPlacer
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CalorianVinesTrunkPlacer extends TrunkPlacer {

    public static final Codec<CalorianVinesTrunkPlacer> CODEC = RecordCodecBuilder.create(i -> trunkPlacerParts(i).and(i.group(
            IntProvider.POSITIVE_CODEC.fieldOf("extra_branch_steps").forGetter(p -> p.extraBranchSteps),
            Codec.floatRange(0.0F, 1.0F).fieldOf("place_branch_per_log_probability").forGetter(p -> p.placeBranchPerLogProbability),
            IntProvider.NON_NEGATIVE_CODEC.fieldOf("extra_branch_length").forGetter(p -> p.extraBranchLength),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_grow_through").forGetter(p -> p.canGrowThrough)
    )).apply(i, CalorianVinesTrunkPlacer::new));

    private final IntProvider extraBranchSteps;
    private final float placeBranchPerLogProbability;
    private final IntProvider extraBranchLength;
    private final HolderSet<Block> canGrowThrough;

    public CalorianVinesTrunkPlacer(int baseHeight, int heightRandA, int heightRandB,
                                    IntProvider extraBranchSteps, float placeBranchPerLogProbability,
                                    IntProvider extraBranchLength, HolderSet<Block> canGrowThrough) {
        super(baseHeight, heightRandA, heightRandB);
        this.extraBranchSteps = extraBranchSteps;
        this.placeBranchPerLogProbability = placeBranchPerLogProbability;
        this.extraBranchLength = extraBranchLength;
        this.canGrowThrough = canGrowThrough;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return NorthstarTrunkPlacerTypes.CALORIAN_VINES_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level,
                                                            BiConsumer<BlockPos, BlockState> blockSetter,
                                                            RandomSource random, int freeTreeHeight, BlockPos pos,
                                                            TreeConfiguration config) {
        List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
        placeLog(level, blockSetter, random, pos, config, Direction.UP);
        // 90 + random.nextInt(1, 12)
        // 90 + random.nextInt(1, 12)
        // 90 + random.nextInt(1, 12)
        // 90 + random.nextInt(1, 12)
        placeBranch(level, blockSetter, random, freeTreeHeight, pos.relative(Direction.NORTH), config, random.nextFloat(), Direction.NORTH);
        placeBranch(level, blockSetter, random, freeTreeHeight, pos.relative(Direction.SOUTH), config, random.nextFloat(), Direction.SOUTH);
        placeBranch(level, blockSetter, random, freeTreeHeight, pos.relative(Direction.EAST), config, random.nextFloat(), Direction.EAST);
        placeBranch(level, blockSetter, random, freeTreeHeight, pos.relative(Direction.WEST), config, random.nextFloat(), Direction.WEST);
        placeOre(blockSetter, pos);
        placeCap(blockSetter, pos);
        return list;
    }

    public List<FoliagePlacer.FoliageAttachment> placeBranch(LevelSimulatedReader level,
                                                             BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random,
                                                             int freeTreeHeight, BlockPos pos, TreeConfiguration config,
                                                             float bias, Direction dir) {
        List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
        BlockPos.MutableBlockPos mutable = pos.mutable();

        for (int i = 0; i < freeTreeHeight; i++) {
            if (random.nextFloat() > 0.7 && i != 0) {
                if (random.nextFloat() < bias) {
                    mutable.move(dir.getClockWise());
                } else {
                    mutable.move(dir.getCounterClockWise());
                }
            }

            if (random.nextFloat() > 0.9 && i != 0) {
                float newBias = random.nextFloat();
                while (0.49 > Math.abs(bias - newBias)) {
                    newBias = random.nextFloat();
                }
                placeSecondBranch(level, blockSetter, random, freeTreeHeight - 1, mutable, config, random.nextInt(1, 4), newBias, dir, random.nextBoolean() ? dir.getClockWise() : dir.getCounterClockWise());
            }
            int posX = mutable.getX();
            int posY = mutable.getY();
            int posZ = mutable.getZ();
            boolean moveFlag = false;
            if (level.isStateAtPosition(mutable.below(), block -> block.isSolidRender((BlockGetter) level, mutable.below())) && level.isStateAtPosition(mutable, BlockBehaviour.BlockStateBase::canBeReplaced)) {
                posY = mutable.getY();
                moveFlag = true;
                placeLog(level, blockSetter, random, mutable.set(posX, posY, posZ), config, dir);
            } else if (level.isStateAtPosition(mutable.below(), BlockBehaviour.BlockStateBase::canBeReplaced)) {
                placeLog(level, blockSetter, random, new BlockPos(mutable.set(posX, posY, posZ)), config, Direction.DOWN);
                mutable.move(Direction.DOWN);
            } else if (level.isStateAtPosition(mutable, block -> block.isSolidRender((BlockGetter) level, mutable)) && level.isStateAtPosition(mutable.above(), BlockBehaviour.BlockStateBase::canBeReplaced)) {
                mutable.move(0, 1, 0);
                moveFlag = true;
                posY = mutable.getY();
                placeLog(level, blockSetter, random, mutable.set(posX, posY, posZ), config, dir);
            } else if (level.isStateAtPosition(mutable.below().below(), block -> block.isSolidRender((BlockGetter) level, mutable.below().below())) && level.isStateAtPosition(mutable.below(), BlockBehaviour.BlockStateBase::canBeReplaced)) {
                mutable.move(0, -1, 0);
                moveFlag = true;
                posY = mutable.getY();
                placeLog(level, blockSetter, random, mutable.set(posX, posY, posZ), config, dir);
            } else if (level.isStateAtPosition(mutable.relative(dir), block -> block.isSolidRender((BlockGetter) level, mutable.relative(dir))) && level.isStateAtPosition(mutable, BlockBehaviour.BlockStateBase::canBeReplaced)) {
                placeLog(level, blockSetter, random, new BlockPos(mutable.set(posX, posY, posZ)), config, Direction.UP);
                mutable.move(Direction.UP);
            } else {
                break;
            }
            if ((level.isStateAtPosition(mutable.relative(dir).below(), BlockBehaviour.BlockStateBase::canBeReplaced)
                 || level.isStateAtPosition(mutable.relative(dir), BlockBehaviour.BlockStateBase::canBeReplaced)
                 || level.isStateAtPosition(mutable.relative(dir).above(), BlockBehaviour.BlockStateBase::canBeReplaced)) && moveFlag) {
                mutable.move(dir);
            }
        }
        return list;
    }

    public List<FoliagePlacer.FoliageAttachment> placeSecondBranch(LevelSimulatedReader level,
                                                                   BiConsumer<BlockPos, BlockState> pBlockSetter,
                                                                   RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos,
                                                                   TreeConfiguration treeconfiguration, int dist, float bias,
                                                                   Direction dir, Direction offshootDir) {
        List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
        BlockPos.MutableBlockPos mutable = pPos.mutable();

        for (int i = 0; i < pFreeTreeHeight; i++) {
            Direction newDir = dir;
            if (pRandom.nextFloat() > 0.7 && i > 3) {
                if (pRandom.nextFloat() > bias) {
                    mutable.move(dir.getClockWise());
                } else {
                    mutable.move(dir.getCounterClockWise());
                }
            }
            int posX = mutable.getX();
            int posY = mutable.getY();
            int posZ = mutable.getZ();
            boolean moveFlag = false;

            if (i <= dist) {
                newDir = offshootDir;
            }
            if (level.isStateAtPosition(mutable.below(), block -> block.isSolidRender((BlockGetter) level, mutable.below())) && level.isStateAtPosition(mutable, BlockBehaviour.BlockStateBase::canBeReplaced)) {
                posY = mutable.getY();
                moveFlag = true;
                placeLog(level, pBlockSetter, pRandom, new BlockPos(mutable.set(posX, posY, posZ)), treeconfiguration, newDir);
            } else if (level.isStateAtPosition(mutable.below(), BlockBehaviour.BlockStateBase::canBeReplaced)) {
                placeLog(level, pBlockSetter, pRandom, new BlockPos(mutable.set(posX, posY, posZ)), treeconfiguration, Direction.DOWN);
                mutable.move(Direction.DOWN);
            } else if (level.isStateAtPosition(mutable, block -> block.isSolidRender((BlockGetter) level, mutable)) && level.isStateAtPosition(mutable.above(), BlockBehaviour.BlockStateBase::canBeReplaced)) {
                mutable.move(0, 1, 0);
                moveFlag = true;
                posY = mutable.getY();
                placeLog(level, pBlockSetter, pRandom, new BlockPos(mutable.set(posX, posY, posZ)), treeconfiguration, newDir);
            } else if (level.isStateAtPosition(mutable.below().below(), block -> block.isSolidRender((BlockGetter) level, mutable.below().below())) && level.isStateAtPosition(mutable.below(), BlockBehaviour.BlockStateBase::canBeReplaced)) {
                mutable.move(0, -1, 0);
                moveFlag = true;
                posY = mutable.getY();
                placeLog(level, pBlockSetter, pRandom, new BlockPos(mutable.set(posX, posY, posZ)), treeconfiguration, newDir);
            } else if (level.isStateAtPosition(mutable.relative(dir), block -> block.isSolidRender((BlockGetter) level, mutable.relative(dir))) && level.isStateAtPosition(mutable, BlockBehaviour.BlockStateBase::canBeReplaced)) {
                placeLog(level, pBlockSetter, pRandom, new BlockPos(mutable.set(posX, posY, posZ)), treeconfiguration, Direction.UP);
                mutable.move(Direction.UP);
            } else {
                break;
            }
            if (level.isStateAtPosition(mutable.relative(dir).below(), BlockBehaviour.BlockStateBase::canBeReplaced)
                || level.isStateAtPosition(mutable.relative(dir), BlockBehaviour.BlockStateBase::canBeReplaced)
                || level.isStateAtPosition(mutable.relative(dir).above(), BlockBehaviour.BlockStateBase::canBeReplaced)) {
                if (i <= dist && moveFlag) {
                    mutable.move(offshootDir);
                } else if (moveFlag) {
                    mutable.move(dir);
                }
            }
        }
        return list;
    }

    public void placeOre(BiConsumer<BlockPos, BlockState> blockSetter, BlockPos pos) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    blockSetter.accept(pos.offset(x, y - 2, z), NorthstarBlocks.MERCURY_DEEP_TUNGSTEN_ORE.get().defaultBlockState());
                }
            }
        }
    }

    public void placeCap(BiConsumer<BlockPos, BlockState> blockSetter, BlockPos pos) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                blockSetter.accept(pos.offset(x, 1, z), Blocks.BLACKSTONE.defaultBlockState());
            }
        }

        blockSetter.accept(pos.offset(2, 2, -1), Blocks.BLACKSTONE.defaultBlockState());
        blockSetter.accept(pos.offset(2, 2, 0), Blocks.BLACKSTONE.defaultBlockState());
        blockSetter.accept(pos.offset(2, 2, 1), Blocks.BLACKSTONE.defaultBlockState());

        blockSetter.accept(pos.offset(-1, 2, 2), Blocks.BLACKSTONE.defaultBlockState());
        blockSetter.accept(pos.offset(0, 2, 2), Blocks.BLACKSTONE.defaultBlockState());
        blockSetter.accept(pos.offset(1, 2, 2), Blocks.BLACKSTONE.defaultBlockState());

        blockSetter.accept(pos.offset(-2, 2, -1), Blocks.BLACKSTONE.defaultBlockState());
        blockSetter.accept(pos.offset(-2, 2, 0), Blocks.BLACKSTONE.defaultBlockState());
        blockSetter.accept(pos.offset(-2, 2, 1), Blocks.BLACKSTONE.defaultBlockState());

        blockSetter.accept(pos.offset(-1, 2, -2), Blocks.BLACKSTONE.defaultBlockState());
        blockSetter.accept(pos.offset(0, 2, -2), Blocks.BLACKSTONE.defaultBlockState());
        blockSetter.accept(pos.offset(1, 2, -2), Blocks.BLACKSTONE.defaultBlockState());

        blockSetter.accept(pos.offset(0, 2, 0), Blocks.SHROOMLIGHT.defaultBlockState());
        blockSetter.accept(pos.offset(0, 3, 0), Blocks.SHROOMLIGHT.defaultBlockState());
        blockSetter.accept(pos.offset(0, 4, 0), Blocks.SHROOMLIGHT.defaultBlockState());
    }

    protected boolean placeLog(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, BlockPos pPos, TreeConfiguration treeconfiguration, Direction dir) {
        return placeLog(pLevel, pBlockSetter, pRandom, pPos, treeconfiguration, Function.identity(), dir);
    }

    protected boolean placeLog(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, BlockPos pPos, TreeConfiguration treeconfiguration, Function<Object, Object> function, Direction dir) {
        if (validTreePos(pLevel, pPos)) {
            pBlockSetter.accept(pPos, (BlockState) function.apply(treeconfiguration.trunkProvider.getState(pRandom, pPos).setValue(RotatedPillarBlock.AXIS, dir.getAxis())));
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean validTreePos(LevelSimulatedReader pLevel, BlockPos pPos) {
        return true;
    }

}