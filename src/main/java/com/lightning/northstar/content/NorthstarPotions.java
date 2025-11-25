package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NorthstarPotions {

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, Northstar.MOD_ID);

    public static final DeferredHolder<Potion, Potion> ENHANCED_STRENGTH = POTIONS.register("enhanced_strength", () -> new Potion("strength", new MobEffectInstance(MobEffects.DAMAGE_BOOST, 900, 2)));
    public static final DeferredHolder<Potion, Potion> ENHANCED_HEALING = POTIONS.register("enhanced_healing", () -> new Potion("healing", new MobEffectInstance(MobEffects.HEAL, 1, 2)));
    public static final DeferredHolder<Potion, Potion> ENHANCED_REGENERATION = POTIONS.register("enhanced_regeneration", () -> new Potion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 225, 2)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }

}
