package com.lightning.northstar.mixin.block;

import com.lightning.northstar.block.simple.ExtinguishedTorchBlock;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.mixin.accessor.FlowingFluidAccessor;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.lightning.northstar.world.sealer.SealReactiveBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(TorchBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TorchBlockMixin extends Block implements LiquidBlockContainer, SealReactiveBlock {

    public TorchBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "updateShape", at = @At("TAIL"), cancellable = true)
    public void northstar$updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
                                      BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> info) {
        if (state.getBlock() == Blocks.TORCH && level instanceof Level l && !NorthstarOxygen.hasOxygen(l, pos)) {
            info.setReturnValue(NorthstarBlocks.EXTINGUISHED_TORCH.get().defaultBlockState());
        }
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return true;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (fluidState.getType() != Fluids.WATER) {
            if (fluidState.getType() instanceof FlowingFluidAccessor flowing)
                flowing.beforeDestroyingBlock(level, pos, state);
            level.setBlock(pos, fluidState.createLegacyBlock(), 3);
            return true;
        }

        level.setBlock(pos, NorthstarBlocks.EXTINGUISHED_TORCH
                        .getDefaultState()
                        .setValue(ExtinguishedTorchBlock.WATERLOGGED, true),
                Block.UPDATE_ALL);
        return true;
    }

    @Override
    public void northstar$onSealUpdated(Level level, BlockPos pos, BlockState state, SealingMode mode) {
        if (mode == SealingMode.OXYGEN && state.getBlock() == Blocks.TORCH && !NorthstarOxygen.hasOxygen(level, pos)) {
            level.setBlock(pos, NorthstarBlocks.EXTINGUISHED_TORCH.get().defaultBlockState(), Block.UPDATE_ALL);
        }
    }

}
