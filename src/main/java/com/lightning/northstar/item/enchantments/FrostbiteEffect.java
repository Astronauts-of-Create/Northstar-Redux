package com.lightning.northstar.item.enchantments;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public class FrostbiteEffect implements EnchantmentEntityEffect {

    public static final MapCodec<FrostbiteEffect> CODEC = MapCodec.unit(new FrostbiteEffect());

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        entity.setTicksFrozen(150 + enchantmentLevel * 80);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }

}
