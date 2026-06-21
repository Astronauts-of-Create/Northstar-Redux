package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.item.enchantments.FrostbiteEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NorthstarEnchantments {

    public static final ResourceKey<Enchantment>
            FROSTBITE = key("frostbite");

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, Northstar.asResource(name));
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> itemHolderGetter = context.lookup(Registries.ITEM);

        register(
                context,
                FROSTBITE,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemHolderGetter.getOrThrow(ItemTags.SWORDS),
                                10,
                                3,
                                Enchantment.dynamicCost(0, 10),
                                Enchantment.dynamicCost(25, 10),
                                1,
                                EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND
                        )
                ).exclusiveWith(
                        HolderSet.direct(context.lookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.FIRE_ASPECT))
                ).withEffect(
                        EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        new FrostbiteEffect()
                )
        );
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }

    public static void register(IEventBus bus) {
        DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> register = DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Northstar.MOD_ID);
        register.register("frostbite", () -> FrostbiteEffect.CODEC);
        register.register(bus);
    }

}
