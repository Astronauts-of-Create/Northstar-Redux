package com.lightning.northstar.mixin.block;

import com.google.common.cache.CacheBuilder;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Block.class)
public class BlockMixin {

    // Sealers make pretty extensive use of this cache through Block#isFaceFull(VoxelShape, Direction), making the cache
    // unbounded ensures better performance as recomputing the values can be way more expensive than keeping them cached
    // forever both CPU and memory wise.
    @Redirect(method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lcom/google/common/cache/CacheBuilder;maximumSize(J)Lcom/google/common/cache/CacheBuilder;",
                    remap = false))
    private static <K, V> CacheBuilder<K, V> northstar$removeShapeCacheSize(CacheBuilder<K, V> instance, long maximumSize) {
        return instance;
    }

}
