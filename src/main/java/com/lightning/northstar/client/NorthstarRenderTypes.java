package com.lightning.northstar.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class NorthstarRenderTypes extends RenderStateShard {

    // https://github.com/Creators-of-Create/Create/blob/mc1.21.1/dev/src/main/java/com/simibubi/create/foundation/render/RenderTypes.java#L83
    private static final Function<ResourceLocation, RenderType> CHAIN = Util.memoize(texture -> RenderType.create(
            "chain_conveyor_chain", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256,
            false, true, RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_CUTOUT_MIPPED_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, true))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false)));

    public static RenderType chain(ResourceLocation location) {
        return CHAIN.apply(location);
    }

    private NorthstarRenderTypes() {
        super(null, null, null);
    }

}
