package com.lightning.northstar.mixin.item;

import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireChargeItem.class)
public class FireChargeItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void northstar$testOxygen(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!NorthstarOxygen.hasOxygen(context.getLevel(), context.getClickedPos().relative(context.getClickedFace()))) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

}
