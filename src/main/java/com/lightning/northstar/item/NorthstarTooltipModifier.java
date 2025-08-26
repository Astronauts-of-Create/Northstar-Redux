package com.lightning.northstar.item;

import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class NorthstarTooltipModifier implements TooltipModifier {

    @Override
    public void modify(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        CompoundTag tag = stack.getTag();
        if (tag == null || !NorthstarItemTags.OXYGEN_SOURCES.matches(stack)) {
            return;
        }
        int oxygen = tag.contains("Oxygen", Tag.TAG_INT) ? tag.getInt("Oxygen") : 0;
        event.getToolTip().add(1, Component.literal("Oxygen: " + oxygen + "mb").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false)));
    }

}
