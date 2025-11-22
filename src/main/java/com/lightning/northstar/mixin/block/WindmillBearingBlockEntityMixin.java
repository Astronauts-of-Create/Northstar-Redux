package com.lightning.northstar.mixin.block;

import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WindmillBearingBlockEntity.class, remap = false)
public abstract class WindmillBearingBlockEntityMixin extends MechanicalBearingBlockEntity {

    public WindmillBearingBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @ModifyReturnValue(method = "getGeneratedSpeed", at = @At("RETURN"))
    private float northstar$updateGeneratedSpeed(float value) {
        return value * NorthstarPlanets.getWindMultiplier(level);
    }

}
