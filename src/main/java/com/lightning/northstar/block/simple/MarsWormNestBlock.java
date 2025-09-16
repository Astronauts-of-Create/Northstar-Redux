package com.lightning.northstar.block.simple;

import com.lightning.northstar.content.NorthstarEntityTypes;
import com.lightning.northstar.entity.MarsWormEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MarsWormNestBlock extends Block {

    public static final VoxelShape NEST_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    public static final VoxelShape EGG_SHAPE = Shapes.or(NEST_SHAPE, Block.box(3.5D, 0.0D, 3.5D, 12.5D, 16.0D, 12.5D));
    public static final BooleanProperty HAS_EGG = BooleanProperty.create("has_egg");

    public MarsWormNestBlock(BlockBehaviour.Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(HAS_EGG, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HAS_EGG);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(HAS_EGG))
            return EGG_SHAPE;
        return NEST_SHAPE;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getValue(HAS_EGG) ? 9 : 0;
    }

    public boolean canBeEgged(BlockState block) {
        return block.getValue(MarsWormNestBlock.HAS_EGG);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!level.isClientSide && state.getValue(HAS_EGG) && !(entity instanceof MarsWormEntity)) {
            level.setBlockAndUpdate(pos, state.setValue(HAS_EGG, false));
            if (entity instanceof Player player) {
                level.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 1, 1);
                level.playSound(player, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 1, 1);
            } else {
                level.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 1, 1);
            }
            level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
        }
    }


    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource pRandom) {
        if (state.getValue(HAS_EGG) && pRandom.nextInt(9) == 0) {
            MarsWormEntity baby = NorthstarEntityTypes.MARS_WORM.get().create(level);
            baby.setBaby(true);
            baby.eggTimer = 12000;
            baby.moveTo(pos, 0, 0);
            level.addFreshEntity(baby);
            level.setBlockAndUpdate(pos, defaultBlockState());
            level.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 1, explosionResistance);
        }
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return canSupportCenter(pLevel, pPos.below(), Direction.UP);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }

}
