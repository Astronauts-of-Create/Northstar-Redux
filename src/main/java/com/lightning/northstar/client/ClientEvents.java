package com.lightning.northstar.client;

import com.lightning.northstar.contraptions.RocketContraptionEntity;
import com.lightning.northstar.item.client.SpaceSuitFirstPersonRenderer;
import com.simibubi.create.content.trains.CameraDistanceModifier;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Pre event) {
        if (!isGameActive())
            return;
        SpaceSuitFirstPersonRenderer.clientTick();
    }

    @SubscribeEvent
    public static void onMount(EntityMountEvent event) {
        if (event.getEntityMounting() != Minecraft.getInstance().player)
            return;

        if (event.isDismounting()) {
            CameraDistanceModifier.reset();
            return;
        }

        if (!event.isMounting() || !(event.getEntityBeingMounted() instanceof RocketContraptionEntity rocket)) {
            return;
        }

        CameraDistanceModifier.zoomOut(6);
    }

    protected static boolean isGameActive() {
        return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
    }

}
