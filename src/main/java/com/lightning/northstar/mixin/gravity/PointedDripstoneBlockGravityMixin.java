package com.lightning.northstar.mixin.gravity;

import com.lightning.northstar.config.NorthstarConfigs;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockGravityMixin {

    @WrapWithCondition(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/PointedDripstoneBlock;maybeTransferFluid(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;F)V"
            )
    )
    private boolean northstar$mayTransferFluid(BlockState state, ServerLevel level, BlockPos pos, float randChance) {
        return !level.northstar$isZeroGravity() || NorthstarConfigs.server().doDripstoneDripWithoutGravity.get();
    }

    @WrapWithCondition(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/PointedDripstoneBlock;growStalactiteOrStalagmiteIfPossible(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V"
            )
    )
    private boolean northstar$mayGrow(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        return !level.northstar$isZeroGravity() || NorthstarConfigs.server().doDripstoneGrowWithoutGravity.get();
    }

    @ModifyExpressionValue(
            method = "animateTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/PointedDripstoneBlock;canDrip(Lnet/minecraft/world/level/block/state/BlockState;)Z"
            )
    )
    private boolean northstar$mayDripParticle(boolean original, @Local(argsOnly = true) Level level) {
        return original && !level.northstar$isZeroGravity();
    }

}
