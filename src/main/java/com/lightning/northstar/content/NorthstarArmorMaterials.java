package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class NorthstarArmorMaterials {

    private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, Northstar.MOD_ID);

    public static final Holder<ArmorMaterial> MARTIAN_STEEL_ARMOR = register(
            "martian_steel_armor",
            new int[] { 3, 8, 6, 3, 10 },
            25,
            SoundEvents.ARMOR_EQUIP_IRON,
            2.5f,
            0.05f,
            () -> Ingredient.of(NorthstarItems.MARTIAN_STEEL.get()),
            List.of(new ArmorMaterial.Layer(Northstar.asResource("martian_steel_armor"))));

    public static final Holder<ArmorMaterial> IRON_SPACE_SUIT = register(
            "iron_space_suit",
            new int[] { 2, 7, 5, 2, 8 },
            25,
            SoundEvents.ARMOR_EQUIP_IRON,
            0.5f,
            0.0f,
            () -> Ingredient.of(Items.IRON_INGOT),
            List.of(new ArmorMaterial.Layer(Northstar.asResource("iron_space_suit"))));

    public static final Holder<ArmorMaterial> MARTIAN_STEEL_SPACE_SUIT = register(
            "martian_steel_space_suit",
            new int[] { 3, 8, 6, 3, 10 },
            25,
            SoundEvents.ARMOR_EQUIP_IRON,
            2.5f,
            0.05f,
            () -> Ingredient.of(NorthstarItems.MARTIAN_STEEL.get()),
            List.of(new ArmorMaterial.Layer(Northstar.asResource("martian_steel_space_suit"))));

    private static Holder<ArmorMaterial> register(
            String name,
            int[] defense,
            int enchantmentValue,
            Holder<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient,
            List<ArmorMaterial.Layer> layers) {
        EnumMap<ArmorItem.Type, Integer> defenseMap = new EnumMap<>(Map.of(
                ArmorItem.Type.HELMET, defense[0],
                ArmorItem.Type.CHESTPLATE, defense[1],
                ArmorItem.Type.LEGGINGS, defense[2],
                ArmorItem.Type.BOOTS, defense[3],
                ArmorItem.Type.BODY, defense[4]
        ));
        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(defenseMap, enchantmentValue, equipSound, repairIngredient, layers, toughness, knockbackResistance));
    }

    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }

}
