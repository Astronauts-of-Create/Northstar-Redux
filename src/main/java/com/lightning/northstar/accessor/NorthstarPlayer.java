package com.lightning.northstar.accessor;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface NorthstarPlayer {

    @Nullable
    default Entity northstar$getRelativeEntity() {
        return null;
    }

    default void northstar$setRelativeEntity(@Nullable Entity entity, int ticks) {
    }

}
