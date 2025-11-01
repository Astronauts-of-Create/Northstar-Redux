package com.lightning.northstar.mixin.compat.tfmg;

import com.drmangotea.tfmg.content.machinery.misc.air_intake.AirIntakeBlock;
import com.lightning.northstar.accessor.NorthstarOxygenConsumingBlockEntity;
import com.lightning.northstar.world.oxygen.OxygenConsumer;
import com.lightning.northstar.world.sealer.SealableBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(AirIntakeBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AirIntakeBlockMixin implements SealableBlock, OxygenConsumer {

    @Override
    public boolean northstar$isFaceSealed(BlockGetter level, BlockPos pos, BlockState state, Direction direction, boolean source, SealingMode mode) {
        return direction != state.getValue(AirIntakeBlock.FACING);
    }

    @Override
    public boolean northstar$isGogglesOnly(BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public float northstar$getOxygenConsumption(BlockGetter level, BlockPos pos, float base) {
        return level.getBlockEntity(pos) instanceof NorthstarOxygenConsumingBlockEntity be ? be.northstar$getOxygenUsage() * base : 0;
    }

}
