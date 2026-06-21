package com.lightning.northstar.mixin.compat.cdg;

import com.jesz.createdieselgenerators.content.diesel_engine.huge.HugeDieselEngineBlockEntity;
import com.lightning.northstar.api.WhenModLoaded;
import com.lightning.northstar.data.ModCompat;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@WhenModLoaded(ModCompat.CDG)
@Mixin(HugeDieselEngineBlockEntity.class)
public abstract class HugeDieselEngineBlockEntityMixin extends SmartBlockEntity {

    @Unique
    private boolean northstar$hasOxygen;

    public HugeDieselEngineBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Redirect(
            method = { "tick", "tickClient", "addToGoggleTooltip" },
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/jesz/createdieselgenerators/content/diesel_engine/huge/HugeDieselEngineBlockEntity;enabled()Z",
                    remap = false
            ),
            remap = false
    )
    private boolean northstar$addOxygenCheck(HugeDieselEngineBlockEntity instance) {
        return northstar$hasOxygen && instance.enabled();
    }

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void northstar$tick(CallbackInfo ci) {
        northstar$hasOxygen = NorthstarOxygen.hasOxygen(level, worldPosition);
    }

}
