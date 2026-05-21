package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemEntity.class)
public abstract class ItemEntityGravityMixin extends Entity {

    public ItemEntityGravityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyConstant(
            method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V",
            constant = @Constant(doubleValue = 0.2, ordinal = 1)
    )
    private static double northstar$modifyInitialVerticalVelocity(double y, @Local(argsOnly = true) Level level) {
        if (level.northstar$isZeroGravity())
            return level.random.nextDouble() * 0.2 - 0.1;
        return y;
    }

    @ModifyConstant(
            method = "tick",
            constant = @Constant(doubleValue = -0.04D)
    )
    private double northstar$modifyGravity(double constant) {
        return constant * level().northstar$gravityScale();
    }

}
