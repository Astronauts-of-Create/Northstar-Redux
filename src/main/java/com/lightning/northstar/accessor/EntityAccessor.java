package com.lightning.northstar.accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;

public interface EntityAccessor {

    default void pinToVehicle(Entity vehicle) {
        throw new RuntimeException("This should be implemented by a mixin!");
    }

    default Map<UUID, Vec3> getRocketPassengerOffsets() {
        throw new RuntimeException("This should be implemented by a mixin!");
    }

    default void setRocketPassengerOffsets(Map<UUID, Vec3> set) {
        throw new RuntimeException("This should be implemented by a mixin!");
    }

}
