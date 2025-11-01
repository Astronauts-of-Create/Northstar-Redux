package com.lightning.northstar.mixin.block;

import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.sealer.SealReactiveBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(FireBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FireBlockMixin implements SealReactiveBlock {

    // TODO: temperature behaviour isn't consistent with other blocks
    //  only fire extinguishes below a certain temperature

    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    public void northstar$canSurvive(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        if (level instanceof Level l && (NorthstarTemperature.getTemperatureAt(l, pos) < -100 || !NorthstarOxygen.hasOxygen(l, pos))) {
            info.setReturnValue(false);
        }
    }

    @Override
    public void northstar$onSealUpdated(Level level, BlockPos pos, BlockState state, SealingMode mode) {
        if (mode == SealingMode.OXYGEN && !NorthstarOxygen.hasOxygen(level, pos)) {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            return;
        }

        if (mode == SealingMode.TEMPERATURE && NorthstarTemperature.getTemperatureAt(level, pos) < -100) {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

}
