package com.lightning.northstar.mixin.gravity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PrimedTnt.class)
public abstract class PrimedTntGravityMixin extends Entity {

    protected PrimedTntGravityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyConstant(
            method = "tick",
            constant = @Constant(doubleValue = -0.04D)
    )
    private double northstar$modifyGravity(double constant) {
        return constant * level().northstar$gravityScale();
    }

}
