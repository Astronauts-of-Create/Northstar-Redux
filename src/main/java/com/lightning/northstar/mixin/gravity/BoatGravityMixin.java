package com.lightning.northstar.mixin.gravity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Boat.class)
public abstract class BoatGravityMixin extends Entity {

    public BoatGravityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyConstant(
            method = "floatBoat",
            constant = @Constant(doubleValue = (double) -0.04f)
    )
    private double northstar$modifyGravity(double constant) {
        return constant * level().northstar$gravityScale();
    }

}
