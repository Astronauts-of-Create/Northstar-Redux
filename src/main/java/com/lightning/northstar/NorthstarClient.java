package com.lightning.northstar;

import com.lightning.northstar.block.tech.rocket_controls.RocketControlsClientHandler;
import com.lightning.northstar.client.renderer.armor.SpaceSuitLayerRenderer;
import com.lightning.northstar.client.renderer.effect.MarsEffects;
import com.lightning.northstar.client.renderer.effect.SpaceEffects;
import com.lightning.northstar.client.renderer.effect.VenusEffects;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.item.armor.RemainingOxygenOverlay;
import com.lightning.northstar.particle.NorthstarParticles;
import com.lightning.northstar.ponder.NorthstarPonderPlugin;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.world.dimension.NorthstarDimensions;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = Northstar.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class NorthstarClient {

    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(NorthstarParticles::registerFactories);

        NorthstarPonderPlugin.register();
    }

    @SubscribeEvent
    public static void onRegisterDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(NorthstarDimensions.SPACE_EFFECTS, new SpaceEffects());
        event.register(NorthstarDimensions.MARS_EFFECTS, new MarsEffects());
        event.register(NorthstarDimensions.VENUS_EFFECTS, new VenusEffects());
    }

    @SubscribeEvent
    public static void addEntityRendererLayers(EntityRenderersEvent.AddLayers event) {
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

        SpaceSuitLayerRenderer.registerOnAll(dispatcher);
    }

    @SubscribeEvent
    public static void registerRenderers(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(NorthstarFluids.SULFURIC_ACID.get().getSource(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(NorthstarFluids.SULFURIC_ACID.get(), RenderType.translucent());

        ItemBlockRenderTypes.setRenderLayer(NorthstarFluids.LIQUID_HYDROGEN.get().getSource(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(NorthstarFluids.LIQUID_HYDROGEN.get(), RenderType.translucent());

        ItemBlockRenderTypes.setRenderLayer(NorthstarFluids.LIQUID_OXYGEN.get().getSource(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(NorthstarFluids.LIQUID_OXYGEN.get(), RenderType.translucent());

        ItemBlockRenderTypes.setRenderLayer(NorthstarFluids.METHANE.get().getSource(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(NorthstarFluids.METHANE.get(), RenderType.translucent());
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.AIR_LEVEL.id(), "remaining_oxygen", RemainingOxygenOverlay.INSTANCE);
    }

    @EventBusSubscriber(modid = Northstar.MOD_ID, value = Dist.CLIENT)
    public static class ForgeBusEvents {
        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
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

        @SubscribeEvent
        public static void onTick(ClientTickEvent event) {
            if (event.phase == Phase.START) {
                RocketControlsClientHandler.tick();
            }
        }
    }

}
