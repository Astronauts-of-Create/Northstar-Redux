package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Containers.class)
public class ContainersGravityMixin {

    @ModifyConstant(
            method = "dropItemStack",
            constant = @Constant(doubleValue = 0.2)
    )
    private static double northstar$modifyVelocityY(double constant, @Local(argsOnly = true) Level level) {
        return level.northstar$isZeroGravity() ? 0.0 : constant;
    }

}
