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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(CampfireBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class CampfireBlockMixin extends BaseEntityBlock implements SealReactiveBlock {

    public CampfireBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void northstar$init(boolean spawnParticles, int fireDamage, Properties properties, CallbackInfo ci) {
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
        if (state == null || NorthstarOxygen.hasOxygen(context.getLevel(), context.getClickedPos()))
            return state;
        return state.setValue(CampfireBlock.LIT, false)
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
        if (state.getBlock() instanceof CampfireBlock && level instanceof Level l) {
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
                level.setBlockAndUpdate(pos, newState);
            }
        }
    }

    @Unique
    @Nullable
    private BlockState northstar$updateShape(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(CampfireBlock.LIT)) {
            if (!NorthstarOxygen.hasOxygen(level, pos)) {
                level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1, 1);
                return state.setValue(CampfireBlock.LIT, false)
                        .setValue(NorthstarBlockStateProperties.OXYGEN_DEPRIVED, true);
            }
        } else {
            if (state.getValue(NorthstarBlockStateProperties.OXYGEN_DEPRIVED) &&
                NorthstarConfigs.server().relitExtinguishedBlocks.get() &&
                NorthstarOxygen.hasOxygen(level, pos)) {
                return state.setValue(CampfireBlock.LIT, true)
                        .setValue(NorthstarBlockStateProperties.OXYGEN_DEPRIVED, false);
            }
        }
        return null;
    }

}
