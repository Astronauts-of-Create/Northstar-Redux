package com.lightning.northstar.mixin.client;

import com.lightning.northstar.accessor.NorthstarRenderChunkRegion;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderChunkRegion.class)
public class RenderChunkRegionMixin implements NorthstarRenderChunkRegion {

    @Shadow
    @Final
    protected Level level;

    @Override
    public Level northstar$level() {
        return level;
    }

}
