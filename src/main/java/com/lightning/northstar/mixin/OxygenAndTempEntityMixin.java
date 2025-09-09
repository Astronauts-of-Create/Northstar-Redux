package com.lightning.northstar.mixin;

import com.lightning.northstar.advancements.NorthstarAdvancements;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarEntityTags;
import com.lightning.northstar.world.NorthstarTemperature;
import com.lightning.northstar.world.dimension.NorthstarDimensions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class OxygenAndTempEntityMixin {

    int tempHurtBuffer = 70;

    @Inject(method = "tick", at = @At("TAIL"))
    public void northstar$tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof ZombifiedPiglin || entity.level().isClientSide())
            return;

        ResourceKey<Level> dim = entity.level().dimension();
        float temp = NorthstarTemperature.getTemperatureAt(entity.level(), entity.position());
        boolean hasInsulation = NorthstarTemperature.hasInsulation(entity);
        boolean hasHeatProtection = NorthstarTemperature.hasHeatProtection(entity);
        boolean creativeCheck = false;
        if (entity instanceof ServerPlayer svp) {
            creativeCheck = svp.isCreative();
        }

        if (temp > -32 && temp < 300) {
            tempHurtBuffer = 40;
        }

        if (tempHurtBuffer > 0) {
            tempHurtBuffer--;
        }

        if (temp < -32 && !entity.isSpectator() && !hasInsulation && tempHurtBuffer <= 0 && !creativeCheck && !NorthstarEntityTags.CAN_SURVIVE_COLD.matches(entity)) {
            boolean flag = entity.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES);
            int j = flag ? 7 : 2;
            entity.hurt(entity.level().damageSources().freeze(), (float) j);
            entity.setTicksFrozen(entity.getTicksRequiredToFreeze());
        }
        if (temp > 300 && !entity.isOnFire() && !entity.fireImmune() && !hasHeatProtection && tempHurtBuffer <= 0) {
            entity.setSecondsOnFire(5);
        }
        if (entity.level() instanceof ServerLevel) {
            if (dim == NorthstarDimensions.EARTH_ORBIT_DIM_KEY && entity.getY() < -10) {
                ServerLevel newLevel = entity.level().getServer().getLevel(Level.END);
//                if(entity.level instanceof ServerLevel)
//                entity = (LivingEntity) changeDimensionCustom(newLevel);
            } else if (entity instanceof Player plyer) {
                if (dim == NorthstarDimensions.MOON_DIM_KEY && !NorthstarAdvancements.ONE_SMALL_STEP.isAlreadyAwardedTo(plyer)) {
                    if (plyer.level().getBlockState(plyer.blockPosition().below()).is(NorthstarBlockTags.MOON_BLOCKS.tag)) {
                        NorthstarAdvancements.ONE_SMALL_STEP.awardTo(plyer);
                    }

                } else if (dim == NorthstarDimensions.MARS_DIM_KEY && !NorthstarAdvancements.ONE_GIANT_LEAP.isAlreadyAwardedTo(plyer)) {
                    if (plyer.level().getBlockState(plyer.blockPosition().below()).is(NorthstarBlockTags.MARS_BLOCKS.tag)) {
                        NorthstarAdvancements.ONE_GIANT_LEAP.awardTo(plyer);
                    }
                }
            }
        }
        if (getFluidAtPos(entity, entity.level()) == NorthstarFluids.SULFURIC_ACID.get() || getFluidAtPos(entity, entity.level()) == NorthstarFluids.SULFURIC_ACID.getSource().getSource()) {
            sulfurBurn(entity, entity.getRandom());
        }

        // Test code to figure out how changing dimensions works
//        if(entity instanceof ServerPlayer && entity.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.FLINT_AND_STEEL && !entity.level.isClientSide && entity.level.dimension() == Level.OVERWORLD) {
//            ResourceKey<Level> dest = NorthstarDimensions.MOON_DIM_KEY;
//            ServerLevel destLevel = entity.getLevel().getServer().getLevel(dest);
//            RocketHandler.changePlayerDimension(destLevel, (ServerPlayer) entity, new PortalForcer(destLevel));
//        }
    }

    public void sulfurBurn(Entity entity, RandomSource rando) {
        if (entity.hurt(entity.level().damageSources().lava(), 6.0F)) {
            entity.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + rando.nextFloat() * 0.4F);
        }
    }

    public Fluid getFluidAtPos(Entity entity, Level level) {
        float height = level.getBlockState(entity.blockPosition()).getFluidState().getType().getHeight(level.getFluidState(entity.blockPosition()), level, entity.blockPosition());
        if (height + entity.blockPosition().getY() > entity.position().y) {
            return level.getBlockState(entity.blockPosition()).getFluidState().getType();
        } else {
            return null;
        }
    }

}
