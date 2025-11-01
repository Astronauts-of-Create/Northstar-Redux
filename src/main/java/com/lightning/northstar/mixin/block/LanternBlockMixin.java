package com.lightning.northstar.mixin.block;

import com.lightning.northstar.block.simple.ExtinguishedLanternBlock;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.sealer.SealReactiveBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(LanternBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LanternBlockMixin extends Block implements SealReactiveBlock {

    @Shadow
    @Final
    public static BooleanProperty HANGING;
    @Shadow
    @Final
    public static BooleanProperty WATERLOGGED;

    public LanternBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    public void northstar$updatePlacementShape(BlockPlaceContext context, CallbackInfoReturnable<BlockState> info) {
        BlockState state = info.getReturnValue() == null ? defaultBlockState() : info.getReturnValue();
        if (state.getBlock() == Blocks.LANTERN && !NorthstarOxygen.hasOxygen(context.getLevel(), context.getClickedPos())) {
            info.setReturnValue(northstar$copyStateExtinguished(state));
        }
    }

    @Inject(method = "updateShape", at = @At("TAIL"), cancellable = true)
    public void northstar$updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
                                      BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> info) {
        if (state.getBlock() == Blocks.LANTERN && level instanceof Level l && !NorthstarOxygen.hasOxygen(l, pos)) {
            info.setReturnValue(northstar$copyStateExtinguished(state));
        }
    }

    @Override
    public void northstar$onSealUpdated(Level level, BlockPos pos, BlockState state, SealingMode mode) {
        if (mode == SealingMode.OXYGEN && state.getBlock() == Blocks.LANTERN && !NorthstarOxygen.hasOxygen(level, pos)) {
            level.setBlock(pos, northstar$copyStateExtinguished(state), Block.UPDATE_ALL);
        }
    }

    @Unique
    private BlockState northstar$copyStateExtinguished(BlockState state) {
        return NorthstarBlocks.EXTINGUISHED_LANTERN
                .get()
                .defaultBlockState()
                .setValue(ExtinguishedLanternBlock.HANGING, state.getValue(HANGING))
                .setValue(ExtinguishedLanternBlock.WATERLOGGED, state.getValue(WATERLOGGED));
    }

}
