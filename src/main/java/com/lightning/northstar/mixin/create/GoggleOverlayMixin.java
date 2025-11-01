package com.lightning.northstar.mixin.create;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.world.oxygen.OxygenConsumer;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.equipment.goggles.GoggleOverlayRenderer;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GoggleOverlayRenderer.class)
public class GoggleOverlayMixin {

    @Inject(method = "renderOverlay",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/List;isEmpty()Z",
                    shift = At.Shift.BEFORE,
                    ordinal = 2),
            remap = false)
    private static void northstar$addOverlay(GuiGraphics graphics, DeltaTracker tracker, CallbackInfo ci,
                                             @Local ClientLevel level,
                                             @Local BlockPos pos,
                                             @Local List<Component> tooltip) {
        Block block = level.getBlockState(pos).getBlock();

        OxygenConsumer consumer = block instanceof OxygenConsumer cons ? cons : OxygenConsumer.REGISTRY.get(block);
        if (consumer != null && !level.northstar$oxygen().hasOxygen() && level.northstar$oxygen().hasOxygen(pos)) {
            float consumption = consumer.northstar$getOxygenConsumption(level, pos, NorthstarConfigs.server().oxygenSealerBlockActiveDrain.getF());
            if (consumption != 0 && Float.isFinite(consumption)) {
                NorthstarLang.translate("gui.oxygen_sealer.oxygen_usage")
                        .style(ChatFormatting.GRAY)
                        .forGoggles(tooltip);

                CreateLang.number(consumption)
                        .add(NorthstarLang.MB_PER_TICK)
                        .style(ChatFormatting.GOLD)
                        .forGoggles(tooltip, 1);
            }
        }
    }

}
