package com.lightning.northstar.mixin.compat.create;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.fluids.OpenEndedPipe;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OpenEndedPipe.class)
public class OpenEndedPipeMixin {

    @Shadow
    private Level world;
    @Shadow
    private BlockPos outputPos;

    @ModifyExpressionValue(
            method = "provideFluidToSpace",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/dimension/DimensionType;ultraWarm()Z"
            )
    )
    private boolean northstar$isUltraWarm(boolean original, @Local(argsOnly = true) FluidStack fluid) {
        return FluidHelper.isTag(fluid, FluidTags.WATER) && world.northstar$temperature().isUltraWarm(outputPos, 100, original);
    }

}
