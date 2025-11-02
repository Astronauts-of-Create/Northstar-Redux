package com.lightning.northstar.world.sealer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface SealReactiveBlock {

    void northstar$onSealUpdated(Level level, BlockPos pos, BlockState state, SealingMode mode);

}
