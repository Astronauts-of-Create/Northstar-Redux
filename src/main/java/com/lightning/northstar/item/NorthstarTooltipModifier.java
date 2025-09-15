package com.lightning.northstar.item;

import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.util.NorthstarLang;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class NorthstarTooltipModifier implements TooltipModifier {

    @Override
    public void modify(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!NorthstarItemTags.OXYGEN_SOURCES.matches(stack)) {
            return;
        }
        Integer oxygen = stack.get(NorthstarDataComponents.OXYGEN);
        MutableComponent tooltip = Component.translatable("northstar.gui.tooltip.oxygen")
                .append(LangNumberFormat.format(oxygen == null ? 0 : oxygen))
                .append(NorthstarLang.MB.component())
                .withStyle(ChatFormatting.GRAY);

        event.getToolTip().add(1, tooltip);
    }

}
