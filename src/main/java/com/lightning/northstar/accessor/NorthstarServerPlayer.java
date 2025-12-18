package com.lightning.northstar.accessor;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.Entity;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface NorthstarServerPlayer {

    default void northstar$setPositionRelativeTo(Entity other) {
    }

}
