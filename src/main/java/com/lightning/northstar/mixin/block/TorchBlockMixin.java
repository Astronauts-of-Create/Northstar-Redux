package com.lightning.northstar.mixin.block;

import com.lightning.northstar.block.simple.ExtinguishedTorchBlock;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.mixin.accessor.FlowingFluidAccessor;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.lightning.northstar.world.sealer.SealReactiveBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(TorchBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TorchBlockMixin extends Block implements LiquidBlockContainer, SealReactiveBlock {

    public TorchBlockMixin(Properties properties) {
        super(properties);
    }

    /*@ModifyReturnValue(method = "updateShape", at = @At("RETURN"))
    public BlockState northstar$updateShape(BlockState state,
                                            @Local(argsOnly = true) LevelAccessor level,
                                            @Local(argsOnly = true, ordinal = 0) BlockPos pos) {
        if (state.getBlock() == Blocks.TORCH && level instanceof Level l && !NorthstarOxygen.hasOxygen(l, pos)) {
            level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1, 1);
            return NorthstarBlocks.EXTINGUISHED_TORCH.get().defaultBlockState();
        }
        return state;
    }*/

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return true;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (fluidState.getType() != Fluids.WATER || state.getBlock() != Blocks.TORCH) {
            if (fluidState.getType() instanceof FlowingFluidAccessor flowing)
                flowing.northstar$beforeDestroyingBlock(level, pos, state);
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
