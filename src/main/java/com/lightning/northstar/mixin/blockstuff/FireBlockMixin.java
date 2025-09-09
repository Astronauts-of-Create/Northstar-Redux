package com.lightning.northstar.mixin.blockstuff;

import com.lightning.northstar.world.NorthstarOxygen;
import com.lightning.northstar.world.NorthstarTemperature;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
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
    public void canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos, CallbackInfoReturnable<Boolean> info) {
        try {
            if (NorthstarTemperature.getTemperatureAt((Level) pLevel, pPos) < -100 || (!NorthstarOxygen.hasOxygen(((Level)pLevel), pPos) && !NorthstarPlanets.getPlanetOxy(((Level)pLevel).dimension()))) {
                info.setReturnValue(false);
            }
        } catch (Exception e) {
            //oops
        }

//        System.out.println("TEMP: " + TemperatureStuff.getTemp(pPos, (Level) pLevel));
//        System.out.println("OXY: " + OxygenStuff.hasOxygen(pPos,((Level)pLevel).dimension()));
//        System.out.println("PLANET OXY: " + NorthstarPlanets.getPlanetOxy(((Level)pLevel).dimension()));
    }


}
