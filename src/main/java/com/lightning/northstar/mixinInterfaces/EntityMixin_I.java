package com.lightning.northstar.mixinInterfaces;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;

public interface EntityMixin_I {
    public void pinToVehicle(Entity vehicle);

    public void out_addPassenger(Entity p_20081_);

    public Map<UUID, Vec3> getRocketPassengerOffsets();

    public void setRocketPassengerOffsets(Map<UUID, Vec3> set);
}
