package com.lightning.northstar.world.sealer.transform;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface TransformProviderFactory {

    TransformProvider createTransformProvider(Level level, BlockPos pos);

}
