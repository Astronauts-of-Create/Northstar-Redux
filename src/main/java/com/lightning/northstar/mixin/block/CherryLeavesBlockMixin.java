package com.lightning.northstar.mixin.block;

import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CherryLeavesBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CherryLeavesBlock.class)
public class CherryLeavesBlockMixin {

    @WrapWithCondition(
            method = "animateTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/ParticleUtils;spawnParticleBelow(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/particles/ParticleOptions;)V"
            )
    )
    private boolean northstar$shouldLeavesFall(Level level, BlockPos pos, RandomSource random, ParticleOptions particle) {
        return NorthstarOxygen.hasOxygen(level, pos);
    }

}
