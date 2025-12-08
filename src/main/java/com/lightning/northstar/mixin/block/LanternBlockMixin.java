package com.lightning.northstar.mixin.block;

import com.lightning.northstar.block.simple.ExtinguishedLanternBlock;
import com.lightning.northstar.content.NorthstarBlocks;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

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

    @Nullable
    @ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
    public BlockState northstar$updatePlacementLit(@Nullable BlockState state,
                                                   @Local(argsOnly = true) BlockPlaceContext context) {
        if (state != null && state.getBlock() == Blocks.LANTERN && !NorthstarOxygen.hasOxygen(context.getLevel(), context.getClickedPos()))
            return northstar$copyStateExtinguished(state);
        return state;
    }

    @ModifyReturnValue(method = "updateShape", at = @At("RETURN"))
    public BlockState northstar$updateShape(BlockState state,
                                            @Local(argsOnly = true) LevelAccessor level,
                                            @Local(argsOnly = true, ordinal = 0) BlockPos pos) {
        if (state.getBlock() == Blocks.LANTERN && level instanceof Level l && !NorthstarOxygen.hasOxygen(l, pos)) {
            level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1, 1);
            return northstar$copyStateExtinguished(state);
        }
        return state;
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
