package com.lightning.northstar.mixin.block;

import com.lightning.northstar.world.NorthstarOxygen;
import com.lightning.northstar.world.NorthstarTemperature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireBlock.class)
public class FireBlockMixin {

    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    public void northstar$canSurvive(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        if (NorthstarTemperature.getTemperatureAt((Level) level, pos) < -100 ||
                !NorthstarOxygen.hasOxygen((Level) level, pos)) {
            info.setReturnValue(false);
        }
    }

}
