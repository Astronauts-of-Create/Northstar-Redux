package com.lightning.northstar.world.sealer;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.particle.NorthstarParticles;
import com.lightning.northstar.util.MutableAABB;
import com.lightning.northstar.util.NorthstarLang;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
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
import net.minecraft.world.phys.Vec3;
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
    private final LongList updatedBlocks = new LongArrayList();
    private boolean hasLeak;
    private int extraVolume;

    private int checkCooldown;

    private SealerDebugVisualizer visualizer = SealerDebugVisualizer.NOOP;

    public ProgressiveBlockSealer(SealingMode mode) {
        this.mode = mode;

        visited.defaultReturnValue(START_MARKER);
    }

    /** @return if the sealing process is complete and the seal has been updated */
    public boolean processSeal(Level level, BlockPos origin, @Nullable Direction originDirection, int maximumSealed) {
        if (isSealInProgress()) {
            return updateSeal(level, maximumSealed);
        }

        if (--checkCooldown <= 0) {
            checkCooldown = NorthstarConfigs.server().sealerCheckDelay.get();
            return !beginSeal(level, origin, originDirection);
        }

        return false;
    }

    /** @return if the sealing process has started */
    public boolean beginSeal(Level level, BlockPos origin, @Nullable Direction originDirection) {
        visited.clear();
        queue.clear();
        bounds.neg();
        updatedBlocks.clear();

        extraCheckedPerTick = 0;
        extraCheckedVolume = 0;

        if (originDirection != null) {
            tempPos1.setWithOffset(origin, originDirection);
            if (isFaceOccluded(level, tempPos1, originDirection.getOpposite(), false, mode)) {
                bounds.zero();
                onSealComplete(0, START_MARKER);
                return false;
            }
        } else {
            tempPos1.set(origin);
        }

        if (level.isClientSide() && NorthstarConfigs.client().debugSealerBounds.get() != visualizer instanceof SealerDebugVisualizer.Client) {
            visualizer = NorthstarConfigs.client().debugSealerBounds.get() ? new SealerDebugVisualizer.Client() : SealerDebugVisualizer.NOOP;
        }

        visited.put(tempPos1.asLong(), START_MARKER);
        queue.enqueue(tempPos1.asLong());

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
                if (isAirOccluded(level, tempPos1, tempPos2, direction)) {
                    continue;
                }

                visited.put(packed, parent);
                onBlockAdded(level, tempPos2);

                if (!sealedBlocks.contains(packed))
                    updatedBlocks.add(packed);

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

        profiler.incrementCounter("blocks", checked);

        if (!queue.isEmpty() && (visited.size() + queue.size()) < maximumSealed) {
            profiler.pop();
            return false;
        }

        onSealComplete(maximumSealed, lastChecked);

        profiler.pop();
        return true;
    }

    protected void onSealComplete(int maximumSealed, long lastChecked) {
        hasLeak = !queue.isEmpty() || visited.size() > maximumSealed;
        leakPath.clear();
        if (hasLeak) {
            while (lastChecked != START_MARKER) {
                leakPath.add(lastChecked);
                lastChecked = visited.get(lastChecked);
            }

            updatedBlocks.clear();
            updatedBlocks.addAll(sealedBlocks);

            sealedBlocks.clear();
            sealedBounds.neg();
        } else {
            leakPath.trim();

            LongIterator iterator = sealedBlocks.longIterator();
            while (iterator.hasNext()) {
                long value = iterator.nextLong();
                if (!visited.containsKey(value))
                    updatedBlocks.add(value);
            }

            sealedBlocks.clear();
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
        renderLeakPath(level, null);
    }

    public void renderLeakPath(Level level, @Nullable AbstractContraptionEntity contraption) {
        if (!hasLeak || leakPath.isEmpty() || !level.isClientSide)
            return;

        MutableBlockPos pos = tempPos1;
        RandomSource random = level.random;

        double px = 0, py = 0, pz = 0;
        double cx, cy, cz;

        for (int i = 0, j = leakPath.size(); i < j; i++) {
            pos.set(leakPath.getLong(i));

            cx = pos.getX() + 0.5;
            cy = pos.getY() + 0.5;
            cz = pos.getZ() + 0.5;

            if (contraption != null) {
                Vec3 global = contraption.toGlobalVector(new Vec3(cx, cy, cz), 1);
                cx = global.x;
                cy = global.y;
                cz = global.z;
            }

            if (i != 0) {
                level.addParticle(NorthstarParticles.LEAK.get(),
                        cx - 0.2 + random.nextFloat() * 0.4,
                        cy - 0.2 + random.nextFloat() * 0.4,
                        cz - 0.2 + random.nextFloat() * 0.4,
                        px - cx,
                        py - cy,
                        pz - cz);
            }

            px = cx;
            py = cy;
            pz = cz;
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
                    .style(ChatFormatting.BLUE)
                    .text(" ")
                    .add(NorthstarLang.blocks(maximumSealed + extraVolume)
                            .style(ChatFormatting.GRAY))
                    .forGoggles(tooltip, 1);
        } else {
            NorthstarLang.translate("gui.goggles.sealer.blocks_filled")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            CreateLang.number(sealedBlocks.size())
                    .style(ChatFormatting.BLUE)
                    .text(ChatFormatting.GRAY, " / ")
                    .add(CreateLang.number(maximumSealed + extraVolume)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
        }

        if (isPlayerSneaking && extraVolume != 0) {
            CreateLang.number(maximumSealed)
                    .style(ChatFormatting.BLUE)
                    .add(NorthstarLang.translate("gui.goggles.sealer.capacity_speed")
                            .style(ChatFormatting.GRAY))
                    .forGoggles(tooltip, 2);
            CreateLang.number(extraVolume)
                    .style(ChatFormatting.BLUE)
                    .add(NorthstarLang.translate("gui.goggles.sealer.capacity_extra")
                            .style(ChatFormatting.GRAY))
                    .forGoggles(tooltip, 2);
        }
    }

    public void addCooldownTooltip(List<Component> tooltip, int maximumSealed) {
        addCooldownTooltip(tooltip, checkCooldown, maximumSealed);
    }

    public void addCooldownTooltip(List<Component> tooltip, int cooldown, int maximumSealed) {
        if (cooldown > 0) {
            NorthstarLang.translate("gui.goggles.sealer.cooldown")
                    .style(ChatFormatting.GRAY)
                    .add(CreateLang.number(Math.round(cooldown / 20f))
                            .style(ChatFormatting.AQUA))
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

    public LongList getUpdatedBlocks() {
        return updatedBlocks;
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

    public int getCheckCooldown() {
        return checkCooldown;
    }
}
