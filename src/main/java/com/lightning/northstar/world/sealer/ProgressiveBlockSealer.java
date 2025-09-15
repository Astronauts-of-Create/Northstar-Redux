package com.lightning.northstar.world.sealer;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.util.MutableAABB;
import com.lightning.northstar.util.NorthstarLang;
import com.simibubi.create.foundation.utility.CreateLang;
import it.unimi.dsi.fastutil.longs.*;
import net.createmod.catnip.data.Iterate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ProgressiveBlockSealer {

    private final MutableBlockPos tempPos1 = new MutableBlockPos();
    private final MutableBlockPos tempPos2 = new MutableBlockPos();

    private final Long2LongMap visited = new Long2LongOpenHashMap();
    private final LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
    private final MutableAABB bounds = new MutableAABB();

    private final LongSet sealedBlocks = new LongOpenHashSet();
    private final MutableAABB sealedBounds = new MutableAABB();
    private boolean hasLeak;

    private SealerDebugVisualizer visualizer = SealerDebugVisualizer.NOOP;

    public boolean beginSeal(Level level, BlockPos origin, Direction originDirection) {
        if (originDirection != null) {
            tempPos1.setWithOffset(origin, originDirection);
            if (isFaceOccluded(level, tempPos1, originDirection.getOpposite(), false)) {
                sealedBounds.zero();
                sealedBlocks.clear();
                visualizer.complete();
                return false;
            }
        } else {
            tempPos1.set(origin);
        }

        if (level.isClientSide() && NorthstarConfigs.client().debugSealerBounds.get() != visualizer instanceof SealerDebugVisualizer.Client) {
            visualizer = NorthstarConfigs.client().debugSealerBounds.get() ? new SealerDebugVisualizer.Client() : SealerDebugVisualizer.NOOP;
        }

        visited.clear();
        queue.clear();
        bounds.neg();

        visited.put(tempPos1.asLong(), 0);
        queue.enqueue(tempPos1.asLong());

        bounds.union(origin);
        bounds.union(tempPos1);
        return true;
    }

    public boolean updateSeal(Level level, int maximumSealed) {
        return updateSeal(level, maximumSealed, NorthstarConfigs.server().sealerMaxBlocksPerTick.get());
    }

    /**
     * @return true if the seal is complete or false if it is still in progress
     */
    public boolean updateSeal(Level level, int maximumSealed, int maximumChecked) {
        if (queue.isEmpty()) {
            return true; // nothing to do
        }

        ProfilerFiller profiler = level.getProfiler();
        profiler.push("northstar:seal_blocks");

        int checked = 0;
        while (!queue.isEmpty() && checked++ < maximumChecked && visited.size() <= maximumSealed) {
            tempPos1.set(queue.dequeueLong());

            for (Direction direction : Iterate.directions) {
                tempPos2.setWithOffset(tempPos1, direction);

                if (isAirOccluded(level, tempPos1, tempPos2, direction)) {
                    continue;
                }

                long packed = tempPos2.asLong();
                if (visited.put(packed, 1) == 0) {
                    bounds.union(tempPos2);
                    queue.enqueue(packed);

                    visualizer.addConnection(tempPos1.asLong(), packed);
                }
            }
        }

        if (!queue.isEmpty() && visited.size() < maximumSealed) {
            profiler.pop();
            return false;
        }

        hasLeak = visited.size() > maximumSealed;
        sealedBlocks.clear();
        if (hasLeak) {
            sealedBounds.neg();
        } else {
            sealedBlocks.addAll(visited.keySet());
            sealedBounds.set(bounds);
        }

        visualizer.complete();

        queue.clear();
        visited.clear();
        bounds.neg();

        profiler.pop();
        return true;
    }

    protected boolean isAirOccluded(BlockGetter level, BlockPos from, BlockPos to, Direction direction) {
        return isFaceOccluded(level, from, direction, true) || isFaceOccluded(level, to, direction.getOpposite(), false);
    }

    protected static boolean isFaceOccluded(BlockGetter level, BlockPos pos, Direction direction, boolean source) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof SealableBlock sealable)
            return sealable.isFaceSealed(level, pos, direction, source);
        if (source && NorthstarBlockTags.BLOCKS_AIR.matches(state))
            return true;
        return Block.isFaceFull(state.getCollisionShape(level, pos), direction) && !NorthstarBlockTags.AIR_PASSES_THROUGH.matches(state);
    }

    public void addToGoggleTooltip(List<Component> tooltip, int maximumSealed) {
        if (hasLeak()) {
            NorthstarLang.translate("gui.goggles.sealer.area_too_big")
                    .style(ChatFormatting.DARK_RED)
                    .forGoggles(tooltip);
        } else {
            NorthstarLang.translate("gui.goggles.sealer.blocks_filled")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            CreateLang.number(sealedBlocks.size())
                    .style(ChatFormatting.AQUA)
                    .text(ChatFormatting.GRAY, " / ")
                    .add(CreateLang.number(maximumSealed)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
        }
    }

    // This probably doesn't belong here, but I couldn't find a better place to put it
    public void addCooldownTooltip(List<Component> tooltip, int cooldown, int maximumSealed) {
        if (cooldown > 0) {
            NorthstarLang.translate("gui.goggles.sealer.cooldown")
                    .style(ChatFormatting.GRAY)
                    .add(CreateLang.number(Math.round(cooldown / 20f)))
                    .forGoggles(tooltip);
        } else {
            NorthstarLang.translate("gui.goggles.sealer.sealing")
                    .style(ChatFormatting.GRAY)
                    .add(CreateLang.number(100.0 * visited.size() / maximumSealed)
                            .text("%")
                            .style(ChatFormatting.AQUA))
                    .forGoggles(tooltip);
        }
    }

    public boolean isSealInProgress() {
        return !queue.isEmpty();
    }

    public MutableAABB getSealedBounds() {
        return sealedBounds;
    }

    public LongSet getSealedBlocks() {
        return sealedBlocks;
    }

    public boolean hasLeak() {
        return hasLeak;
    }

    public SealerDebugVisualizer getVisualizer() {
        return visualizer;
    }

    public int getSealedBlockCount() {
        return sealedBlocks.size();
    }

}
