package com.lightning.northstar.mixin.block;

import com.lightning.northstar.world.temperature.NorthstarTemperature;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FluidType.class)
public abstract class FluidTypeMixin {

    @Shadow
    public abstract FluidState getStateForPlacement(BlockAndTintGetter getter, BlockPos pos, FluidStack stack);

    @ModifyExpressionValue(
            method = "isVaporizedOnPlacement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/dimension/DimensionType;ultraWarm()Z"
            )
    )
    private boolean northstar$isVaporizedOnPlacementIsUltraWarm(
            boolean original,
            @Local(argsOnly = true) Level level,
            @Local(argsOnly = true) BlockPos pos,
            @Local(argsOnly = true) FluidStack stack
    ) {
        return level.northstar$temperature().isUltraWarm(pos, NorthstarTemperature.getBoilingPoint(getStateForPlacement(level, pos, stack)), original);
    }

}
