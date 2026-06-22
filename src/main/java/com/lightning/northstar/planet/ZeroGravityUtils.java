package com.lightning.northstar.planet;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ZeroGravityUtils {

    /**
     * Applies custom knockback in zero gravity dimensions by replacing the vertical velocity with a projectile like
     * velocity following the attacker's facing. Orbital tennis when?
     *
     * @return if no knockback has been applied and vanilla knockback should be applied instead
     */
    public static boolean shouldApplyKnockback(@Nullable Entity attacker, Entity attacked, double strength) {
        if (attacker == null || !attacked.level().northstar$isZeroGravity()) {
            return true;
        }

        // TODO: It would probably be nice if this could still trigger Forge's LivingKnockBackEvent but it wouldn't be possible to pass the Y velocity
        //  It would either require to be injected via a Mixin + Accessor but mods wouldn't know about it (and possibly mess it up)

        if (attacked instanceof LivingEntity living) {
            strength *= 1.0 - living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        }

        if (strength > 0) {
            float inaccuracy = 1f;
            float xRot = attacker.getXRot();
            float yRot = attacker.getYRot();
            RandomSource random = attacker.level().getRandom();
            Vec3 knockback = new Vec3(
                    -Mth.sin(yRot * Mth.DEG_TO_RAD) * Mth.cos(xRot * Mth.DEG_TO_RAD),
                    -Mth.sin(xRot * Mth.DEG_TO_RAD),
                    Mth.cos(yRot * Mth.DEG_TO_RAD) * Mth.cos(xRot * Mth.DEG_TO_RAD)
            )
                    .normalize()
                    .add(
                            random.triangle(0.0, 0.0172275 * inaccuracy),
                            random.triangle(0.0, 0.0172275 * inaccuracy),
                            random.triangle(0.0, 0.0172275 * inaccuracy)
                    )
                    .scale(strength);

            attacked.hasImpulse = true;
            Vec3 entityVelocity = attacked.getDeltaMovement();
            attacked.setDeltaMovement(entityVelocity.x / 2.0 + knockback.x, entityVelocity.y / 2.0 + knockback.y, entityVelocity.z / 2.0 + knockback.z);
        }
        return false;
    }

}
