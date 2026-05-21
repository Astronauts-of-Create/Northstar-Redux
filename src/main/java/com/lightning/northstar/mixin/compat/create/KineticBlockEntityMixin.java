package com.lightning.northstar.mixin.compat.create;

import com.lightning.northstar.api.create.ReceivingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KineticBlockEntity.class)
public class KineticBlockEntityMixin {

    // unfortunately overriding propagateRotationTo only works to propagate from ourselves to others,
    //  but we want to propagate from others to ourselves.
    // There might be a way to do it without this mixin but if this is the case, I couldn't find it. If anyone does however
    //  feel free to update this code.
    @Inject(
            method = "propagateRotationTo",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void northstar$injectFanPropagation(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs, CallbackInfoReturnable<Float> cir) {
        if (target instanceof ReceivingKineticBlockEntity inverse) {
            float f = inverse.propagateRotationFrom((KineticBlockEntity) (Object) this, stateTo, stateFrom, diff.multiply(-1), connectedViaAxes, connectedViaCogs);
            if (f != 0)
                cir.setReturnValue(f);
        }
    }

}
