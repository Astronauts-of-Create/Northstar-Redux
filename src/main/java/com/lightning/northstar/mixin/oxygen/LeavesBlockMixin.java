package com.lightning.northstar.mixin.oxygen;

import com.lightning.northstar.world.oxygen.OxygenConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LeavesBlock;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(LeavesBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LeavesBlockMixin implements OxygenConsumer {

    @Override
    public float northstar$getOxygenConsumption(BlockGetter level, BlockPos pos, float base) {
        return base * -0.2f;
    }

}
