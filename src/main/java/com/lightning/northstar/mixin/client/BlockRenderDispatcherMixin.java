package com.lightning.northstar.mixin.client;

import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockRenderDispatcher.class)
public class BlockRenderDispatcherMixin {

    // I couldn't get satisfying enough results, so I prefer to leave the default fluid renderer for now.
    /*@WrapWithCondition(
            method = "renderLiquid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;tesselate(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"
            )
    )
    private boolean northstar$redirectFluidRenderer(LiquidBlockRenderer instance, BlockAndTintGetter level, BlockPos pos, VertexConsumer vc, BlockState block, FluidState fluid) {
        if (level instanceof NorthstarRenderChunkRegion r && r.northstar$level().northstar$isZeroGravity()) {
            ZeroGravityFluidRenderer.render(level, pos, vc, block, fluid, instance);
            return false;
        }
        return true;
    }*/

}
