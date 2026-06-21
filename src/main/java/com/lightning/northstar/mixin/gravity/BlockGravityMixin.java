package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Block.class)
public class BlockGravityMixin {

    @ModifyVariable(
            method = "popResourceFromFace",
            at = @At("STORE"),
            ordinal = 6
    )
    private static double northstar$popResourceNoGravity(
            double original,
            @Local(argsOnly = true) Direction direction,
            @Local(argsOnly = true) Level level
    ) {
        if (level.northstar$isZeroGravity())
            return direction.getStepY() == 0 ? Mth.nextDouble(level.random, -0.1, 0.1) : direction.getStepY() * 0.1;
        return original;
    }

}
