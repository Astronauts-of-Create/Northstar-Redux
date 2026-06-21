package com.lightning.northstar.world.gen.feature;

import com.lightning.northstar.world.gen.feature.configuration.GlowstoneBranchConfig;
import com.mojang.serialization.Codec;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.GlowstoneFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/** Similar to vanilla's {@link GlowstoneFeature} */
public class GlowstoneBranchFeature extends Feature<GlowstoneBranchConfig> {

    public GlowstoneBranchFeature(Codec<GlowstoneBranchConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<GlowstoneBranchConfig> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        GlowstoneBranchConfig config = context.config();
        BlockStateProvider block = config.glowProvider();
        if (!level.isEmptyBlock(origin)) {
            return false;
        }
        BlockState blockState = level.getBlockState(config.downwards() ? origin.below() : origin.above());
        if (blockState.is(Blocks.AIR)) {
            return false;
        }
        level.setBlock(origin, block.getState(random, origin), Block.UPDATE_CLIENTS);

        MutableBlockPos pos = new MutableBlockPos();
        MutableBlockPos neighbor = new MutableBlockPos();

        for (int i = 0; i < config.attempts(); i++) {
            pos.setWithOffset(origin,
                    random.nextInt(8) - random.nextInt(8),
                    config.downwards() ? random.nextInt(12) : -random.nextInt(12),
                    random.nextInt(8) - random.nextInt(8));

            if (!level.getBlockState(pos).isAir())
                continue;

            int neighbors = 0;

            for (Direction direction : Iterate.directions) {
                neighbor.setWithOffset(pos, direction);

                if (level.getBlockState(neighbor).is(block.getState(random, neighbor).getBlock())) {
                    neighbors++;

                    if (neighbors > 1) {
                        break;
                    }
                }
            }

            if (neighbors == 1) {
                level.setBlock(pos, block.getState(random, pos), Block.UPDATE_CLIENTS);
            }
        }

        return true;
    }

}
