package com.lightning.northstar.mixin.compat.tfmg;

import com.drmangotea.tfmg.blocks.machines.exhaust.ExhaustBlock;
import com.lightning.northstar.accessor.NorthstarOxygenConsumingBlockEntity;
import com.lightning.northstar.api.WhenModLoaded;
import com.lightning.northstar.data.ModCompat;
import com.lightning.northstar.world.oxygen.OxygenConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@WhenModLoaded(ModCompat.TFMG)
@Mixin(ExhaustBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ExhaustBlockMixin implements OxygenConsumer {

    @Override
    public boolean northstar$isOxygenConsumptionDynamic(BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public float northstar$getOxygenConsumption(BlockGetter level, BlockPos pos, float base) {
        // dumping CO2 in an enclosed environment can't be recommended
        return level.getBlockEntity(pos) instanceof NorthstarOxygenConsumingBlockEntity be ? be.northstar$getOxygenUsage() * base : 0;
    }

}
