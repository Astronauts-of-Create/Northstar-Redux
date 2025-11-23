package com.lightning.northstar.mixin;

import com.lightning.northstar.api.data.datamap.DimensionInfo;
import com.lightning.northstar.content.NorthstarDataMaps;
import com.lightning.northstar.contraption.rocket.RocketHandler;
import com.lightning.northstar.world.dimension.NorthstarDimensions;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(LivingEntity.class)
public class GravityStuffMixin {
    @Unique
    private static final double northstar$CONSTANT = 0.08;

    @Unique
    double PLANET_GRAV = 1;
    @Unique
    private int fall_disabled = 0;

    @Inject(method = "travel", at = @At("TAIL"))
    public void northstar$travel(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (fall_disabled > 0) {
            fall_disabled--;
            entity.fallDistance = 0;
        }
        if (RocketHandler.isInRocket(entity) && entity.getY() > 1500) {
            fall_disabled = 400;
        }
        Vec3 velocity = entity.getDeltaMovement();
        boolean isInOrbit = NorthstarPlanets.isInOrbit(entity.level().dimension());

        DimensionInfo info = entity.level().dimensionTypeRegistration().getData(NorthstarDataMaps.LEVEL_INFO);

        // Used to make IntelliJ happy
        if (info == null) {
            // Just have a default of 1 I guess
            info = DimensionInfo.getDefault();
        };

        PLANET_GRAV = info.gravity();

        if (entity.isFallFlying() || entity.isInFluidType()) {
            PLANET_GRAV = 1;
        }
        if (!entity.isNoGravity() && !entity.isInWater() && !entity.isInLava() && !entity.hasEffect(MobEffects.SLOW_FALLING)) {
            float dust_push = 0;
            if (entity.level().getRainLevel(0) > 0 && entity.level().getRawBrightness(entity.blockPosition(), -1) == 16 && !entity.isSpectator() && (entity.level().dimension() == NorthstarDimensions.MARS_DIM_KEY && !NorthstarOxygen.hasOxygen(entity.level(), entity.getEyePosition()))
                    && entity.level().isInWorldBounds(entity.blockPosition()) && !RocketHandler.isInRocket(entity)) {
                dust_push = 0.005f;
            }
            if (entity instanceof Player ply) {
                if (ply.isCreative()) {
                    dust_push = 0;
                }
            }

            double newGrav = northstar$CONSTANT * PLANET_GRAV;
            float crouchPush = 0;
            if (!isInOrbit) {
                entity.setDeltaMovement(velocity.x() + dust_push, velocity.y() + (northstar$CONSTANT - newGrav), velocity.z() - dust_push);
            } else {
                if (entity.isCrouching()) {
                    crouchPush = 0.05f;
                }
                float vel_y = (float) Mth.clamp(velocity.y(), -0.3, 15);
                entity.setDeltaMovement(velocity.x() + dust_push, vel_y + (northstar$CONSTANT - newGrav) - crouchPush, velocity.z() - dust_push);
            }
        }
        /*if (isInOrbit) {
            if (entity.getY() < 0 && !entity.level().isClientSide) {
                if (entity.level().dimension() == NorthstarDimensions.EARTH_ORBIT_DIM_KEY) {
                    ServerLevel destLevel = entity.level().getServer().getLevel(Level.OVERWORLD);
                    if (entity instanceof ServerPlayer player) {
                        changePlayerDimension(destLevel, player);
                    } else {
                        changeDimensionCustom(destLevel, entity);
                    }
                }
            }
        }*/
    }

    @Inject(method = "calculateFallDamage", at = @At("HEAD"), cancellable = true)
    public void calculateFallDamage(float pFallDistance, float pDamageMultiplier, CallbackInfoReturnable<Integer> info) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!NorthstarPlanets.hasNormalGrav(entity.level().dimensionTypeRegistration())) {
            MobEffectInstance mobeffectinstance = entity.getEffect(MobEffects.JUMP);
            double mult = northstar$getGravMultiplier(entity.level().dimensionTypeRegistration());
            float f = (float) (mobeffectinstance == null ? 0.0F : (float) (mobeffectinstance.getAmplifier() + 1) * mult);
            info.setReturnValue(Mth.ceil(((pFallDistance * mult) - 3.0F - f) * pDamageMultiplier));
        }
    }

    @Unique
    public double northstar$getGravMultiplier(Holder<DimensionType> dimension) {
        DimensionInfo info = dimension.getData(NorthstarDataMaps.LEVEL_INFO);
        return info == null ? 1 : info.gravity();
    }
}
