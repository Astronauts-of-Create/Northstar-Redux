package com.lightning.northstar.mixin.compat.tfmg;

import com.drmangotea.tfmg.content.machinery.misc.exhaust.ExhaustBlockEntity;
import com.lightning.northstar.accessor.NorthstarOxygenConsumingBlockEntity;
import com.lightning.northstar.api.WhenModLoaded;
import com.lightning.northstar.data.ModCompat;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;

@WhenModLoaded(ModCompat.TFMG)
@Mixin(ExhaustBlockEntity.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ExhaustBlockEntityMixin implements NorthstarOxygenConsumingBlockEntity {

    @Shadow(remap = false)
    public FluidTank tankInventory;
    @Unique
    private int northstar$dumpedLastTick;

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void northstar$onDrain(CallbackInfo ci) {
        int drained = tankInventory.getSpace() > 700 ? 100 : 10;
        northstar$dumpedLastTick = Math.min(drained, tankInventory.getFluidAmount());
    }

    @Override
    public float northstar$getOxygenUsage() {
        return northstar$dumpedLastTick;
    }

}
