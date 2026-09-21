package com.lightning.northstar.mixin.block;

import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(AbstractFurnaceBlockEntity.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class AbstractFurnaceBlockEntityMixin extends BaseContainerBlockEntity {

    protected AbstractFurnaceBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @ModifyReturnValue(
            method = "canBurn",
            at = @At("RETURN")
    )
    private static boolean northstar$preventBurningWithoutOxygen(
            boolean original,
            @Local(argsOnly = true) AbstractFurnaceBlockEntity furnace
    ) {
        return original && NorthstarOxygen.hasOxygen(furnace.getLevel(), furnace.getBlockPos());
    }

}
