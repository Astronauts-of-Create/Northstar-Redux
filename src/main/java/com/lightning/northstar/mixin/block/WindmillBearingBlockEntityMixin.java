package com.lightning.northstar.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WindmillBearingBlockEntity.class, remap = false)
public abstract class WindmillBearingBlockEntityMixin extends MechanicalBearingBlockEntity {

    @Shadow
    public abstract void updateGeneratedRotation();

    @Unique
    private float northstar$windMultiplier;

    public WindmillBearingBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void northstar$init(BlockEntityType<?> type, BlockPos pos, BlockState state, CallbackInfo ci) {
        northstar$windMultiplier = 1;
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/simibubi/create/content/contraptions/bearing/WindmillBearingBlockEntity;queuedReassembly:Z",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0
            )
    )
    private void northstar$tick(CallbackInfo ci) {
        float windMultiplier = level.northstar$dimension().wind().get(level, worldPosition);
        if (!Mth.equal(windMultiplier, northstar$windMultiplier) && getFlickerScore() <= 64) {
            northstar$windMultiplier = windMultiplier;
            updateGeneratedRotation();
            notifyUpdate();
        }
    }

    @ModifyReturnValue(
            method = "getGeneratedSpeed",
            at = @At(
                    value = "RETURN",
                    ordinal = 2
            )
    )
    private float northstar$updateGeneratedSpeed(float value) {
        return value * northstar$windMultiplier;
    }

    @Inject(
            method = "write",
            at = @At("TAIL")
    )
    private void northstar$write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        compound.putFloat("NorthstarWindMultiplier", northstar$windMultiplier);
    }

    @Inject(
            method = "read",
            at = @At("TAIL")
    )
    private void northstar$read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        northstar$windMultiplier = compound.getFloat("NorthstarWindMultiplier");
    }

}
