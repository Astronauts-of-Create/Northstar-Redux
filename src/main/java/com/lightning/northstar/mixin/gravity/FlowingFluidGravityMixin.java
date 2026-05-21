package com.lightning.northstar.mixin.gravity;

import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidGravityMixin {

    @Inject(method = "spread", at = @At("HEAD"), cancellable = true)
    private void northstar$spread(Level level, BlockPos pos, FluidState state, CallbackInfo ci) {
        if (!level.northstar$isZeroGravity() || state.isEmpty())
            return;
        ci.cancel();

        MutableBlockPos otherPos = new MutableBlockPos();
        BlockState blockstate = level.getBlockState(pos);

        for (Direction direction : Iterate.directions) {
            otherPos.setWithOffset(pos, direction);

            BlockState otherBlockstate = level.getBlockState(otherPos);
            FluidState fluidstate = getNewLiquid(level, otherPos, otherBlockstate);

            if (canSpreadTo(level, pos, blockstate, direction, otherPos, otherBlockstate, level.getFluidState(otherPos), fluidstate.getType())) {
                spreadTo(level, otherPos, otherBlockstate, direction, fluidstate);
            }
        }
    }

    @Inject(method = "getNewLiquid", at = @At("HEAD"), cancellable = true)
    private void northstar$getNewLiquid(Level level, BlockPos pos, BlockState blockState, CallbackInfoReturnable<FluidState> cir) {
        if (!level.northstar$isZeroGravity())
            return;

        FlowingFluid self = (FlowingFluid) (Object) this;

        int highest = 0;
        int sources = 0;
        MutableBlockPos other = new MutableBlockPos();

        for (Direction direction : Iterate.directions) {
            other.setWithOffset(pos, direction);

            BlockState blockstate = level.getBlockState(other);
            FluidState fluidstate = blockstate.getFluidState();
            if (fluidstate.getType().isSame(self) && canPassThroughWall(direction, level, pos, blockState, other, blockstate)) {
                if (fluidstate.isSource() && ForgeEventFactory.canCreateFluidSource(level, other, blockstate, fluidstate.canConvertToSource(level, other))) {
                    sources++;
                }

                highest = Math.max(highest, fluidstate.getAmount());
            }
        }

        if (sources >= 2) {
            BlockState otherBlockstate = level.getBlockState(other.setWithOffset(pos, Direction.DOWN));
            FluidState otherFluidstate = otherBlockstate.getFluidState();
            if (otherBlockstate.isSolid() || isSourceBlockOfThisType(otherFluidstate)) {
                cir.setReturnValue(getSource(false));
                return;
            }
        }

        int newLevel = highest - getDropOff(level);
        cir.setReturnValue(newLevel <= 0 ? Fluids.EMPTY.defaultFluidState() : getFlowing(newLevel, false));
    }

    @Shadow
    protected abstract boolean canSpreadTo(BlockGetter level, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fluid);

    @Shadow
    protected abstract void spreadTo(LevelAccessor level, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState);

    @Shadow
    protected abstract boolean canPassThroughWall(Direction direction, BlockGetter level, BlockPos pos, BlockState state, BlockPos spreadPos, BlockState spreadState);

    @Shadow
    protected abstract boolean isSourceBlockOfThisType(FluidState state);

    @Shadow
    protected abstract FluidState getNewLiquid(Level level, BlockPos pos, BlockState blockState);

    @Shadow
    protected abstract int getDropOff(LevelReader level);

    @Shadow
    public abstract FluidState getSource(boolean falling);

    @Shadow
    public abstract FluidState getFlowing(int level, boolean falling);

}
