package com.lightning.northstar.mixin.compat.create;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.fluids.transfer.FluidFillingBehaviour;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FluidFillingBehaviour.class)
public class FluidFillingBehaviourMixin {

    @ModifyExpressionValue(
            method = "tryDeposit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/dimension/DimensionType;ultraWarm()Z"
            )
    )
    private boolean northstar$isUltraWarm(
            boolean original,
            @Local(argsOnly = true) Fluid fluid,
            @Local(argsOnly = true) BlockPos pos,
            @Local(name = "world") Level world
    ) {
        return FluidHelper.isTag(fluid, FluidTags.WATER) && world.northstar$temperature().isUltraWarm(pos, 100, original);
    }

}
