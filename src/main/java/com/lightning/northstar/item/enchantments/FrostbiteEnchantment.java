package com.lightning.northstar.item.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.FireAspectEnchantment;
import org.jetbrains.annotations.NotNull;

public class FrostbiteEnchantment extends Enchantment {

    public FrostbiteEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slot) {
        super(rarity, category, slot);
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return enchantmentLevel * 10;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return getMinCost(enchantmentLevel) + 25;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return (stack.getItem() instanceof SwordItem || stack.is(Items.STICK)) && !stack.getAllEnchantments().containsKey(Enchantments.FIRE_ASPECT);
    }

    @Override
    public boolean checkCompatibility(@NotNull Enchantment enchantment) {
        return !(enchantment instanceof FireAspectEnchantment) && super.checkCompatibility(enchantment);
    }

}
