package com.lightning.northstar.mixin.block;

import com.lightning.northstar.world.sealer.SealReactiveBlock;
import com.lightning.northstar.world.sealer.SealableBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(IceBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class IceBlockMixin implements SealableBlock, SealReactiveBlock {

    @Shadow
    protected abstract void melt(BlockState state, Level level, BlockPos pos);

    @Override
    public boolean northstar$isFaceSealed(BlockGetter level, BlockPos pos, BlockState state, Direction direction, boolean source, SealingMode mode) {
        return source;
    }

    @Override
    public void northstar$onSealUpdated(Level level, BlockPos pos, BlockState state, SealingMode mode) {
        if (mode != SealingMode.TEMPERATURE)
            return;

        float temperature = NorthstarTemperature.getTemperatureAt(level, pos);
        if (temperature >= 100) {
            NorthstarTemperature.evaporate(level, pos);
        } else if (temperature >= 30) { // ice should melt above 0°C but this would just cause ice to melt in the overworld so the temperature is a bit higher.
            melt(state, level, pos);
        }
    }

}
