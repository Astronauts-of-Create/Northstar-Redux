package com.lightning.northstar.mixin.compat.create;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeltInventory.class)
public class BeltInventoryMixin {

    @Shadow(remap = false)
    @Final
    BeltBlockEntity belt;

    @ModifyExpressionValue(
            method = "eject",
            at = @At(
                    value = "CONSTANT",
                    args = "doubleValue=0.125"
            ),
            remap = false
    )
    private double northstar$modifyVerticalVelocity(double original) {
        return original * belt.getLevel().northstar$gravityScale();
    }

}
