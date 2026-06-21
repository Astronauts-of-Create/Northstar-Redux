package com.lightning.northstar.api.client;

import net.neoforged.neoforge.client.event.ViewportEvent;

public interface NorthstarDimensionEffectsExtension {

    default void northstar$onFrameStart() {
    }

    default void northstar$setupLight() {
    }

    default void northstar$setupFogColor(ViewportEvent.ComputeFogColor fog) {
    }

    default void northstar$setupFogRender(ViewportEvent.RenderFog fog) {
    }

}
