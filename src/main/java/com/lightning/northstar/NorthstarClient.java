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
import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.content.NorthstarTextures;
import com.lightning.northstar.content.world.NorthstarDimensionEffects;
import com.lightning.northstar.contraption.rocket.LaunchStatus;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.lightning.northstar.particle.NorthstarParticles;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Northstar.MOD_ID, value = Dist.CLIENT)
public class NorthstarClient {

    public static NorthstarAtlas PLANET_ATLAS;
    private static float atmosphereBlend;

    public static void clientInit(IEventBus eventBus) {
        eventBus.addListener(NorthstarParticles::registerFactories);

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
    public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.AIR_LEVEL, Northstar.asResource("remaining_oxygen"), RemainingOxygenOverlay.INSTANCE);
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!NorthstarItemTags.OXYGEN_SOURCES.matches(stack)) {
            return;
        }
        MutableComponent tooltip = Component.translatable("northstar.gui.tooltip.oxygen")
                .append(LangNumberFormat.format(stack.getOrDefault(NorthstarDataComponents.OXYGEN, 0)))
                .append(NorthstarLang.MB.component())
                .withStyle(ChatFormatting.GRAY);

        event.getToolTip().add(1, tooltip);
    }

    @SubscribeEvent
    public static void onRenderTick(RenderFrameEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        // shouldRenderLevel is needed when leaving a singleplayer world, the level is still non-null
        // but Forge configs are unloaded causing a crash when accessing them.
        if (level != null && minecraft.player != null && minecraft.northstar$shouldRenderLevel()) {
            float partialTick = event.getPartialTick().getGameTimeDeltaTicks();
            NorthstarLevel.CLIENT_TRACKER.tick(level, partialTick);

            atmosphereBlend = NorthstarConfigs.server().calculateAtmosphereBlend(level, minecraft.player.getEyePosition(partialTick).y);
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

    /** @return The current atmosphere blending factor for the player's eye position */
    public static float getAtmosphereBlend() {
        return atmosphereBlend;
    }

}
