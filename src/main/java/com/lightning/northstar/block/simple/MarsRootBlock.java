package com.lightning.northstar.block.simple;

import com.lightning.northstar.content.NorthstarBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MarsRootBlock extends MultifaceBlock implements BonemealableBlock {

    public static final MapCodec<MarsRootBlock> CODEC = simpleCodec(MarsRootBlock::new);

    public final MultifaceSpreader spreader = new MultifaceSpreader(new MultifaceSpreader.DefaultSpreaderConfig(this) {
        @Override
        public @Nullable BlockState getStateForPlacement(BlockState currentState, BlockGetter level, BlockPos pos, Direction lookingDirection) {
            MarsRootBlock block = currentState.getBlock() instanceof MarsRootBlock b ? b :
                    (level instanceof Level l ? l.random.nextFloat() : (float) Math.random()) < 0.2f ?
                            NorthstarBlocks.GLOWING_MARS_ROOTS.get() :
                            NorthstarBlocks.MARS_ROOTS.get();
            return block.getStateForPlacement(currentState, level, pos, lookingDirection);
        }
    });

    public MarsRootBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends MultifaceBlock> codec() {
        return CODEC;
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return spreader;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        spreader.spreadAll(state, level, pos, false);
    }

}
