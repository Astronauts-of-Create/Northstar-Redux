package com.lightning.northstar.world.gen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.lightning.northstar.world.gen.feature.configuration.AlienTreeConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ArgyreSaplingTrunkPlacer extends TrunkPlacer {

    public static final Codec<ArgyreSaplingTrunkPlacer> CODEC = RecordCodecBuilder.create(i -> trunkPlacerParts(i).and(i.group(
            IntProvider.POSITIVE_CODEC.fieldOf("extra_branch_steps").forGetter(p -> p.extraBranchSteps),
            Codec.floatRange(0.0F, 1.0F).fieldOf("place_branch_per_log_probability").forGetter(p -> p.placeBranchPerLogProbability),
            IntProvider.NON_NEGATIVE_CODEC.fieldOf("extra_branch_length").forGetter(p -> p.extraBranchLength),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_grow_through").forGetter(p -> p.canGrowThrough),
            IntProvider.NON_NEGATIVE_CODEC.fieldOf("extra_branch_length").forGetter(p -> p.spinFactor))
    ).apply(i, ArgyreSaplingTrunkPlacer::new));

    private final IntProvider extraBranchSteps;
    private final float placeBranchPerLogProbability;
    private final IntProvider extraBranchLength;
    private final HolderSet<Block> canGrowThrough;
    private final IntProvider spinFactor;

    public ArgyreSaplingTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider extraBranchSteps, float placeBranchPerLogProbability, IntProvider extraBranchLength, HolderSet<Block> canGrowThrough, IntProvider spinFactor) {
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
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int spinX = 0;
        int spinZ = 0;
        int spinZAdd = -1;
        int spinXAdd = 1;
        int branchCount = 0;
        int lastBranch = 0;
        for (int i = 0; i < pFreeTreeHeight; ) {

            if (Math.random() > 0.3) {
                i++;
                if (pRandom.nextFloat() > 0.9 && branchCount < 3 && lastBranch > 3) {
                    list.addAll(this.placeBranch(pLevel, pBlockSetter, pRandom, pFreeTreeHeight + pRandom.nextIntBetweenInclusive(-pFreeTreeHeight / 4, pFreeTreeHeight / 4), pPos, treeconfiguration, spinX, spinZ));
                    branchCount++;
                    lastBranch = 0;
                }
            }
            lastBranch++;
            if (Math.random() > 0.5) {
                spinX += spinXAdd;
            }
            if (Math.random() > 0.5) {
                spinZ += spinZAdd;
            }
            if (Math.random() < 0.5) {
                spinX -= spinXAdd;
            }
            if (Math.random() < 0.5) {
                spinZ -= spinZAdd;
            }
            int Xpos = pPos.getX() + spinX;
            int Zpos = pPos.getZ() + spinZ;
            int j = pPos.getY() + i;

            this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos, j, Zpos), treeconfiguration);
            this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos + 1, j, Zpos), treeconfiguration);
            this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos, j, Zpos + 1), treeconfiguration);
            this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos - 1, j, Zpos), treeconfiguration);
            this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos, j, Zpos - 1), treeconfiguration);
            if (i == 1) {
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos, j - 1, Zpos), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos + 1, j - 1, Zpos), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos, j - 1, Zpos + 1), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos - 1, j - 1, Zpos), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos, j - 1, Zpos - 1), treeconfiguration);

                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos + 2, j - 1, Zpos), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos - 2, j - 1, Zpos), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos, j - 1, Zpos + 2), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos, j - 1, Zpos - 2), treeconfiguration);

                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos + 1, j - 1, Zpos + 1), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos - 1, j - 1, Zpos - 1), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos + 1, j - 1, Zpos - 1), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos - 1, j - 1, Zpos + 1), treeconfiguration);
            }


            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos + 1, j, Zpos), treeconfiguration);
            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos, j, Zpos + 1), treeconfiguration);
            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos + 1, j, Zpos + 1), treeconfiguration);


            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos, j - 1, Zpos), treeconfiguration);

            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos + 1, j - 1, Zpos), treeconfiguration);
            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos, j - 1, Zpos + 1), treeconfiguration);
            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos + 1, j - 1, Zpos + 1), treeconfiguration);

            //  }
            //  if (this.spinFactor == UniformInt.of(1,1)) {
            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos - 1, j, Zpos), treeconfiguration);
            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos, j, Zpos - 1), treeconfiguration);
            if (i == pFreeTreeHeight - 1) {
                list.add(new FoliagePlacer.FoliageAttachment(blockpos$mutableblockpos.set(Xpos, j + 4, Zpos), 0, false));
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos + 1, j, Zpos), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos - 1, j, Zpos), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos, j, Zpos + 1), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos.set(Xpos, j, Zpos - 1), treeconfiguration);
            }
        }
        //   }
        return list;
    }

    public List<FoliagePlacer.FoliageAttachment> placeBranch(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight,
                                                             BlockPos pPos, TreeConfiguration treeconfiguration, int oldspinX, int oldspinZ) {
        List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
        BlockPos.MutableBlockPos blockpos$mutableblockpos2 = new BlockPos.MutableBlockPos();
        int spinX = 0;
        int spinZ = 0;
        int spinZAdd = -1;
        int spinXAdd = 1;

        for (int i = 0; i < pFreeTreeHeight; ) {
            if (Math.random() > 0.2) {
                i++;
            }
            if (Math.random() > 0.35) {
                spinX += spinXAdd;
            }
            if (Math.random() > 0.35) {
                spinZ += spinZAdd;
            }
            if (Math.random() < 0.35) {
                spinX -= spinXAdd;
            }
            if (Math.random() < 0.35) {
                spinZ -= spinZAdd;
            }
            int Xpos = pPos.getX() + spinX;
            int Zpos = pPos.getZ() + spinZ;
            int j = pPos.getY() + i;

            this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos, j, Zpos), treeconfiguration);
            //  if (this.spinFactor == UniformInt.of(1,1)) {
            this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos - 1, j, Zpos), treeconfiguration);
            this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos, j, Zpos - 1), treeconfiguration);
            this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos + 1, j, Zpos), treeconfiguration);
            this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos, j, Zpos + 1), treeconfiguration);

            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos + 1, j, Zpos), treeconfiguration);
            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos, j, Zpos + 1), treeconfiguration);
            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos + 1, j, Zpos + 1), treeconfiguration);

            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos, j - 1, Zpos), treeconfiguration);
            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos + 1, j - 1, Zpos), treeconfiguration);
            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos, j - 1, Zpos + 1), treeconfiguration);
            //  this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos + 1, j - 1, Zpos + 1), treeconfiguration);
            if (i == pFreeTreeHeight - 1) {
                list.add(new FoliagePlacer.FoliageAttachment(blockpos$mutableblockpos2.set(Xpos, j + 4, Zpos), 0, false));
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos + 1, j, Zpos), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos - 1, j, Zpos), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos, j, Zpos + 1), treeconfiguration);
                this.placeLog(pLevel, pBlockSetter, pRandom, blockpos$mutableblockpos2.set(Xpos, j, Zpos - 1), treeconfiguration);
            }
            //  }
        }
        return list;
    }

    protected boolean placeLog(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, BlockPos pPos, AlienTreeConfig treeconfiguration) {
        return this.placeLog(pLevel, pBlockSetter, pRandom, pPos, treeconfiguration, Function.identity());
    }

    protected boolean placeLog(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, BlockPos pPos, AlienTreeConfig treeconfiguration, Function<Object, Object> function) {
        if (this.validTreePos(pLevel, pPos)) {
            pBlockSetter.accept(pPos, (BlockState) function.apply(treeconfiguration.trunkProvider().getState(pRandom, pPos)));
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean validTreePos(LevelSimulatedReader pLevel, BlockPos pPos) {
        return pLevel.isStateAtPosition(pPos, BlockBehaviour.BlockStateBase::canBeReplaced);
    }
}