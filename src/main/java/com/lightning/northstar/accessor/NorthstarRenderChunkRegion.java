package com.lightning.northstar.accessor;

import net.minecraft.world.level.Level;

public interface NorthstarRenderChunkRegion {

    default Level northstar$level() {
        throw new MissingMixinException();
    }

}
