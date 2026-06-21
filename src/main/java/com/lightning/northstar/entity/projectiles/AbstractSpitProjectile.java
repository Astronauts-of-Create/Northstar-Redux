package com.lightning.northstar.entity.projectiles;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AbstractSpitProjectile extends Projectile {

    protected AbstractSpitProjectile(EntityType<? extends AbstractSpitProjectile> entityType, Level level) {
        super(entityType, level);
    }

    protected AbstractSpitProjectile(EntityType<? extends AbstractSpitProjectile> entityType, Level level, LivingEntity spitter) {
        this(entityType, level);
        setOwner(spitter);
        setPos(
                spitter.getX() - (spitter.getBbWidth() + 1.0F) * 0.5 * Mth.sin(spitter.yBodyRot * (float) (Math.PI / 180.0)),
                spitter.getEyeY() - 0.1F,
                spitter.getZ() + (spitter.getBbWidth() + 1.0F) * 0.5 * Mth.cos(spitter.yBodyRot * (float) (Math.PI / 180.0))
        );
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vec3 = getDeltaMovement();
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitresult))
            onHit(hitresult);
        double newX = getX() + vec3.x;
        double newY = getY() + vec3.y;
        double newZ = getZ() + vec3.z;
        updateRotation();
        if (level().getBlockStates(getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
            discard();
        } else if (isInWaterOrBubble()) {
            discard();
        } else {
            setDeltaMovement(vec3.scale(0.99F));
            applyGravity();
            setPos(newX, newY, newZ);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        double d0 = packet.getXa();
        double d1 = packet.getYa();
        double d2 = packet.getZa();

        for (int i = 0; i < 7; ++i) {
            double d3 = 0.4D + 0.1D * (double) i;
            this.level().addParticle(ParticleTypes.SPIT, this.getX(), this.getY(), this.getZ(), d0 * d3, d1, d2 * d3);
        }

        this.setDeltaMovement(d0, d1, d2);
    }

}
