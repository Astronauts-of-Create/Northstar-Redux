package com.lightning.northstar.mixin.compat.create;

import com.lightning.northstar.world.oxygen.OxygenConsumer;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(BacktankBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BackTankBlockMixin implements OxygenConsumer {

    @Override
    public boolean northstar$isGogglesOnly(BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public float northstar$getOxygenConsumption(BlockGetter level, BlockPos pos, float base) {
        if (!(level.getBlockEntity(pos) instanceof BacktankBlockEntity be) || be.getComparatorOutput() == 15)
            return 0;

        float abs = Math.abs(be.getSpeed());
        int increment = Mth.clamp(((int) abs - 100) / 20, 1, 5);
        int airLevelTimer = Mth.clamp((int) (128f - abs / 5f) - 108, 1, 20);

        return (float) increment / (float) airLevelTimer;
    }

}
