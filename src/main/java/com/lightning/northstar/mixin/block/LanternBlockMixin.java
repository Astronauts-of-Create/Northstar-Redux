package com.lightning.northstar.mixin.block;

import com.lightning.northstar.block.simple.ExtinguishedLanternBlock;
import com.lightning.northstar.content.NorthstarTechBlocks;
import com.lightning.northstar.world.NorthstarOxygen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
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

@Mixin(LanternBlock.class)
public class LanternBlockMixin extends Block {

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
        if (!NorthstarOxygen.hasOxygen(context.getLevel(), context.getClickedPos())) {
            BlockState state = info.getReturnValue() == null ? defaultBlockState() : info.getReturnValue();

            info.setReturnValue(northstar$copyStateExtinguished(state));
        }
    }

    @Inject(method = "updateShape", at = @At("TAIL"), cancellable = true)
    public void northstar$updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
                                      BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> info) {
        if (level instanceof Level l && !NorthstarOxygen.hasOxygen(l, pos)) {
            info.setReturnValue(northstar$copyStateExtinguished(state));
        }
    }

    @Unique
    private BlockState northstar$copyStateExtinguished(BlockState state) {
        return NorthstarTechBlocks.EXTINGUISHED_LANTERN
                .get()
                .defaultBlockState()
                .setValue(ExtinguishedLanternBlock.HANGING, state.getValue(HANGING))
                .setValue(ExtinguishedLanternBlock.WATERLOGGED, state.getValue(WATERLOGGED));
    }

}
