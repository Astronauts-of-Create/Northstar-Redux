package com.lightning.northstar.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WetSpongeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WetSpongeBlock.class)
public class WetSpongeBlockMixin {

    @ModifyExpressionValue(
            method = "onPlace",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/dimension/DimensionType;ultraWarm()Z"
            )
    )
    private boolean northstar$onPlaceIsUltraWarm(
            boolean original,
            @Local(argsOnly = true) Level level,
            @Local(argsOnly = true) BlockPos pos
    ) {
        return level.northstar$temperature().isUltraWarm(pos, 100, original);
    }

}
