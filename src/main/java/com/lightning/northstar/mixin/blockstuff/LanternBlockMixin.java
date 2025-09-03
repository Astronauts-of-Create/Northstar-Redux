package com.lightning.northstar.mixin.blockstuff;

import com.lightning.northstar.block.ExtinguishedLanternBlock;
import com.lightning.northstar.content.NorthstarTechBlocks;
import com.lightning.northstar.world.NorthstarOxygen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LanternBlock.class)
public class LanternBlockMixin {
    @Shadow
    @Final
    public static BooleanProperty HANGING;
    @Shadow
    @Final
    public static BooleanProperty WATERLOGGED;


    @Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true)
    public void getStateForPlacement(BlockPlaceContext pContext, CallbackInfoReturnable<BlockState> info) {
        try {
            if (pContext.getItemInHand().getItem() == Blocks.LANTERN.asItem()) {
                boolean hanging = false;
                BlockState blockstate = Blocks.LANTERN.defaultBlockState().setValue(HANGING, true);
                if (blockstate.canSurvive(pContext.getLevel(), pContext.getClickedPos())) {
                    hanging = true;
                }

                FluidState fluidState = pContext.getLevel().getFluidState(pContext.getClickedPos());

                if (!NorthstarOxygen.hasOxygen(pContext.getLevel(), pContext.getClickedPos())) {
                    pContext.getLevel().playSound(null, pContext.getClickedPos(), SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1, 0);
                    info.setReturnValue(NorthstarTechBlocks.EXTINGUISHED_LANTERN.get().defaultBlockState()
                            .setValue(ExtinguishedLanternBlock.HANGING, hanging).setValue(ExtinguishedLanternBlock.WATERLOGGED, fluidState.is(Fluids.WATER)));

                }
            }
        } catch (Exception e) {
            //oops
        }
    }

    @Inject(method = "updateShape", at = @At("TAIL"), cancellable = true)
    public void updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
                            LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos, CallbackInfoReturnable<BlockState> info) {
        try {
            if (pState.getBlock() == Blocks.LANTERN) {
                if (!NorthstarOxygen.hasOxygen(((Level) pLevel), pCurrentPos)) {
                    pLevel.playSound(null, pCurrentPos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1, 0);
                    info.setReturnValue(NorthstarTechBlocks.EXTINGUISHED_LANTERN.get().defaultBlockState()
                            .setValue(ExtinguishedLanternBlock.HANGING, pState.getValue(HANGING)).setValue(ExtinguishedLanternBlock.WATERLOGGED, pState.getValue(WATERLOGGED)));
                }
            }
        } catch (Exception e) {
            //oops
        }

    }

}
