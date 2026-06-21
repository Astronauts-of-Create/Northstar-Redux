package com.lightning.northstar.mixin.gravity;

import com.lightning.northstar.content.NorthstarTags.NorthstarEntityTags;
import com.lightning.northstar.entity.ai.ZeroGravityMoveControl;
import com.lightning.northstar.entity.ai.ZeroGravityNavigation;
import com.lightning.northstar.planet.ZeroGravityUtils;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MobGravityMixin extends LivingEntity {

    @Shadow
    protected MoveControl moveControl;

    @Shadow
    protected PathNavigation navigation;

    @Shadow
    protected abstract PathNavigation createNavigation(Level level);

    protected MobGravityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void northstar$onResourceReload() {
        super.northstar$onResourceReload();

        boolean shouldUseZeroGravity = level().northstar$isZeroGravity() && !NorthstarEntityTags.IGNORE_ZERO_GRAVITY_AI.matches(this);
        if (shouldUseZeroGravity == navigation instanceof ZeroGravityNavigation) {
            return;
        }

        Mob self = (Mob) (Object) this;
        PathNavigation nav = createNavigation(level());
        if (shouldUseZeroGravity) {
            if (moveControl.getClass().equals(MoveControl.class)) {
                moveControl = new ZeroGravityMoveControl(self, 20);
            }
            navigation = nav instanceof GroundPathNavigation groundNav ? new ZeroGravityNavigation(self, level(), groundNav) : nav;
        } else {
            if (moveControl instanceof ZeroGravityMoveControl) {
                moveControl = new MoveControl(self);
            }
            navigation = nav;
        }
    }

    @ModifyExpressionValue(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Mob;createNavigation(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/ai/navigation/PathNavigation;"
            )
    )
    private PathNavigation northstar$wrapNavigation(PathNavigation original) {
        if (!level().northstar$isZeroGravity() || NorthstarEntityTags.IGNORE_ZERO_GRAVITY_AI.matches(this)) {
            return original;
        }

        Mob self = (Mob) (Object) this;
        moveControl = new ZeroGravityMoveControl(self, 20);
        return original instanceof GroundPathNavigation nav ? new ZeroGravityNavigation(self, level(), nav) : original;
    }

    @WrapWithCondition(
            method = "doHurtTarget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"
            )
    )
    private boolean northstar$replaceZeroGravityKnockback(LivingEntity entity, double strength, double x, double z) {
        return ZeroGravityUtils.shouldApplyKnockback(this, entity, strength);
    }

}
