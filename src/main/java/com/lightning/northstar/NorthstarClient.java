package com.lightning.northstar;

import com.lightning.northstar.block.tech.rocket_controls.RocketControlsClientHandler;
import com.lightning.northstar.client.renderer.armor.SpaceSuitLayerRenderer;
import com.lightning.northstar.client.renderer.effect.MarsEffects;
import com.lightning.northstar.client.renderer.effect.VenusEffects;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.item.armor.RemainingOxygenOverlay;
import com.lightning.northstar.particle.NorthstarParticles;
import com.lightning.northstar.ponder.NorthstarPonderPlugin;
import com.lightning.northstar.world.dimension.NorthstarDimensions;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = Northstar.MOD_ID, value = Dist.CLIENT)
public class NorthstarClient {

    public static void onCtorClient(IEventBus modEventBus) {
        modEventBus.addListener(NorthstarParticles::registerFactories);

        PonderIndex.addPlugin(new NorthstarPonderPlugin());
    }

    @SubscribeEvent
    public static void onRegisterDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
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
    public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.AIR_LEVEL, Northstar.asResource("remaining_oxygen"), RemainingOxygenOverlay.INSTANCE);
    }

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Pre event) {
        RocketControlsClientHandler.tick();
    }

}
