package com.lightning.northstar.item;

import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.util.NorthstarLang;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
        MutableComponent tooltip = Component.translatable("northstar.gui.tooltip.oxygen")
                .append(LangNumberFormat.format(tag.getInt("Oxygen")))
                .append(NorthstarLang.MB.component())
                .withStyle(ChatFormatting.GRAY);

        event.getToolTip().add(1, tooltip);
    }

}
