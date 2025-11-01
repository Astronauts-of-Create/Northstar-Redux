package com.lightning.northstar.world.sealer;

import com.lightning.northstar.accessor.NorthstarFluidState;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/**
 * General idea is to have blocks only update when the oxygen/temperature is
 * updated. This would eliminate the following problems:<br>
 * - blocks that don't randomly tick can still be affected without neighbor updates.<br>
 * - blocks that randomly tick avoid wasting time to check temperature/oxygen statuses.<br>
 * - blocks don't get affected while the sealer is doing the initial seal after the
 * chunks have been freshly loaded.<br>
 * <p>
 * This class could probably use another name and could probably be part of
 * block sealers because right now it makes a bit of spaghetti everywhere
 * when this is somewhat the sealer's responsibility.<br>
 * Instead of having a generic SealReactiveBlock it could be for oxygen or
 * temperature directly to avoid extra lookups from the blocks themselves.
 */
public class ProgressiveBlockUpdater {

    private final SealingMode mode;
    private final LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    public ProgressiveBlockUpdater(SealingMode mode) {
        this.mode = mode;
    }

    public void queueUpdates(LongCollection positions) {
        positions.forEach(queue::enqueue);
    }

    public void processUpdates(Level level) {
        LongArrayFIFOQueue queue = this.queue;
        BlockPos.MutableBlockPos pos = this.pos;

        for (int i = 0, j = Math.max(4096, queue.size() / 32); i < j && !queue.isEmpty(); i++) {
            BlockState blockstate = level.getBlockState(pos.set(queue.dequeueLong()));
            if (blockstate.getBlock() instanceof SealReactiveBlock block)
                block.northstar$onSealUpdated(level, pos, blockstate, mode);

            FluidState fluidstate = level.getFluidState(pos);
            if (!fluidstate.isEmpty())
                ((NorthstarFluidState) (Object) fluidstate).northstar$onSealUpdated(level, pos, mode);
        }
    }

    public SealingMode getMode() {
        return mode;
    }

    public LongArrayFIFOQueue getQueue() {
        return queue;
    }

}
