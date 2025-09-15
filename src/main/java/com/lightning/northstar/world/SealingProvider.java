package com.lightning.northstar.world;

import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public interface SealingProvider {

    boolean isSealed(Vec3 pos);

    boolean isSealed(Vec3i pos);

}
