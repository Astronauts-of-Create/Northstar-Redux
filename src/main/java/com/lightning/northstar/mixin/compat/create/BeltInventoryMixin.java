package com.lightning.northstar.mixin.compat.create;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BeltInventory.class)
public class BeltInventoryMixin {

    @Shadow(remap = false)
    @Final
    BeltBlockEntity belt;

    @ModifyConstant(
            method = "eject",
            constant = @Constant(doubleValue = (double) (1 / 8f)),
            remap = false
    )
    private double northstar$modifyVerticalVelocity(double original) {
        return original * belt.getLevel().northstar$gravityScale();
    }

}
