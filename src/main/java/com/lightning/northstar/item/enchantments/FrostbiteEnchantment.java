package com.lightning.northstar.item.enchantments;

import com.lightning.northstar.particle.NorthstarParticles;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.FireAspectEnchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
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
    public boolean checkCompatibility(Enchantment enchantment) {
        return !(enchantment instanceof FireAspectEnchantment) && super.checkCompatibility(enchantment);
    }

    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {
        target.setTicksFrozen((level * 80) + 150);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> applyFrostParticles(target));
    }

    @OnlyIn(Dist.CLIENT)
    private void applyFrostParticles(Entity entity) {
        Minecraft.getInstance().particleEngine.createTrackingEmitter(entity, NorthstarParticles.SNOWFLAKE.get());
    }

}
