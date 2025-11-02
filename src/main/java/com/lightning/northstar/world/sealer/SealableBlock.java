package com.lightning.northstar.world.sealer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface SealableBlock {

    boolean northstar$isFaceSealed(BlockGetter level, BlockPos pos, BlockState state, Direction direction, boolean source, SealingMode mode);

}
