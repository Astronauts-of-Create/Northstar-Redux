package com.lightning.northstar.mixin.compat.tfmg_ce;

import com.drmangotea.tfmg.base.fluid.ForceableFluidTank;
import com.drmangotea.tfmg.content.machinery.misc.exhaust.ExhaustBlockEntity;
import com.lightning.northstar.accessor.NorthstarOxygenConsumingBlockEntity;
import com.lightning.northstar.api.WhenModLoaded;
import com.lightning.northstar.data.ModCompat;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;

@WhenModLoaded(ModCompat.TFMG_CE)
@Mixin(ExhaustBlockEntity.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ExhaustBlockEntityMixin implements NorthstarOxygenConsumingBlockEntity {

    @Final
    @Shadow(remap = false)
    public ForceableFluidTank tankInventory;

    @Unique
    private int northstar$dumpedLastTick;

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/drmangotea/tfmg/base/fluid/ForceableFluidTank;forceDrain(ILnet/neoforged/neoforge/fluids/capability/IFluidHandler$FluidAction;)Lnet/neoforged/neoforge/fluids/FluidStack;",
                    shift = At.Shift.BEFORE
            )
    )
    private void northstar$onDrain(CallbackInfo ci) {
        northstar$dumpedLastTick = Math.min(100, tankInventory.getFluidAmount());
    }

    @Override
    public float northstar$getOxygenUsage() {
        return northstar$dumpedLastTick;
    }

}
