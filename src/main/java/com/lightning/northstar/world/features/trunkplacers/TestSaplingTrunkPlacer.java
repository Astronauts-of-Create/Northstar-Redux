package com.lightning.northstar.world.features.trunkplacers;

import com.google.common.collect.Lists;
import com.lightning.northstar.content.NorthstarBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

//

public class TestSaplingTrunkPlacer extends TrunkPlacer {

    public static final MapCodec<TestSaplingTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(i -> trunkPlacerParts(i).and(i.group(
            IntProvider.POSITIVE_CODEC.fieldOf("extra_branch_steps").forGetter(p -> p.extraBranchSteps),
            Codec.floatRange(0.0F, 1.0F).fieldOf("place_branch_per_log_probability").forGetter(p -> p.placeBranchPerLogProbability),
            IntProvider.NON_NEGATIVE_CODEC.fieldOf("extra_branch_length").forGetter(p -> p.extraBranchLength),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_grow_through").forGetter(p -> p.canGrowThrough),
            IntProvider.NON_NEGATIVE_CODEC.fieldOf("extra_branch_length").forGetter(p -> p.spinFactor)
    )).apply(i, TestSaplingTrunkPlacer::new));

    private final IntProvider extraBranchSteps;
    private final float placeBranchPerLogProbability;
    private final IntProvider extraBranchLength;
    private final HolderSet<Block> canGrowThrough;
    private final IntProvider spinFactor;

    public TestSaplingTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider extraBranchSteps, float placeBranchPerLogProbability, IntProvider extraBranchLength, HolderSet<Block> canGrowThrough, IntProvider spinFactor) {
        super(baseHeight, heightRandA, heightRandB);
        this.extraBranchSteps = extraBranchSteps;
        this.placeBranchPerLogProbability = placeBranchPerLogProbability;
        this.extraBranchLength = extraBranchLength;
        this.canGrowThrough = canGrowThrough;
        this.spinFactor = spinFactor;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.DARK_OAK_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration treeconfiguration) {
        List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
        this.placeLog(pLevel, pBlockSetter, pRandom, pPos, treeconfiguration, Direction.UP);
        this.placeBranch(pLevel, pBlockSetter, pRandom, pFreeTreeHeight, pPos.relative(Direction.NORTH), treeconfiguration, pRandom.nextFloat(), Direction.NORTH);
        this.placeBranch(pLevel, pBlockSetter, pRandom, pFreeTreeHeight, pPos.relative(Direction.SOUTH), treeconfiguration, pRandom.nextFloat(), Direction.SOUTH);
        this.placeBranch(pLevel, pBlockSetter, pRandom, pFreeTreeHeight, pPos.relative(Direction.EAST), treeconfiguration, pRandom.nextFloat(), Direction.EAST);
        this.placeBranch(pLevel, pBlockSetter, pRandom, pFreeTreeHeight, pPos.relative(Direction.WEST), treeconfiguration, pRandom.nextFloat(), Direction.WEST);
        this.placeOre(pLevel, pBlockSetter, pRandom, pFreeTreeHeight, pPos, treeconfiguration);
        this.placeCap(pLevel, pBlockSetter, pRandom, pFreeTreeHeight, pPos, treeconfiguration);
        //   }
        return list;
    }

    public List<FoliagePlacer.FoliageAttachment> placeBranch(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight,
                                                             BlockPos pPos, TreeConfiguration treeconfiguration, float bias, Direction dir) {
        List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
        BlockPos.MutableBlockPos mutable = pPos.mutable();

        int yPos = pPos.getY();
        for (int i = 0; i < pFreeTreeHeight; ) {
            if (pRandom.nextFloat() > 0.7 && i != 0) {
                if (pRandom.nextFloat() < bias) {
                    mutable.move(dir.getClockWise());
                } else {
                    mutable.move(dir.getCounterClockWise());
                }
            }

            if (pRandom.nextFloat() > 0.9 && i != 0) {
                float newBias = pRandom.nextFloat();
                while (0.49 > Math.abs(bias - newBias)) {
                    newBias = pRandom.nextFloat();
                }
                this.placeSecondBranch(pLevel, pBlockSetter, pRandom, pFreeTreeHeight - 1, mutable, treeconfiguration, pRandom.nextInt(1, 4), newBias, dir, pRandom.nextBoolean() ? dir.getClockWise() : dir.getCounterClockWise());
            }
            yPos = mutable.getY();
            int Xpos = mutable.getX();
            int Zpos = mutable.getZ();
            boolean moveFlag = false;
            if (pLevel.isStateAtPosition(mutable.below(), block -> block.isSolidRender((BlockGetter) pLevel, mutable.below())) && pLevel.isStateAtPosition(mutable, BlockBehaviour.BlockStateBase::canBeReplaced)) {
                yPos = mutable.getY();
                moveFlag = true;
                this.placeLog(pLevel, pBlockSetter, pRandom, mutable.set(Xpos, yPos, Zpos), treeconfiguration, dir);
            } else if (pLevel.isStateAtPosition(mutable.below(), BlockBehaviour.BlockStateBase::canBeReplaced)) {
                this.placeLog(pLevel, pBlockSetter, pRandom, new BlockPos(mutable.set(Xpos, yPos, Zpos)), treeconfiguration, Direction.DOWN);
                mutable.move(Direction.DOWN);
            } else if (pLevel.isStateAtPosition(mutable, block -> block.isSolidRender((BlockGetter) pLevel, mutable)) && pLevel.isStateAtPosition(mutable.above(), BlockBehaviour.BlockStateBase::canBeReplaced)) {
                mutable.move(0, 1, 0);
                moveFlag = true;
                yPos = mutable.getY();
                this.placeLog(pLevel, pBlockSetter, pRandom, mutable.set(Xpos, yPos, Zpos), treeconfiguration, dir);
            } else if (pLevel.isStateAtPosition(mutable.below().below(), block -> block.isSolidRender((BlockGetter) pLevel, mutable.below().below())) && pLevel.isStateAtPosition(mutable.below(), BlockBehaviour.BlockStateBase::canBeReplaced)) {
                mutable.move(0, -1, 0);
                moveFlag = true;
                yPos = mutable.getY();
                this.placeLog(pLevel, pBlockSetter, pRandom, mutable.set(Xpos, yPos, Zpos), treeconfiguration, dir);
            } else if (pLevel.isStateAtPosition(mutable.relative(dir), block -> block.isSolidRender((BlockGetter) pLevel, mutable.relative(dir))) && pLevel.isStateAtPosition(mutable, BlockBehaviour.BlockStateBase::canBeReplaced)) {
                this.placeLog(pLevel, pBlockSetter, pRandom, new BlockPos(mutable.set(Xpos, yPos, Zpos)), treeconfiguration, Direction.UP);
                mutable.move(Direction.UP);
            } else {
                i += 999999999;
            }
            if ((pLevel.isStateAtPosition(mutable.relative(dir).below(), BlockBehaviour.BlockStateBase::canBeReplaced)
                    || pLevel.isStateAtPosition(mutable.relative(dir), BlockBehaviour.BlockStateBase::canBeReplaced)
                    || pLevel.isStateAtPosition(mutable.relative(dir).above(), BlockBehaviour.BlockStateBase::canBeReplaced)) && moveFlag) {
                mutable.move(dir);
            }
            i++;
        }
        return list;
    }

    public List<FoliagePlacer.FoliageAttachment> placeSecondBranch(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight,
                                                                   BlockPos pPos, TreeConfiguration treeconfiguration, int dist, float bias, Direction dir, Direction offshootDir) {
        List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
        BlockPos.MutableBlockPos mutable = pPos.mutable();

        int yPos = pPos.getY();
        for (int i = 0; i < pFreeTreeHeight; ) {
            Direction newDir = dir;
            if (pRandom.nextFloat() > 0.7 && i > 3) {
                if (pRandom.nextFloat() > bias) {
                    mutable.move(dir.getClockWise());
                } else {
                    mutable.move(dir.getCounterClockWise());
                }
            }
            yPos = mutable.getY();
            int Xpos = mutable.getX();
            int Zpos = mutable.getZ();
            boolean moveFlag = false;

            if (i <= dist) {
                newDir = offshootDir;
            }
            if (pLevel.isStateAtPosition(mutable.below(), block -> block.isSolidRender((BlockGetter) pLevel, mutable.below())) && pLevel.isStateAtPosition(mutable, BlockBehaviour.BlockStateBase::canBeReplaced)) {
                yPos = mutable.getY();
                moveFlag = true;
                this.placeLog(pLevel, pBlockSetter, pRandom, new BlockPos(mutable.set(Xpos, yPos, Zpos)), treeconfiguration, newDir);
            } else if (pLevel.isStateAtPosition(mutable.below(), BlockBehaviour.BlockStateBase::canBeReplaced)) {
                this.placeLog(pLevel, pBlockSetter, pRandom, new BlockPos(mutable.set(Xpos, yPos, Zpos)), treeconfiguration, Direction.DOWN);
                mutable.move(Direction.DOWN);
            } else if (pLevel.isStateAtPosition(mutable, block -> block.isSolidRender((BlockGetter) pLevel, mutable)) && pLevel.isStateAtPosition(mutable.above(), BlockBehaviour.BlockStateBase::canBeReplaced)) {
                mutable.move(0, 1, 0);
                moveFlag = true;
                yPos = mutable.getY();
                this.placeLog(pLevel, pBlockSetter, pRandom, new BlockPos(mutable.set(Xpos, yPos, Zpos)), treeconfiguration, newDir);
            } else if (pLevel.isStateAtPosition(mutable.below().below(), block -> block.isSolidRender((BlockGetter) pLevel, mutable.below().below())) && pLevel.isStateAtPosition(mutable.below(), BlockBehaviour.BlockStateBase::canBeReplaced)) {
                mutable.move(0, -1, 0);
                moveFlag = true;
                yPos = mutable.getY();
                this.placeLog(pLevel, pBlockSetter, pRandom, new BlockPos(mutable.set(Xpos, yPos, Zpos)), treeconfiguration, newDir);
            } else if (pLevel.isStateAtPosition(mutable.relative(dir), block -> block.isSolidRender((BlockGetter) pLevel, mutable.relative(dir))) && pLevel.isStateAtPosition(mutable, BlockBehaviour.BlockStateBase::canBeReplaced)) {
                this.placeLog(pLevel, pBlockSetter, pRandom, new BlockPos(mutable.set(Xpos, yPos, Zpos)), treeconfiguration, Direction.UP);
                mutable.move(Direction.UP);
            } else {
                i += 999999999;
            }
            if (pLevel.isStateAtPosition(mutable.relative(dir).below(), BlockBehaviour.BlockStateBase::canBeReplaced)
                    || pLevel.isStateAtPosition(mutable.relative(dir), BlockBehaviour.BlockStateBase::canBeReplaced)
                    || pLevel.isStateAtPosition(mutable.relative(dir).above(), BlockBehaviour.BlockStateBase::canBeReplaced)) {
                if (i <= dist && moveFlag) {
                    mutable.move(offshootDir);
                } else if (moveFlag) {
                    mutable.move(dir);
                }
            }
            i++;
        }
        return list;
    }

    public void placeOre(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight,
                         BlockPos pPos, TreeConfiguration treeconfiguration) {

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    pBlockSetter.accept(pPos.offset(x, y - 2, z), NorthstarBlocks.MERCURY_DEEP_TUNGSTEN_ORE.get().defaultBlockState());
                }
            }
        }
    }

    public void placeCap(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight,
                         BlockPos pPos, TreeConfiguration treeconfiguration) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                pBlockSetter.accept(pPos.offset(x, 1, z), Blocks.BLACKSTONE.defaultBlockState());
            }
        }

        pBlockSetter.accept(pPos.offset(2, 2, -1), Blocks.BLACKSTONE.defaultBlockState());
        pBlockSetter.accept(pPos.offset(2, 2, 0), Blocks.BLACKSTONE.defaultBlockState());
        pBlockSetter.accept(pPos.offset(2, 2, 1), Blocks.BLACKSTONE.defaultBlockState());

        pBlockSetter.accept(pPos.offset(-1, 2, 2), Blocks.BLACKSTONE.defaultBlockState());
        pBlockSetter.accept(pPos.offset(0, 2, 2), Blocks.BLACKSTONE.defaultBlockState());
        pBlockSetter.accept(pPos.offset(1, 2, 2), Blocks.BLACKSTONE.defaultBlockState());

        pBlockSetter.accept(pPos.offset(-2, 2, -1), Blocks.BLACKSTONE.defaultBlockState());
        pBlockSetter.accept(pPos.offset(-2, 2, 0), Blocks.BLACKSTONE.defaultBlockState());
        pBlockSetter.accept(pPos.offset(-2, 2, 1), Blocks.BLACKSTONE.defaultBlockState());

        pBlockSetter.accept(pPos.offset(-1, 2, -2), Blocks.BLACKSTONE.defaultBlockState());
        pBlockSetter.accept(pPos.offset(0, 2, -2), Blocks.BLACKSTONE.defaultBlockState());
        pBlockSetter.accept(pPos.offset(1, 2, -2), Blocks.BLACKSTONE.defaultBlockState());

        pBlockSetter.accept(pPos.offset(0, 2, 0), Blocks.SHROOMLIGHT.defaultBlockState());
        pBlockSetter.accept(pPos.offset(0, 3, 0), Blocks.SHROOMLIGHT.defaultBlockState());
        pBlockSetter.accept(pPos.offset(0, 4, 0), Blocks.SHROOMLIGHT.defaultBlockState());
    }

    protected boolean placeLog(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, BlockPos pPos, TreeConfiguration treeconfiguration, Direction dir) {
        return this.placeLog(pLevel, pBlockSetter, pRandom, pPos, treeconfiguration, Function.identity(), dir);
    }

    protected boolean placeLog(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, BlockPos pPos, TreeConfiguration treeconfiguration, Function<Object, Object> function, Direction dir) {
        if (this.validTreePos(pLevel, pPos)) {
            pBlockSetter.accept(pPos, (BlockState) function.apply(treeconfiguration.trunkProvider.getState(pRandom, pPos).setValue(RotatedPillarBlock.AXIS, dir.getAxis())));
            return true;
        } else {
            return false;
        }
    }

    protected boolean placeBlackstone(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, BlockPos pPos, TreeConfiguration treeconfiguration, Function<Object, Object> function, Direction dir) {
        if (this.validTreePos(pLevel, pPos)) {
            pBlockSetter.accept(pPos, Blocks.BLACKSTONE.defaultBlockState());
            return true;
        } else {
            return false;
        }
    }

    protected boolean placeShroomlight(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, BlockPos pPos, TreeConfiguration treeconfiguration, Function<Object, Object> function, Direction dir) {
        if (this.validTreePos(pLevel, pPos)) {
            pBlockSetter.accept(pPos, Blocks.SHROOMLIGHT.defaultBlockState());
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