package com.lightning.northstar.mixin.gravity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({
        AbstractFish.class,
        Dolphin.class,
        Guardian.class
})
public abstract class AquaticEntityGravityMixin extends Entity {

    protected AquaticEntityGravityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyConstant(
            method = "travel",
            constant = @Constant(doubleValue = -0.005D)
    )
    private double northstar$modifyGravity(double constant) {
        return constant * level().northstar$gravityScale();
    }

}
