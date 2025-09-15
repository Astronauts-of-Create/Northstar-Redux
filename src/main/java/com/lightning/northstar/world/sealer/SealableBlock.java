package com.lightning.northstar.world.sealer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;

public interface SealableBlock {

    boolean isFaceSealed(BlockGetter level, BlockPos pos, Direction direction, boolean source);

}
