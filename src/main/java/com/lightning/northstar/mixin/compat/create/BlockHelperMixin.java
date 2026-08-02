package com.lightning.northstar.mixin.compat.create;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockHelper.class)
public class BlockHelperMixin {

    @ModifyExpressionValue(
            method = "destroyBlockAs",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/dimension/DimensionType;ultraWarm()Z"
            )
    )
    private static boolean northstar$destroyBlockIsUltraWarm(
            boolean original,
            @Local(argsOnly = true) Level level,
            @Local(argsOnly = true) BlockPos pos
    ) {
        return level.northstar$temperature().isUltraWarm(pos, 100, original);
    }

    @ModifyExpressionValue(
            method = "placeSchematicBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"
            )
    )
    private static boolean northstar$placeSchematicBlockIsUltraWarm(
            boolean original,
            @Local(argsOnly = true) Level level,
            @Local(argsOnly = true) BlockPos pos
    ) {
        return level.northstar$temperature().isUltraWarm(pos, 100, original);
    }

}
