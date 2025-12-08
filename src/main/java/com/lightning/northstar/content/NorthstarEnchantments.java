package com.lightning.northstar.content;

import com.lightning.northstar.item.enchantments.FrostbiteEnchantment;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import static com.lightning.northstar.Northstar.REGISTRATE;

public class NorthstarEnchantments {

    public static final RegistryEntry<FrostbiteEnchantment> FROSTBITE = REGISTRATE
            .enchantment("frostbite", EnchantmentCategory.WEAPON, FrostbiteEnchantment::new)
            .addSlots(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND)
            .rarity(Rarity.VERY_RARE)
            .register();

    public static void register() {}

}
