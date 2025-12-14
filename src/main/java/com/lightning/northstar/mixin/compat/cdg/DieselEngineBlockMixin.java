package com.lightning.northstar.mixin.compat.cdg;

import com.jesz.createdieselgenerators.content.diesel_engine.IEngine;
import com.jesz.createdieselgenerators.content.diesel_engine.huge.HugeDieselEngineBlock;
import com.jesz.createdieselgenerators.content.diesel_engine.modular.ModularDieselEngineBlock;
import com.jesz.createdieselgenerators.content.diesel_engine.normal.DieselEngineBlock;
import com.lightning.northstar.api.WhenModLoaded;
import com.lightning.northstar.data.ModCompat;
import com.lightning.northstar.world.oxygen.OxygenConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@WhenModLoaded(ModCompat.CDG)
@Mixin({ DieselEngineBlock.class, HugeDieselEngineBlock.class, ModularDieselEngineBlock.class })
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DieselEngineBlockMixin implements OxygenConsumer {

    @Override
    public boolean northstar$isOxygenConsumptionDynamic(BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public float northstar$getOxygenConsumption(BlockGetter level, BlockPos pos, float base) {
        if (level.getBlockEntity(pos) instanceof IEngine engine && engine.enabled())
            return 20f * base;
        return 0;
    }

}
