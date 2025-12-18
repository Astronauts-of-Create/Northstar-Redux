package com.lightning.northstar.accessor;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface NorthstarPlayer {

    @Nullable
    default Entity northstar$getRelativeEntity() {
        return null;
    }

    @Nullable
    default void northstar$setRelativeEntity(Entity entity, int ticks) {

    }
}
