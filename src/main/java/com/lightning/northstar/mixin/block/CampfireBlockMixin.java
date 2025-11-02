package com.lightning.northstar.mixin.block;

import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.sealer.SealReactiveBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(CampfireBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class CampfireBlockMixin extends BaseEntityBlock implements SealReactiveBlock {

    public CampfireBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    public void northstar$updatePlacementLit(BlockPlaceContext context, CallbackInfoReturnable<BlockState> info) {
        BlockState state = info.getReturnValue() == null ? defaultBlockState() : info.getReturnValue();

        if (!NorthstarOxygen.hasOxygen(context.getLevel(), context.getClickedPos())) {
            state = state.setValue(CampfireBlock.LIT, false);
        }

        info.setReturnValue(state);
    }

    @Inject(method = "updateShape", at = @At("TAIL"), cancellable = true)
    public void northstar$updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
                                      BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (level instanceof Level l && !NorthstarOxygen.hasOxygen(l, pos)) {
            cir.setReturnValue(defaultBlockState().setValue(CampfireBlock.LIT, false));
        }
    }

    @Override
    public void northstar$onSealUpdated(Level level, BlockPos pos, BlockState state, SealingMode mode) {
        if (mode == SealingMode.OXYGEN && !NorthstarOxygen.hasOxygen(level, pos)) {
            level.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, false));
        }
    }

}
