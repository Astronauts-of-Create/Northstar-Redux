package com.lightning.northstar.mixin.compat.create;

import com.lightning.northstar.world.oxygen.OxygenConsumer;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import javax.annotation.ParametersAreNonnullByDefault;

@Pseudo
@Mixin(value = BlazeBurnerBlock.class,
        targets = "com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlazeBurnerBlockMixin implements OxygenConsumer {

    @Override
    public float northstar$getOxygenConsumption(BlockGetter level, BlockPos pos, float base) {
        return switch (level.getBlockState(pos).getValue(BlazeBurnerBlock.HEAT_LEVEL)) {
            case NONE, SMOULDERING -> 0;
            case FADING, KINDLED -> base * 5f;
            case SEETHING -> base * 7.5f;
        };
    }

}
