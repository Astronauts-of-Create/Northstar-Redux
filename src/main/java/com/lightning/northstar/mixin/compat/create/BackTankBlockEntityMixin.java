package com.lightning.northstar.mixin.compat.create;

import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(BacktankBlockEntity.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BackTankBlockEntityMixin extends KineticBlockEntity {

    public BackTankBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @ModifyVariable(method = "tick",
            at = @At(value = "STORE",
                    ordinal = 0),
            ordinal = 2,
            remap = false)
    private int northstar$modifyIncrement(int used) {
        NorthstarOxygen oxygen = level.northstar$oxygen();
        if (oxygen.hasOxygen())
            return used;
        NorthstarOxygen.Provider sealer = oxygen.getSealer(worldPosition);
        if (sealer == null)
            return 0;
        sealer.drainOxygen(used);
        return used;
    }

}
