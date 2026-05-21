package com.lightning.northstar.mixin.gravity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LlamaSpit.class)
public abstract class LlamaSpitGravityMixin extends Projectile {

    protected LlamaSpitGravityMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyConstant(
            method = "tick",
            constant = @Constant(doubleValue = (double) -0.06f)
    )
    private double northstar$modifyGravity(double constant) {
        return constant * level().northstar$gravityScale();
    }

}
