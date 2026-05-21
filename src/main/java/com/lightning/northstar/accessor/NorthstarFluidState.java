package com.lightning.northstar.accessor;

import com.lightning.northstar.world.sealer.ProgressiveBlockUpdater;
import com.lightning.northstar.world.sealer.SealingMode;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface NorthstarFluidState {

    /**
     * @deprecated see {@link ProgressiveBlockUpdater}
     */
    @Deprecated
    default void northstar$onSealUpdated(Level level, BlockPos pos, SealingMode mode) {
        throw new MissingMixinException();
    }

}
