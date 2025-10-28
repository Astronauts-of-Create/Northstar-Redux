package com.lightning.northstar.mixin.compat.tfmg;

import com.drmangotea.tfmg.content.machinery.misc.air_intake.AirIntakeBlockEntity;
import com.drmangotea.tfmg.registry.TFMGFluids;
import com.lightning.northstar.accessor.NorthstarOxygenConsumingBlockEntity;
import com.lightning.northstar.world.NorthstarTemperature;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(AirIntakeBlockEntity.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AirIntakeBlockEntityMixin extends KineticBlockEntity implements NorthstarOxygenConsumingBlockEntity {

    @Unique
    private int northstar$lastConsumed;

    public AirIntakeBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @ModifyVariable(method = "produceAir",
            at = @At("STORE"),
            ordinal = 0,
            remap = false)
    private int northstar$addOxygenRequirement(int production) {
        NorthstarOxygen oxygen = level.northstar$oxygen();
        northstar$lastConsumed = 0;
        if (oxygen.hasOxygen())
            return production;
        NorthstarOxygen.Provider sealer = oxygen.getSealer(worldPosition);
        if (sealer == null)
            return 0;
        northstar$lastConsumed = production;
        sealer.drainOxygen(production);
        return production;
    }

    @Redirect(method = "produceAir",
            at = @At(value = "INVOKE",
                    target = "Lcom/tterrag/registrate/util/entry/FluidEntry;getSource()Lnet/minecraftforge/fluids/ForgeFlowingFluid;",
                    remap = false),
            remap = false)
    private ForgeFlowingFluid northstar$convertToHotAir(FluidEntry<?> instance) {
        // Only handle air for now, other gases will be implemented with the new planet system.
        return NorthstarTemperature.getTemperatureAt(level, worldPosition) >= 1000 ? TFMGFluids.HOT_AIR.getSource() : TFMGFluids.AIR.getSource();
    }

    @Override
    public float northstar$getOxygenUsage() {
        return northstar$lastConsumed;
    }

}
