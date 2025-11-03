package com.lightning.northstar.mixin.block;

import com.lightning.northstar.block.simple.ExtinguishedTorchBlock;
import com.lightning.northstar.block.simple.ExtinguishedTorchWallBlock;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.mixin.accessor.FlowingFluidAccessor;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.sealer.SealReactiveBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(WallTorchBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WallTorchBlockMixin extends Block implements LiquidBlockContainer, SealReactiveBlock {

    @Shadow
    @Final
    public static DirectionProperty FACING;

    public WallTorchBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    public void northstar$updatePlacementShape(BlockPlaceContext context, CallbackInfoReturnable<BlockState> info) {
        BlockState state = info.getReturnValue();
        if (state != null &&
                state.getBlock() == Blocks.WALL_TORCH &&
                !NorthstarOxygen.hasOxygen(context.getLevel(), context.getClickedPos())) {
            info.setReturnValue(northstar$copyStateExtinguished(state));
        }
    }

    @Inject(method = "updateShape", at = @At("TAIL"), cancellable = true)
    public void northstar$updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
                                      BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> info) {
        if (state.getBlock() == Blocks.WALL_TORCH && level instanceof Level l && !NorthstarOxygen.hasOxygen(l, pos)) {
            info.setReturnValue(northstar$copyStateExtinguished(state));
        }
    }

    @Override
    public void northstar$onSealUpdated(Level level, BlockPos pos, BlockState state, SealingMode mode) {
        if (mode == SealingMode.OXYGEN && state.getBlock() == Blocks.WALL_TORCH && !NorthstarOxygen.hasOxygen(level, pos)) {
            level.setBlock(pos, northstar$copyStateExtinguished(state), Block.UPDATE_ALL);
        }
    }

    @Unique
    private BlockState northstar$copyStateExtinguished(BlockState state) {
        return NorthstarBlocks.EXTINGUISHED_TORCH_WALL
                .get()
                .defaultBlockState()
                .setValue(ExtinguishedTorchWallBlock.FACING, state.getValue(FACING));
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return true;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (fluidState.getType() != Fluids.WATER || state.getBlock() != Blocks.WALL_TORCH) {
            if (fluidState.getType() instanceof FlowingFluidAccessor flowing)
                flowing.northstar$beforeDestroyingBlock(level, pos, state);
            level.setBlock(pos, fluidState.createLegacyBlock(), 3);
            return true;
        }

        level.setBlock(pos, northstar$copyStateExtinguished(state).setValue(ExtinguishedTorchBlock.WATERLOGGED, true), Block.UPDATE_ALL);
        return true;
    }

}
