package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;


public record NorthstarArmorTiers(
        String name,
        int durability,
        int[] protection,
        int enchantability,
        SoundEvent equipSound,
        float toughness,
        float knockbackResistance,
        Supplier<Ingredient> repairMaterial
) implements ArmorMaterial {

    private static final int[] DURABILITY_PER_SLOT = new int[] { 11, 16, 15, 13 };

    @Override
    public int getDurabilityForType(ArmorItem.@NotNull Type type) {
        return DURABILITY_PER_SLOT[type.ordinal()] * durability;
    }

    @Override
    public int getDefenseForType(ArmorItem.@NotNull Type type) {
        return protection[type.ordinal()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public @NotNull SoundEvent getEquipSound() {
        return equipSound();
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return repairMaterial.get();
    }

    @Override
    public @NotNull String getName() {
        return Northstar.MOD_ID + ":" + name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    public static final ArmorMaterial MARTIAN_STEEL_ARMOR = new NorthstarArmorTiers(
            "martian_steel_armor",
            30,
            new int[] { 3, 8, 6, 3 },
            25,
            SoundEvents.ARMOR_EQUIP_IRON,
            2.5f,
            0.05f,
            () -> Ingredient.of(NorthstarItems.MARTIAN_STEEL_INGOT.get())
    );

    public static final ArmorMaterial IRON_SPACE_SUIT = new NorthstarArmorTiers(
            "iron_space_suit",
            20,
            new int[] { 2, 7, 5, 2 },
            25,
            SoundEvents.ARMOR_EQUIP_IRON,
            0.5f,
            0,
            () -> Ingredient.of(Items.IRON_INGOT)
    );

    public static final ArmorMaterial MARTIAN_STEEL_SPACE_SUIT = new NorthstarArmorTiers(
            "martian_steel_space_suit",
            35,
            new int[] { 3, 8, 6, 3 },
            25,
            SoundEvents.ARMOR_EQUIP_IRON,
            2.5f,
            0.05f,
            () -> Ingredient.of(NorthstarItems.MARTIAN_STEEL_INGOT.get())
    );

}
