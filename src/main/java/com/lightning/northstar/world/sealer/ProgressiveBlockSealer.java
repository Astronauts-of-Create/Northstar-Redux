package com.lightning.northstar.world.sealer;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.particle.NorthstarParticles;
import com.lightning.northstar.util.MutableAABB;
import com.lightning.northstar.util.NorthstarLang;
import com.simibubi.create.foundation.utility.CreateLang;
import it.unimi.dsi.fastutil.longs.*;
import net.createmod.catnip.data.Iterate;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ProgressiveBlockSealer {

    private static final long START_MARKER = Long.MIN_VALUE;

    private final SealingMode mode;

    private final MutableBlockPos tempPos1 = new MutableBlockPos();
    private final MutableBlockPos tempPos2 = new MutableBlockPos();

    private final Long2LongMap visited = new Long2LongOpenHashMap();
    private final LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
    private final MutableAABB bounds = new MutableAABB();
    private int extraCheckedVolume;
    private int extraCheckedPerTick;

    private final LongArrayList leakPath = new LongArrayList();
    private final LongSet sealedBlocks = new LongOpenHashSet();
    private final MutableAABB sealedBounds = new MutableAABB();
    private boolean hasLeak;
    private int extraVolume;


    private SealerDebugVisualizer visualizer = SealerDebugVisualizer.NOOP;

    public ProgressiveBlockSealer(SealingMode mode) {
        this.mode = mode;

        visited.defaultReturnValue(START_MARKER);
    }

    public boolean beginSeal(Level level, BlockPos origin, @Nullable Direction originDirection) {
        if (originDirection != null) {
            tempPos1.setWithOffset(origin, originDirection);
            if (isFaceOccluded(level, tempPos1, originDirection.getOpposite(), false, mode)) {
                sealedBounds.zero();
                sealedBlocks.clear();
                hasLeak = false;
                extraVolume = 0;
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

        visited.put(tempPos1.asLong(), START_MARKER);
        queue.enqueue(tempPos1.asLong());

        extraCheckedPerTick = 0;
        extraCheckedVolume = 0;

        bounds.union(origin);
        bounds.union(tempPos1);
        return true;
    }

    public boolean updateSeal(Level level, int maximumSealed) {
        return updateSeal(level, maximumSealed, NorthstarConfigs.server().sealerBaseCheckBlocksPerTick.get());
    }

    /**
     * @return true if the seal is complete or false if it is still in progress
     */
    public boolean updateSeal(Level level, int maximumSealed, int maximumChecked) {
        if (queue.isEmpty()) {
            return true; // nothing to do
        }

        maximumSealed += extraCheckedVolume;
        maximumChecked = Math.min(maximumChecked + extraCheckedPerTick, NorthstarConfigs.server().sealerMaxCheckedBlocksPerTick.get());

        ProfilerFiller profiler = level.getProfiler();
        profiler.push("northstar:seal_blocks");

        long lastChecked = 0;

        int checked = 0;
        while (!queue.isEmpty() && checked++ < maximumChecked && visited.size() <= maximumSealed) {
            long parent = queue.dequeueLong();
            tempPos1.set(parent);

            for (Direction direction : Iterate.directions) {
                tempPos2.setWithOffset(tempPos1, direction);

                long packed = tempPos2.asLong();
                if (visited.containsKey(packed))
                    continue;
                if (isAirOccluded(level, tempPos1, tempPos2, direction))
                    continue;

                visited.put(packed, parent);
                onBlockAdded(level, tempPos2);

                BlockState state = level.getBlockState(tempPos2);
                if (state.getBlock() instanceof SealerExtensionSource source) {
                    int sourceVolume = source.getMaximumSealedBlocks(level, tempPos2);
                    int sourceChecked = source.getMaximumCheckedPerTick(level, tempPos2);

                    extraCheckedVolume += sourceVolume;
                    extraCheckedPerTick += sourceChecked;
                    maximumSealed += sourceVolume;
                    maximumChecked = Math.min(maximumChecked + sourceChecked, NorthstarConfigs.server().sealerMaxCheckedBlocksPerTick.get());
                }

                bounds.union(tempPos2);
                queue.enqueue(packed);
                lastChecked = packed;

                visualizer.addConnection(parent, packed);
            }
        }

        if (!queue.isEmpty() && visited.size() < maximumSealed) {
            profiler.pop();
            return false;
        }

        onSealComplete(maximumSealed, lastChecked);

        profiler.pop();
        return true;
    }

    protected void onSealComplete(int maximumSealed, long lastChecked) {
        hasLeak = visited.size() > maximumSealed;
        leakPath.clear();
        sealedBlocks.clear();
        if (hasLeak) {
            while (lastChecked != START_MARKER) {
                leakPath.add(lastChecked);
                lastChecked = visited.get(lastChecked);
            }

            sealedBounds.neg();
        } else {
            leakPath.trim();
            sealedBlocks.addAll(visited.keySet());
            sealedBounds.set(bounds);
        }

        extraVolume = extraCheckedVolume;

        visualizer.complete();

        queue.clear();
        visited.clear();
        bounds.neg();
    }

    protected void onBlockAdded(BlockGetter level, BlockPos pos) {
    }

    protected boolean isAirOccluded(BlockGetter level, BlockPos from, BlockPos to, Direction direction) {
        return isAirOccluded(level, from, to, direction, mode);
    }

    public static boolean isAirOccluded(BlockGetter level, BlockPos from, BlockPos to, Direction direction, SealingMode mode) {
        return isFaceOccluded(level, from, direction, true, mode) || isFaceOccluded(level, to, direction.getOpposite(), false, mode);
    }

    public static boolean isFaceOccluded(BlockGetter level, BlockPos pos, Direction direction, boolean source, SealingMode mode) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof SealableBlock sealable)
            return sealable.northstar$isFaceSealed(level, pos, state, direction, source, mode);
        if (source && NorthstarBlockTags.BLOCKS_AIR.matches(state))
            return true;
        return Block.isFaceFull(state.getShape(level, pos), direction) && !NorthstarBlockTags.AIR_PASSES_THROUGH.matches(state);
    }

    public void renderLeakPath(Level level) {
        if (!hasLeak || leakPath.isEmpty() || !level.isClientSide)
            return;

        MutableBlockPos prev = tempPos1;
        MutableBlockPos pos = tempPos2;
        RandomSource random = level.random;

        prev.set(leakPath.getLong(0));

        for (int i = 1, j = leakPath.size(); i < j; i++) {
            pos.set(leakPath.getLong(i));

            level.addParticle(NorthstarParticles.LEAK.get(),
                    pos.getX() + random.nextFloat() * 0.4 + 0.3,
                    pos.getY() + random.nextFloat() * 0.4 + 0.3,
                    pos.getZ() + random.nextFloat() * 0.4 + 0.3,
                    prev.getX() - pos.getX(),
                    prev.getY() - pos.getY(),
                    prev.getZ() - pos.getZ());

            prev.set(pos);
        }
    }

    public void addToGoggleTooltip(List<Component> tooltip, int maximumSealed, boolean isPlayerSneaking) {
        if (hasLeak()) {
            NorthstarLang.translate("gui.goggles.sealer.area_too_big")
                    .style(ChatFormatting.DARK_RED)
                    .forGoggles(tooltip);
            NorthstarLang.translate("gui.goggles.sealer.max_sealed")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            CreateLang.number(maximumSealed + extraVolume)
                    .style(ChatFormatting.AQUA)
                    .text(ChatFormatting.GRAY, " blocks")
                    .forGoggles(tooltip, 1);
        } else {
            NorthstarLang.translate("gui.goggles.sealer.blocks_filled")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            CreateLang.number(sealedBlocks.size())
                    .style(ChatFormatting.AQUA)
                    .text(ChatFormatting.GRAY, " / ")
                    .add(CreateLang.number(maximumSealed + extraVolume)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
        }

        if (isPlayerSneaking && extraVolume != 0) {
            CreateLang.number(maximumSealed)
                    .style(ChatFormatting.AQUA)
                    .add(NorthstarLang.translate("gui.goggles.sealer.capacity_speed")
                            .style(ChatFormatting.GRAY))
                    .forGoggles(tooltip, 2);
            CreateLang.number(extraVolume)
                    .style(ChatFormatting.AQUA)
                    .add(NorthstarLang.translate("gui.goggles.sealer.capacity_extra")
                            .style(ChatFormatting.GRAY))
                    .forGoggles(tooltip, 2);
        }
    }

    public void addCooldownTooltip(List<Component> tooltip, int cooldown, int maximumSealed) {
        if (cooldown > 0) {
            NorthstarLang.translate("gui.goggles.sealer.cooldown")
                    .style(ChatFormatting.GRAY)
                    .add(CreateLang.number(Math.round(cooldown / 20f)))
                    .forGoggles(tooltip);
        } else {
            NorthstarLang.translate("gui.goggles.sealer.sealing")
                    .style(ChatFormatting.GRAY)
                    .add(CreateLang.number(100.0 * visited.size() / (maximumSealed + extraCheckedVolume))
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
