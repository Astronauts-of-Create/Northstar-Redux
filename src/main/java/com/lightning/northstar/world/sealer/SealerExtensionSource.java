package com.lightning.northstar.world.sealer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface SealerExtensionSource {

    int getMaximumSealedBlocks(Level level, BlockPos pos);

    default int getMaximumCheckedPerTick(Level level, BlockPos pos) {
        return getMaximumSealedBlocks(level, pos) / 100;
    }

}
