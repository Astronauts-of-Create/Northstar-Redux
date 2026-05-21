package com.lightning.northstar.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.event.ViewportEvent;

public interface NorthstarDimensionEffectsExtension {

    default void northstar$onFrameStart() {
    }

    default void northstar$setupLight(PoseStack pose) {
    }

    default void northstar$setupFogColor(ViewportEvent.ComputeFogColor fog) {
    }

    default void northstar$setupFogRender(ViewportEvent.RenderFog fog) {
    }

}
