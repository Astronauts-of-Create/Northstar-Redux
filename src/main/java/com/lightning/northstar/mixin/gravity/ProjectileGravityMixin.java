package com.lightning.northstar.mixin.gravity;

import com.lightning.northstar.config.NorthstarConfigs;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public abstract class ProjectileGravityMixin extends Entity {

    @Shadow
    protected abstract void onHit(HitResult result);

    public ProjectileGravityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // TODO: Add an allow/deny-list using entity tags?
    @Inject(method = "tick", at = @At("TAIL"))
    private void northstar$tick(CallbackInfo ci) {
        // when slow enough, fake hitting something to prevent it from flying forever, this causes potions and
        //  experience bottles to explode, tridents to return etc...
        Vec3 delta = getDeltaMovement();
        if (level().northstar$isZeroGravity() && delta.lengthSqr() < 0.01 && NorthstarConfigs.server().removeStalledProjectiles.get()) {
            onHit(new BlockHitResult(position(), Direction.getNearest(delta.x, delta.y, delta.z), blockPosition(), true));
        }
    }

}
