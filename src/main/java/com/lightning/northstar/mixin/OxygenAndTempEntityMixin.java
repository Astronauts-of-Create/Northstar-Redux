package com.lightning.northstar.mixin;

import com.lightning.northstar.advancements.NorthstarAdvancements;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.world.dimension.NorthstarDimensions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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

    @Inject(method = "tick", at = @At("TAIL"))
    public void northstar$tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof ZombifiedPiglin || entity.level().isClientSide())
            return;

        ResourceKey<Level> dim = entity.level().dimension();

        if (entity.level() instanceof ServerLevel) {
            if (entity instanceof Player player) {
                if (dim == NorthstarDimensions.MOON_DIM_KEY && !NorthstarAdvancements.ONE_SMALL_STEP.isAlreadyAwardedTo(player)) {
                    if (player.level().getBlockState(player.blockPosition().below()).is(NorthstarBlockTags.MOON_BLOCKS.tag)) {
                        NorthstarAdvancements.ONE_SMALL_STEP.awardTo(player);
                    }

                } else if (dim == NorthstarDimensions.MARS_DIM_KEY && !NorthstarAdvancements.ONE_GIANT_LEAP.isAlreadyAwardedTo(player)) {
                    if (player.level().getBlockState(player.blockPosition().below()).is(NorthstarBlockTags.MARS_BLOCKS.tag)) {
                        NorthstarAdvancements.ONE_GIANT_LEAP.awardTo(player);
                    }
                }
            }
        }
        if (getFluidAtPos(entity, entity.level()) == NorthstarFluids.SULFURIC_ACID.get() || getFluidAtPos(entity, entity.level()) == NorthstarFluids.SULFURIC_ACID.getSource().getSource()) {
            sulfurBurn(entity, entity.getRandom());
        }

        // Test code to figure out how changing dimensions works
    // if(entity instanceof ServerPlayer && entity.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.FLINT_AND_STEEL && !entity.level.isClientSide && entity.level.dimension() == Level.OVERWORLD) {
    // ResourceKey<Level> dest = NorthstarDimensions.MOON_DIM_KEY;
    // ServerLevel destLevel = entity.getLevel().getServer().getLevel(dest);
    // RocketHandler.changePlayerDimension(destLevel, (ServerPlayer) entity, new PortalForcer(destLevel));
    // }
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
