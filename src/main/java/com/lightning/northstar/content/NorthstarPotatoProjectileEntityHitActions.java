package com.lightning.northstar.content;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;

import java.util.function.Predicate;

public class NorthstarPotatoProjectileEntityHitActions {

    public static class Freezing implements Predicate<EntityHitResult> {
        @Override
        public boolean test(EntityHitResult ray) {
            Entity entity = ray.getEntity();
            entity.setTicksFrozen(Math.max(entity.getTicksFrozen(), entity.getTicksRequiredToFreeze()) + 200);
            return false;
        }
    }

}
