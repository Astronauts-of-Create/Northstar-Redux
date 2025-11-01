package com.lightning.northstar.mixin.oxygen;

import com.lightning.northstar.world.oxygen.OxygenConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(AbstractFurnaceBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AbstractFurnaceBlockMixin implements OxygenConsumer {

    @Override
    public boolean northstar$isOxygenConsumptionDynamic(BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public float northstar$getOxygenConsumption(BlockGetter level, BlockPos pos, float base) {
        BlockState state = level.getBlockState(pos);
        return state.hasProperty(AbstractFurnaceBlock.LIT) && state.getValue(AbstractFurnaceBlock.LIT) ? base * 5f : 0;
    }

}
