package com.lightning.northstar;

import com.lightning.northstar.accessor.NorthstarLevel;
import com.lightning.northstar.api.client.NorthstarDimensionEffectsExtension;
import com.lightning.northstar.client.NorthstarAtlas;
import com.lightning.northstar.client.renderer.RemainingOxygenOverlay;
import com.lightning.northstar.client.renderer.armor.SpaceSuitLayerRenderer;
import com.lightning.northstar.client.renderer.effect.MarsEffects;
import com.lightning.northstar.client.renderer.effect.SpaceEffects;
import com.lightning.northstar.client.renderer.effect.VenusEffects;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.content.NorthstarTextures;
import com.lightning.northstar.content.world.NorthstarDimensionEffects;
import com.lightning.northstar.contraption.rocket.LaunchStatus;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.lightning.northstar.ponder.NorthstarPonderPlugin;
import com.lightning.northstar.util.NorthstarLang;
import net.createmod.catnip.lang.LangNumberFormat;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = Northstar.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class NorthstarClient {

    public static NorthstarAtlas PLANET_ATLAS;
    private static float atmosphereBlend;

    public static void clientInit(IEventBus modEventBus, IEventBus forgeEventBus) {
        PonderIndex.addPlugin(new NorthstarPonderPlugin());
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        event.registerReloadListener(PLANET_ATLAS = new NorthstarAtlas(textureManager, NorthstarTextures.PLANET_ATLAS, Northstar.asResource("planets")));
    }

    @SubscribeEvent
    public static void onRegisterDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(NorthstarDimensionEffects.SPACE, new SpaceEffects(true));
        event.register(NorthstarDimensionEffects.ORBIT, new SpaceEffects(false));
        event.register(NorthstarDimensionEffects.MARS, new MarsEffects());
        event.register(NorthstarDimensionEffects.VENUS, new VenusEffects());
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
        public static void onRenderTick(TickEvent.RenderTickEvent event) {
            if (event.phase == Phase.START) {
                Minecraft minecraft = Minecraft.getInstance();
                ClientLevel level = minecraft.level;
                // shouldRenderLevel is needed when leaving a singleplayer world, the level is still non-null
                // but Forge configs are unloaded causing a crash when accessing them.
                if (level != null && minecraft.player != null && minecraft.northstar$shouldRenderLevel()) {
                    NorthstarLevel.CLIENT_TRACKER.tick(level, event.renderTickTime);

                    atmosphereBlend = NorthstarConfigs.server().calculateAtmosphereBlend(level, minecraft.player.getEyePosition(minecraft.getPartialTick()).y);
                }
            }
        }

        @SubscribeEvent
        public static void onFogColor(ViewportEvent.ComputeFogColor event) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null && level.effects() instanceof NorthstarDimensionEffectsExtension effects) {
                effects.northstar$setupFogColor(event);
            }

            float alpha = 1 - atmosphereBlend;
            event.setRed(event.getRed() * alpha);
            event.setGreen(event.getGreen() * alpha);
            event.setBlue(event.getBlue() * alpha);
        }

        @SubscribeEvent
        public static void onFogSetup(ViewportEvent.RenderFog event) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null && level.effects() instanceof NorthstarDimensionEffectsExtension effects) {
                effects.northstar$setupFogRender(event);
            }

            if (event.getType() == FogType.NONE && atmosphereBlend > 0) {
                float extent = atmosphereBlend * Minecraft.getInstance().gameRenderer.getRenderDistance();
                event.setNearPlaneDistance(event.getNearPlaneDistance() + extent);
                event.setFarPlaneDistance(event.getFarPlaneDistance() + extent);
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onMountEntity(EntityMountEvent event) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null &&
                player.equals(event.getEntityMounting()) &&
                event.isDismounting() &&
                event.getEntityBeingMounted() instanceof RocketContraptionEntity rocket &&
                rocket.getStatus() != LaunchStatus.WAITING) {
                event.setCanceled(true);
            }
        }
    }

    /** @return The current atmosphere blending factor for the player's eye position */
    public static float getAtmosphereBlend() {
        return atmosphereBlend;
    }

}
