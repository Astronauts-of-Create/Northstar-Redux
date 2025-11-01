package com.lightning.northstar.mixin.oxygen;

import com.lightning.northstar.world.oxygen.OxygenConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseFireBlock;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(BaseFireBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BaseFireBlockMixin implements OxygenConsumer {

    @Override
    public float northstar$getOxygenConsumption(BlockGetter level, BlockPos pos, float base) {
        return base * 10f;
    }

}
