package com.lightning.northstar.world.gen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageAttachment;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.BiConsumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ArgyreTrunkPlacer extends TrunkPlacer {

    public static final Codec<ArgyreTrunkPlacer> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.intRange(0, 32).fieldOf("base_height").forGetter(arg -> arg.baseHeight),
            Codec.intRange(0, 24).fieldOf("height_rand_a").forGetter(arg -> arg.heightRandA),
            Codec.intRange(0, 24).fieldOf("height_rand_b").forGetter(arg -> arg.heightRandB),
            IntProvider.POSITIVE_CODEC.fieldOf("extra_branch_steps").forGetter(p -> p.extraBranchSteps),
            Codec.floatRange(0.0F, 1.0F).fieldOf("place_branch_per_log_probability").forGetter(p -> p.placeBranchPerLogProbability),
            IntProvider.NON_NEGATIVE_CODEC.fieldOf("extra_branch_length").forGetter(p -> p.extraBranchLength),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_grow_through").forGetter(p -> p.canGrowThrough),
            IntProvider.NON_NEGATIVE_CODEC.fieldOf("size").forGetter(p -> p.size),
            BlockStateProvider.CODEC.fieldOf("glow_provider").forGetter(p -> p.glowProvider)
    ).apply(i, ArgyreTrunkPlacer::new));

    private final IntProvider extraBranchSteps;
    private final float placeBranchPerLogProbability;
    private final IntProvider extraBranchLength;
    private final HolderSet<Block> canGrowThrough;
    private final IntProvider size;
    private final BlockStateProvider glowProvider;

    public ArgyreTrunkPlacer(int baseHeight, int heightRandA, int heightRandB,
                             IntProvider extraBranchSteps, float placeBranchPerLogProbability, IntProvider extraBranchLength,
                             HolderSet<Block> canGrowThrough, IntProvider size, BlockStateProvider glowProvider) {
        super(baseHeight, heightRandA, heightRandB);
        this.extraBranchSteps = extraBranchSteps;
        this.placeBranchPerLogProbability = placeBranchPerLogProbability;
        this.extraBranchLength = extraBranchLength;
        this.canGrowThrough = canGrowThrough;
        this.size = size;
        this.glowProvider = glowProvider;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return NorthstarTrunkPlacerTypes.ARGYRE_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
                                              RandomSource random, int freeTreeHeight, BlockPos origin,
                                              TreeConfiguration config) {
        List<FoliagePlacer.FoliageAttachment> foliageAttachments = Lists.newArrayList();
        MutableBlockPos pos = new MutableBlockPos();
        int spinX = 0;
        int spinZ = 0;
        int spinIncrementX = 1;
        int spinIncrementZ = -1;

        for (int i = 0; i < freeTreeHeight; i++) {
            if (random.nextFloat() > 0.97)
                placeBranch(level, blockSetter, random, freeTreeHeight, origin, config, spinX, spinZ);

            if (random.nextFloat() > 0.9)
                spinX += spinIncrementX;
            if (random.nextFloat() > 0.7)
                spinZ += spinIncrementZ;
            if (random.nextFloat() < 0.3)
                spinX -= spinIncrementX;
            if (random.nextFloat() < 0.3)
                spinZ -= spinIncrementZ;

            int posX = origin.getX() + spinX;
            int posY = origin.getY() + i;
            int posZ = origin.getZ() + spinZ;

            int glowOffsetX = random.nextFloat() > 0.5 ? 1 : -1;
            int glowOffsetZ = random.nextFloat() > 0.5 ? 1 : -1;

            placeLog(level, blockSetter, random, pos.set(posX, posY, posZ), config);
            placeLog(level, blockSetter, random, pos.set(posX, posY - 1, posZ), config);
            //  if (spinFactor == UniformInt.of(-1,-1)) {
            placeLog(level, blockSetter, random, pos.set(posX + 1, posY, posZ), config);
            placeLog(level, blockSetter, random, pos.set(posX + 1, posY - 1, posZ), config);
            placeLog(level, blockSetter, random, pos.set(posX, posY, posZ + 1), config);
            placeLog(level, blockSetter, random, pos.set(posX, posY - 1, posZ + 1), config);
            //  }
            //  if (spinFactor == UniformInt.of(1,1)) {
            placeLog(level, blockSetter, random, pos.set(posX + 1, posY, posZ), config);
            placeLog(level, blockSetter, random, pos.set(posX + 1, posY - 1, posZ), config);
            placeLog(level, blockSetter, random, pos.set(posX - 1, posY, posZ), config);
            placeLog(level, blockSetter, random, pos.set(posX - 1, posY - 1, posZ), config);
            placeLog(level, blockSetter, random, pos.set(posX, posY, posZ + 1), config);
            placeLog(level, blockSetter, random, pos.set(posX, posY - 1, posZ + 1), config);
            placeLog(level, blockSetter, random, pos.set(posX, posY, posZ - 1), config);
            placeLog(level, blockSetter, random, pos.set(posX, posY - 1, posZ - 1), config);
            if (random.nextFloat() > 0.92) {
                placeLog(level, blockSetter, random, pos.set(posX + glowOffsetX, posY, posZ + glowOffsetZ), config, glowProvider);
            }
        }

        //              }
        return foliageAttachments;
    }

    public void placeBranch(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random,
                            int pFreeTreeHeight, BlockPos origin, TreeConfiguration treeconfiguration, int oldspinX, int oldspinZ) {
        MutableBlockPos pos = new MutableBlockPos();
        int spinX = 0;
        int spinZ = 0;
        int spinZAdd = -1;
        int spinXAdd = 1;

        for (int i = 0; i < pFreeTreeHeight; ) {
            if (random.nextFloat() > 0.3) {
                i++;
            }
            if (random.nextFloat() > 0.6) {
                spinX += spinXAdd;
            }
            if (random.nextFloat() > 0.6) {
                spinZ += spinZAdd;
            }
            if (random.nextFloat() < 0.4) {
                spinX -= spinXAdd;
            }
            if (random.nextFloat() < 0.4) {
                spinZ -= spinZAdd;
            }
            int Xpos = origin.getX() + spinX;
            int Zpos = origin.getZ() + spinZ;
            int j = origin.getY() + i;
            double rando1 = random.nextFloat();
            double rando2 = random.nextFloat();
            int glowoffsetX = 1;
            int glowoffsetZ = 1;
            if (rando1 > 0.5) {
                glowoffsetX = 1;
            } else {
                glowoffsetX = -1;
            }
            if (rando2 > 0.5) {
                glowoffsetZ = 1;
            } else {
                glowoffsetZ = -1;
            }

            placeLog(level, blockSetter, random, pos.set(Xpos, j, Zpos), treeconfiguration);
            placeLog(level, blockSetter, random, pos.set(Xpos, j - 1, Zpos), treeconfiguration);
            //  if (spinFactor == UniformInt.of(1,1)) {
            placeLog(level, blockSetter, random, pos.set(Xpos + 1, j, Zpos), treeconfiguration);
            placeLog(level, blockSetter, random, pos.set(Xpos + 1, j - 1, Zpos), treeconfiguration);
            placeLog(level, blockSetter, random, pos.set(Xpos, j, Zpos + 1), treeconfiguration);
            placeLog(level, blockSetter, random, pos.set(Xpos, j - 1, Zpos + 1), treeconfiguration);
            if (random.nextFloat() > 0.97) {
                placeLog(level, blockSetter, random, pos.set(Xpos + glowoffsetX, j, Zpos + glowoffsetZ), treeconfiguration, BlockStateProvider.simple(Blocks.GLOWSTONE));
            }
            //  }
        }
    }

    protected boolean placeLog(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource pRandom, BlockPos pPos, TreeConfiguration treeconfiguration) {
        return placeLog(pLevel, blockSetter, pRandom, pPos, treeconfiguration, treeconfiguration.trunkProvider);
    }

    protected boolean placeLog(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource pRandom, BlockPos pPos, TreeConfiguration treeConfig, BlockStateProvider blockStateProvider) {
        if (validTreePos(pLevel, pPos)) {
            blockSetter.accept(pPos, blockStateProvider.getState(pRandom, pPos));
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean validTreePos(LevelSimulatedReader pLevel, BlockPos pPos) {
        return pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pPos).getY() > pPos.getY() && pLevel.isStateAtPosition(pPos, block -> block.is(NorthstarBlockTags.ARGYRE_REPLACEABLE.tag));
    }

}