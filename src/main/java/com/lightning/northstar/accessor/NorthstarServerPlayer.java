package com.lightning.northstar.accessor;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface NorthstarServerPlayer {

    @Nullable
    default Entity northstar$getRelativeEntity() {
        return null;
    }

    default void northstar$setPositionRelativeTo(Entity other) {
    }

}
