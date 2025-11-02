package com.lightning.northstar.mixin.compat.tfmg;

import com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(AbstractEngineBlockEntity.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EngineBlockEntityMixin extends KineticBlockEntity {

    public EngineBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "canWork", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private void northstar$checkForOxygen(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && !NorthstarOxygen.hasOxygen(level, worldPosition)) {
            cir.setReturnValue(false);
        }
    }

}
