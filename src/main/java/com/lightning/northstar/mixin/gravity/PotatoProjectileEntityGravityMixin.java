package com.lightning.northstar.mixin.gravity;

import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PotatoProjectileEntity.class)
public class PotatoProjectileEntityGravityMixin extends AbstractHurtingProjectile {

    protected PotatoProjectileEntityGravityMixin(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyConstant(
            method = "tick",
            constant = @Constant(doubleValue = -0.05)
    )
    private double northstar$modifyGravity(double constant) {
        return constant * level().northstar$gravityScale();
    }

}
