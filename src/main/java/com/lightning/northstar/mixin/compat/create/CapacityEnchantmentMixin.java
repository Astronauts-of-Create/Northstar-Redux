package com.lightning.northstar.mixin.compat.create;

import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.equipment.armor.CapacityEnchantment;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CapacityEnchantment.class, remap = false)
public class CapacityEnchantmentMixin {

    @ModifyReturnValue(
            method = "canApplyAtEnchantingTable",
            at = @At("RETURN")
    )
    private boolean northstar$canApplyEnchantment(boolean original, @Local(argsOnly = true) ItemStack stack) {
        return original || NorthstarItemTags.OXYGEN_SOURCES.matches(stack);
    }

}
