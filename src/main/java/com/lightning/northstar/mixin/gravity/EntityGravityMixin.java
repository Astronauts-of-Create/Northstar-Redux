package com.lightning.northstar.mixin.gravity;

import com.lightning.northstar.accessor.NorthstarEntity;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarTags.NorthstarEntityTags;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.Function;

@Mixin(Entity.class)
public abstract class EntityGravityMixin implements NorthstarEntity {

    @Shadow
    private Level level;
    @Shadow
    @Nullable
    private Entity vehicle;
    @Shadow
    public boolean verticalCollisionBelow;

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract double getZ();

    @Shadow
    @Nullable
    public abstract Entity changeDimension(ServerLevel destination, ITeleporter teleporter);

    @Unique
    private boolean northstar$lastCollisionBelow;

    @ModifyVariable(
            method = "move",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/Entity;verticalCollisionBelow:Z",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            ),
            ordinal = 0,
            argsOnly = true
    )
    private Vec3 northstar$modifyMovementVelocity(Vec3 value) {
        if (verticalCollisionBelow != northstar$lastCollisionBelow) {
            northstar$lastCollisionBelow = verticalCollisionBelow;

            // if we just left the ground (by walking off, not jumping) then cancel out the vertical velocity
            // applied by LivingEntityGravityMixin to prevent the entity from flying downwards
            if (!verticalCollisionBelow && value.y < 0) {
                return new Vec3(value.x, 1f, value.z);
            }
        }
        return value;
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void northstar$onMove(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        if (!(level instanceof ServerLevel level) ||
            !NorthstarConfigs.server().allowDimensionTraversal.get() ||
            vehicle instanceof RocketContraptionEntity ||
            NorthstarEntityTags.IGNORE_WORLD_BOUNDS_TELEPORT.matches(self) ||
            self instanceof ServerPlayer sp && sp.connection == null) {
            return;
        }

        PlanetDimension currentDim = level.northstar$dimension();
        if (currentDim.dimensionBelow() != null && getY() <= level.getMinBuildHeight() - 50) {
            ServerLevel targetLevel = level.getServer().getLevel(currentDim.dimensionBelow());
            if (targetLevel != null) {
                northstar$changeDimension(targetLevel, targetLevel.getMaxBuildHeight() + NorthstarConfigs.server().getCombinedTeleportHeight() - 100);
            }
        }
        if (currentDim.dimensionAbove() != null && getY() >= level.getMaxBuildHeight() + NorthstarConfigs.server().getCombinedTeleportHeight()) {
            ServerLevel targetLevel = level.getServer().getLevel(currentDim.dimensionAbove());
            if (targetLevel != null) {
                northstar$changeDimension(targetLevel, targetLevel.getMinBuildHeight() - 20);
            }
        }
    }

    @Unique
    private void northstar$changeDimension(ServerLevel level, double y) {
        Entity moved = changeDimension(level, new ITeleporter() {
            @Override
            public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
                return new PortalInfo(new Vec3(getX(), y, getZ()), getDeltaMovement(), entity.getXRot(), entity.getYRot());
            }
        });
        if (moved != null) {
            moved.setDeltaMovement(getDeltaMovement());
            if (moved instanceof ServerPlayer sp) {
                sp.connection.send(new ClientboundSetEntityMotionPacket(moved.getId(), getDeltaMovement()));
            }
        }
    }

    // I love scuffed injection points :D
    @ModifyExpressionValue(
            method = "push(Lnet/minecraft/world/entity/Entity;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/Entity;noPhysics:Z",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 1
            )
    )
    private boolean northstar$push(boolean noPhysics, @Local(argsOnly = true) Entity entity) {
        if (noPhysics || !level.northstar$isZeroGravity()) {
            return noPhysics;
        }
        Entity self = (Entity) (Object) this;

        double dx = entity.getX() - self.getX();
        double dy = entity.getY() - self.getY();
        double dz = entity.getZ() - self.getZ();
        double d = Mth.absMax(Mth.absMax(dx, dz), dy);
        if (d >= 0.01F) {
            d = Math.sqrt(d);
            dx /= d;
            dy /= d;
            dz /= d;
            double inv = 1.0 / d;
            if (inv > 1.0) {
                inv = 1.0;
            }

            dx *= inv;
            dy *= inv;
            dz *= inv;
            dx *= 0.05F;
            dy *= 0.05F;
            dz *= 0.05F;
            if (!self.isVehicle() && self.isPushable()) {
                self.push(-dx, -dy, -dz);
            }

            if (!entity.isVehicle() && entity.isPushable()) {
                entity.push(dx, dy, dz);
            }
        }

        return true;
    }

}
