package com.lightning.northstar.mixin.compat.tfmg_ce;

import com.drmangotea.tfmg.content.machinery.misc.air_intake.AirIntakeBlockEntity;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.lightning.northstar.accessor.NorthstarOxygenConsumingBlockEntity;
import com.lightning.northstar.api.WhenModLoaded;
import com.lightning.northstar.data.ModCompat;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.ParametersAreNonnullByDefault;

@WhenModLoaded(ModCompat.TFMG_CE)
@Mixin(AirIntakeBlockEntity.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AirIntakeBlockEntityMixin extends KineticBlockEntity implements NorthstarOxygenConsumingBlockEntity {

    @Unique
    private int northstar$lastConsumed;

    public AirIntakeBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/drmangotea/tfmg/content/machinery/misc/air_intake/AirIntakeBlockEntity;getProduction()I",
                    ordinal = 1
            ),
            remap = false,
            require = 0
    )
    private int northstar$tick$addOxygenRequirement(int original) {
        NorthstarOxygen oxygen = level.northstar$oxygen();
        northstar$lastConsumed = 0;
        if (oxygen.hasOxygen()) {
            return original;
        }

        NorthstarOxygen.Provider sealer = oxygen.getSealer(worldPosition);
        if (sealer == null) {
            return 0;
        }

        northstar$lastConsumed = original;
        sealer.drainOxygen(original);
        return original;
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/tterrag/registrate/util/entry/FluidEntry;get()Ljava/lang/Object;",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private Object northstar$convertToHotAir(Object original) {
        if (!TFMGFluids.AIR.is(original)) {
            return original;
        }
        return NorthstarTemperature.getTemperature(level, worldPosition) >= 1000 ? TFMGFluids.HOT_AIR.getSource() : TFMGFluids.AIR.getSource();
    }

    @Override
    public float northstar$getOxygenUsage() {
        return northstar$lastConsumed;
    }

}
