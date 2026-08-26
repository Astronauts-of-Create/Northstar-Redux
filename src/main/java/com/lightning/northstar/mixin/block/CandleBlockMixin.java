package com.lightning.northstar.mixin.block;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarBlockStateProperties;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.sealer.SealReactiveBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(CandleBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class CandleBlockMixin extends AbstractCandleBlock implements SealReactiveBlock {

    protected CandleBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void northstar$init(Properties properties, CallbackInfo ci) {
        registerDefaultState(defaultBlockState().setValue(NorthstarBlockStateProperties.OXYGEN_DEPRIVED, false));
    }

    @Inject(
            method = "createBlockStateDefinition",
            at = @At("TAIL")
    )
    private void northstar$createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(NorthstarBlockStateProperties.OXYGEN_DEPRIVED);
    }

    @Nullable
    @ModifyReturnValue(
            method = "getStateForPlacement",
            at = @At("RETURN")
    )
    public BlockState northstar$updatePlacementLit(
            @Nullable BlockState state,
            @Local(argsOnly = true) BlockPlaceContext context
    ) {
        if (state == null || !state.hasProperty(LIT) || NorthstarOxygen.hasOxygen(context.getLevel(), context.getClickedPos()))
            return state;
        return state.setValue(LIT, false)
                .setValue(NorthstarBlockStateProperties.OXYGEN_DEPRIVED, true);
    }

    @ModifyReturnValue(
            method = "updateShape",
            at = @At("RETURN")
    )
    public BlockState northstar$updateShape(
            BlockState state,
            @Local(argsOnly = true) LevelAccessor level,
            @Local(argsOnly = true, ordinal = 0) BlockPos pos
    ) {
        if (state.getBlock() instanceof CandleBlock && level instanceof Level l) {
            BlockState newState = northstar$updateShape(l, pos, state);
            if (newState != null) {
                return newState;
            }
        }
        return state;
    }

    @Override
    public void northstar$onSealUpdated(Level level, BlockPos pos, BlockState state, SealingMode mode) {
        if (mode == SealingMode.OXYGEN) {
            BlockState newState = northstar$updateShape(level, pos, state);
            if (newState != null) {
                level.setBlockAndUpdate(pos, state);
            }
        }
    }

    @Unique
    @Nullable
    private BlockState northstar$updateShape(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(LIT)) {
            if (!NorthstarOxygen.hasOxygen(level, pos)) {
                level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1, 1);
                return state.setValue(LIT, false)
                        .setValue(NorthstarBlockStateProperties.OXYGEN_DEPRIVED, true);
            }
        } else {
            if (state.getValue(NorthstarBlockStateProperties.OXYGEN_DEPRIVED) &&
                NorthstarConfigs.server().relitExtinguishedBlocks.get() &&
                NorthstarOxygen.hasOxygen(level, pos)) {
                return state.setValue(LIT, true)
                        .setValue(NorthstarBlockStateProperties.OXYGEN_DEPRIVED, false);
            }
        }
        return null;
    }

}
