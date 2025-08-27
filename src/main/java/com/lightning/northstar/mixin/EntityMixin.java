package com.lightning.northstar.mixin;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.contraptions.RocketContraptionEntity;
import com.lightning.northstar.mixinInterfaces.EntityMixin_I;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityMixin_I {

    @Shadow
    protected abstract void addPassenger(Entity p_20081_);

    public void out_addPassenger(Entity p_20081_) {
        //We expose the private method in a mixin
        this.addPassenger(p_20081_);
    }

    @Shadow
    private Entity vehicle;


    @Shadow
    @Nullable
    public abstract Entity getVehicle();

    public void pinToVehicle(Entity vehicle) {
        Entity me = ((Entity) ((Object) this));
        this.vehicle = vehicle;
        ((EntityMixin_I) vehicle).out_addPassenger(me);
        recordOffsetOfPassenger(me, vehicle); //If the entity was previously unseated, they will need an offset even if they are a player
    }


    // Store offsets for passengers
    @Unique
    private Map<UUID, Vec3> rocketPassengerOffsets;

    public Map<UUID, Vec3> getRocketPassengerOffsets() {
        return rocketPassengerOffsets;
    }

    public void setRocketPassengerOffsets(Map<UUID, Vec3> set) {
        this.rocketPassengerOffsets = set;
    }

    @Inject(method = "addPassenger", at = @At("HEAD"))
    private void recordOffset(Entity passenger, CallbackInfo ci) {
        Entity me = ((Entity) ((Object) this));
        if (me instanceof RocketContraptionEntity) {
            Northstar.LOGGER.info("ADDING Passenger " + passenger + "; Rocket: " + me);
            if (!(passenger instanceof Player)) {
                recordOffsetOfPassenger(passenger, me);
                passenger.setInvulnerable(true);
                passenger.noPhysics = true;
            }
        }
    }

    @Inject(method = "removePassenger(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void onRemovePassenger(Entity passenger, CallbackInfo ci) {
        Entity me = ((Entity) ((Object) this));
        if (me instanceof RocketContraptionEntity rce) {//We only remove passengers from rockets when the rockets disassemble
            Northstar.LOGGER.info("REMOVING Passenger " + passenger + "; Rocket: " + rce);
            passenger.setInvulnerable(false);
            passenger.noPhysics = false;
        }
    }


    @Unique
    private static boolean canDetach(RocketContraptionEntity rocketContraptionEntity) {
        return !rocketContraptionEntity.isLaunchingOrLanding();
    }

    @Unique
    private static void recordOffsetOfPassenger(Entity passenger, Entity vehicle) {
        EntityMixin_I vehicleMixin = (EntityMixin_I) vehicle;
        if (vehicleMixin.getRocketPassengerOffsets() == null) {
            vehicleMixin.setRocketPassengerOffsets(new HashMap<>());
        }
        Vec3 offset = new Vec3(passenger.getX() - vehicle.getX(), passenger.getY() - vehicle.getY(), passenger.getZ() - vehicle.getZ());
        if (!vehicleMixin.getRocketPassengerOffsets().containsKey(passenger.getUUID())) { //Only record the offset once
            Northstar.LOGGER.info("Recording Passenger Offset. Passenger: " + passenger + "; Rocket: " + vehicle + "; Offset: " + offset);
            vehicleMixin.getRocketPassengerOffsets().put(passenger.getUUID(), offset);
        }
//        else {
//            Northstar.LOGGER.info("Passenger Offset already recorded. Passenger: " + passenger + "; Rocket: " + vehicle + "; Offset: " + offset);
//        }
    }

    @Inject(method = "rideTick", at = @At("HEAD"), cancellable = true)
    private void onRideTick(CallbackInfo ci) {
        Entity me = ((Entity) ((Object) this));
        if (me.getVehicle() instanceof RocketContraptionEntity rce) {
            EntityMixin_I vehicle = (EntityMixin_I) me.getVehicle();
            if (vehicle.getRocketPassengerOffsets() == null) return;

            Vec3 offset = vehicle.getRocketPassengerOffsets().get(me.getUUID());
            if (offset != null) { //Only lock the entity in if there is an offset
//                System.out.println(me + ": RideTick: " + offset);
                me.setDeltaMovement(Vec3.ZERO);
                if (me instanceof Player && me.canUpdate()) { //Save on ticking while flying unless we really need to
                    me.tick();
                }
                me.setPos(me.getVehicle().position().x + offset.x,
                        me.getVehicle().position().y + offset.y,
                        me.getVehicle().position().z + offset.z);
                ci.cancel();
            }
        }
    }


    //Prevent a passenger from leaving the vehicle
    @Inject(method = "removeVehicle", at = @At("HEAD"), cancellable = true)
    public void removeVehicle(CallbackInfo ci) {
        if (getVehicle() instanceof RocketContraptionEntity rocketContraptionEntity) {
            if (rocketContraptionEntity.isAlive() && !canDetach(rocketContraptionEntity)) { //if the rocket is still alive and moving

//                Entity me = (Entity) (Object) this;
//                if (me instanceof Player) {
//                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
//                        Minecraft.getInstance().gui.setOverlayMessage(
//                                Lang.translateDirect("contraption.controls.cant_leave_rocket").withStyle(ChatFormatting.RED), false);
//
//                    });
//                }

                ci.cancel();
            }
        }
    }


}
