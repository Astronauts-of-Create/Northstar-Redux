package com.lightning.northstar.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FlowingFluid.class)
public interface NorthstarFlowingFluid {

    @Invoker("beforeDestroyingBlock")
    void northstar$beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state);

}
