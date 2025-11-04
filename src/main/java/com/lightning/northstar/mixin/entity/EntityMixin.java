package com.lightning.northstar.mixin.entity;

import com.lightning.northstar.accessor.NorthstarEntity;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Entity.class)
public class EntityMixin implements NorthstarEntity {

    // Disable block collisions
//    @Inject(method = "isColliding", at = @At("HEAD"), cancellable = true)
//    private void disableBlockCollision(BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
//        if (northstar$disableCollision > Byte.MIN_VALUE) {
//            cir.setReturnValue(false);
//            northstar$disableCollision--;
//        }
//    }

    // Disable collisions with entities
    @Inject(method = "canCollideWith", at = @At("HEAD"), cancellable = true)
    private void disableEntityCollision(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (northstar$disableCollision > 0 && !(entity instanceof RocketContraptionEntity)) {
            cir.setReturnValue(false);
            northstar$disableCollision--;
        }
    }

    // Disable being pushed by fluids
//    @Inject(method = "isPushedByFluid", at = @At("HEAD"), cancellable = true)
//    private void disableFluidPush(CallbackInfoReturnable<Boolean> cir) {
//        if (northstar$disableCollision > 0) {
//            cir.setReturnValue(false);
//            northstar$disableCollision--;
//        }
//    }

//    // Disable block pushing (e.g., suffocating in walls)
//    @Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
//    private void disableWallCheck(CallbackInfoReturnable<Boolean> cir) {
//        if (northstar$disableCollision > 0) {
//            cir.setReturnValue(false);
//            northstar$disableCollision--;
//        }
//    }

//    // Disable stepping or pushing
//    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
//    private void disablePushable(CallbackInfoReturnable<Boolean> cir) {
//        if (northstar$disableCollision > 0) {
//            cir.setReturnValue(false);
//            northstar$disableCollision--;
//        }
//    }

    @Unique
    private byte northstar$disableCollision = Byte.MIN_VALUE;

    @Override
    public void northstar$disableColllision(boolean val) {
        northstar$disableCollision = val ? Byte.MAX_VALUE : Byte.MIN_VALUE;
    }
}
