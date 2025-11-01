package com.lightning.northstar.mixin.block;

import com.lightning.northstar.accessor.NorthstarFluidState;
import com.lightning.northstar.world.sealer.SealingMode;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(FluidState.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FluidStateMixin implements NorthstarFluidState {

    @Inject(method = "tick", at = @At("TAIL"))
    public void northstar$tick(Level level, BlockPos pos, CallbackInfo info) {
        // this can probably be removed or replaced by a check on placement
        northstar$onSealUpdated(level, pos, SealingMode.TEMPERATURE);
    }

    @Override
    public void northstar$onSealUpdated(Level level, BlockPos pos, SealingMode mode) {
        if (mode != SealingMode.TEMPERATURE)
            return;

        FluidState self = (FluidState) (Object) this;

        float temperature = NorthstarTemperature.getTemperatureAt(level, pos);

        if (temperature >= NorthstarTemperature.getBoilingPoint(self)) {
            BlockState block = level.getBlockState(pos);
            if (block.hasProperty(BlockStateProperties.WATERLOGGED) && !self.isEmpty()) {
                level.setBlockAndUpdate(pos, block.setValue(BlockStateProperties.WATERLOGGED, false));
            } else {
                NorthstarTemperature.evaporate(level, pos);
            }
            return;
        }

        if (temperature <= NorthstarTemperature.getFreezingPoint(self) && self.is(Fluids.WATER)) {
            BlockState block = level.getBlockState(pos);
            if (block.hasProperty(BlockStateProperties.WATERLOGGED)) {
                level.setBlockAndUpdate(pos, block.setValue(BlockStateProperties.WATERLOGGED, false));
            } else {
                level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
            }
            return;
        }

        if (NorthstarTemperature.isCombustible(self) && temperature >= NorthstarTemperature.combustionTemp(self)) {
            northstar$combust(level, pos);
        }
    }

    //EXPLOSION!!!!!! YEAH!!!!!!! I LOVE DEATH AND DESTRUCTION!!!!!!!!!!!!!!!!!!!
    @Unique
    public void northstar$combust(Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 2.5F, true, Level.ExplosionInteraction.MOB);
    }

}
