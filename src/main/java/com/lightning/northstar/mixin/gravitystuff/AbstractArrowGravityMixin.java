package com.lightning.northstar.mixin.gravitystuff;

import com.lightning.northstar.world.dimension.NorthstarDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public class AbstractArrowGravityMixin {
    @Unique
    private static final double CONSTANT = 0.08;
    private static final double EARTH_GRAV = 1;
    private static final double VENUS_GRAV = 0.92;
    private static final double MOON_GRAV = 0.5;
    private static final double MARS_GRAV = 0.5;
    private static final double MERCURY_GRAV = 0.5;
    private static final double ORBIT_GRAV = 0.5;
    private static final double AEGAEON_GRAV = 0.02;
    double PLANET_GRAV = 1;


    @Inject(method = "tick", at = @At("TAIL"))
    public void northstar$tick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        Vec3 velocity = entity.getDeltaMovement();
        if (entity.level().dimension() == NorthstarDimensions.MARS_DIM_KEY) {
            PLANET_GRAV = MARS_GRAV;
        } else if (entity.level().dimension() == NorthstarDimensions.MOON_DIM_KEY) {
            PLANET_GRAV = MOON_GRAV;
        } else if (entity.level().dimension() == NorthstarDimensions.VENUS_DIM_KEY) {
            PLANET_GRAV = VENUS_GRAV;
        } else if (entity.level().dimension() == NorthstarDimensions.AEGAEON_DIM_KEY) {
            PLANET_GRAV = AEGAEON_GRAV;
        } else if (entity.level().dimension() == NorthstarDimensions.MERCURY_DIM_KEY) {
            PLANET_GRAV = MERCURY_GRAV;
        } else if (entity.level().dimension() == NorthstarDimensions.EARTH_ORBIT_DIM_KEY) {
            PLANET_GRAV = ORBIT_GRAV;
        } else {
            PLANET_GRAV = EARTH_GRAV;
        }
        if (!entity.isNoGravity()) {
            double newGrav = CONSTANT * PLANET_GRAV;
            entity.setDeltaMovement(velocity.x(), velocity.y() + CONSTANT - newGrav, velocity.z());
        }
    }

}
