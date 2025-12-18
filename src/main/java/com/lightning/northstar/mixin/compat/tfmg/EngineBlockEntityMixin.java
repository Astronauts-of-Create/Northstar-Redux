package com.lightning.northstar.mixin.compat.tfmg;

import com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity;
import com.lightning.northstar.api.WhenModLoaded;
import com.lightning.northstar.data.ModCompat;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.ParametersAreNonnullByDefault;

@WhenModLoaded(ModCompat.TFMG)
@Mixin(AbstractEngineBlockEntity.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EngineBlockEntityMixin extends KineticBlockEntity {

    public EngineBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @ModifyReturnValue(method = "canWork", at = @At("RETURN"), remap = false)
    private boolean northstar$checkForOxygen(boolean original) {
        return original && NorthstarOxygen.hasOxygen(level, worldPosition);
    }

}
