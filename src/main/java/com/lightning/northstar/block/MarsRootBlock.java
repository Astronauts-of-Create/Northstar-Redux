package com.lightning.northstar.block;

import com.google.common.collect.ImmutableMap;
import com.lightning.northstar.content.NorthstarBlocks;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MarsRootBlock extends MultifaceBlock {
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().collect(Util.toMap());
    protected static final float AABB_OFFSET = 1.0F;
    private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    public final MultifaceSpreader spreader = new MultifaceSpreader(this);

    private final Map<BlockState, VoxelShape> shapesCache;


    public MarsRootBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(UP, Boolean.FALSE).setValue(DOWN, Boolean.FALSE).setValue(NORTH, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE));
        this.shapesCache = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), MarsRootBlock::calculateShape)));
    }

    private static VoxelShape calculateShape(BlockState p_57906_) {
        VoxelShape voxelshape = Shapes.empty();
        if (p_57906_.getValue(UP)) {
            voxelshape = UP_AABB;
        }

        if (p_57906_.getValue(NORTH)) {
            voxelshape = Shapes.or(voxelshape, NORTH_AABB);
        }

        if (p_57906_.getValue(SOUTH)) {
            voxelshape = Shapes.or(voxelshape, SOUTH_AABB);
        }

        if (p_57906_.getValue(EAST)) {
            voxelshape = Shapes.or(voxelshape, EAST_AABB);
        }

        if (p_57906_.getValue(WEST)) {
            voxelshape = Shapes.or(voxelshape, WEST_AABB);
        }
        if (p_57906_.getValue(DOWN)) {
            voxelshape = Shapes.or(voxelshape, DOWN_AABB);
        }

        return voxelshape.isEmpty() ? Shapes.block() : voxelshape;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return this.shapesCache.get(pState);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rando) {
        if (rando.nextInt(48) == 0) {
            this.spreader.spreadFromRandomFaceTowardRandomDirection(state, level, pos, rando);
        }
    }


    public static boolean isAcceptableNeighbour(BlockGetter pBlockReader, BlockPos pNeighborPos, Direction pAttachedFace) {
        return MultifaceBlock.canAttachTo(pBlockReader, pAttachedFace, pNeighborPos, pBlockReader.getBlockState(pNeighborPos));
    }

    public static BlockState giveStateValues(LevelAccessor level, BlockPos pPos, RandomSource rando) {
        List<Direction> list = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            BlockState block = level.getBlockState(pPos.relative(dir));

            if (block.isAir()) {
                list.add(dir);
            }
        }
        return rando.nextInt(4) == 0 ?
                NorthstarBlocks.GLOWING_MARS_ROOTS.get().defaultBlockState()
                        .setValue(MarsRootBlock.UP, list.contains(Direction.UP)).setValue(MarsRootBlock.DOWN, list.contains(Direction.DOWN))
                        .setValue(MarsRootBlock.NORTH, list.contains(Direction.NORTH)).setValue(MarsRootBlock.SOUTH, list.contains(Direction.SOUTH))
                        .setValue(MarsRootBlock.EAST, list.contains(Direction.EAST)).setValue(MarsRootBlock.WEST, list.contains(Direction.WEST))
                :
                NorthstarBlocks.MARS_ROOTS.get().defaultBlockState()
                        .setValue(MarsRootBlock.UP, list.contains(Direction.UP)).setValue(MarsRootBlock.DOWN, list.contains(Direction.DOWN))
                        .setValue(MarsRootBlock.NORTH, list.contains(Direction.NORTH)).setValue(MarsRootBlock.SOUTH, list.contains(Direction.SOUTH))
                        .setValue(MarsRootBlock.EAST, list.contains(Direction.EAST)).setValue(MarsRootBlock.WEST, list.contains(Direction.WEST));
    }

    @SuppressWarnings("static-access")
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockstate = pContext.getLevel().getBlockState(pContext.getClickedPos());
        boolean flag = blockstate.is(this);
        BlockState blockstate1 = flag ? blockstate : this.defaultBlockState();

        for (Direction direction : pContext.getNearestLookingDirections()) {
            BooleanProperty booleanproperty = getPropertyForFace(direction);
            boolean flag1 = flag && blockstate.getValue(booleanproperty);
            if (!flag1 && this.canSupportAtFace(pContext.getLevel(), pContext.getClickedPos(), direction)) {
                return blockstate1.setValue(booleanproperty, Boolean.TRUE);
            }

        }
        return flag ? blockstate1 : null;
    }

    public static BlockState getStateForGeneration(WorldGenLevel level, BlockPos pos, RandomSource rando) {
        BlockState newState = NorthstarBlocks.MARS_ROOTS.get().defaultBlockState();
        if (rando.nextInt(4) == 0) {
            newState = NorthstarBlocks.MARS_ROOTS.get().defaultBlockState();
        }
        int i = 0;

        for (Direction direction : Direction.values()) {
            BooleanProperty booleanproperty = getPropertyForFace(direction);
            if (newState.getValue(booleanproperty)) {
                boolean flag = canSupportAtFace(level, pos, direction);
                if (!flag) {
                    flag = (newState.is(NorthstarBlocks.MARS_ROOTS.get()) || newState.is(NorthstarBlocks.GLOWING_MARS_ROOTS.get())) && newState.getValue(booleanproperty);
                }

                newState = newState.setValue(booleanproperty, flag);
                if (!flag)
                    i++;
            }
        }
        if (i == 0) {
            return null;
        } else {
            return newState;
        }
    }

    @SuppressWarnings("static-access")
    private BlockState getUpdatedState(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.above();
        BlockState blockstate = null;

        for (Direction direction : Direction.values()) {
            BooleanProperty booleanproperty = getPropertyForFace(direction);
            if (pState.getValue(booleanproperty)) {
                boolean flag = this.canSupportAtFace(pLevel, pPos, direction);
                if (!flag) {
                    if (blockstate == null) {
                        blockstate = pLevel.getBlockState(blockpos);
                    }

                    flag = blockstate.is(this) && blockstate.getValue(booleanproperty);
                }

                pState = pState.setValue(booleanproperty, flag);
            }
        }

        return pState;
    }

    /**
     * Update the provided state given the provided neighbor direction and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific direction passed in.
     */
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        BlockState blockstate = this.getUpdatedState(pState, pLevel, pCurrentPos);
        return !this.hasFaces(blockstate) ? Blocks.AIR.defaultBlockState() : blockstate;
    }

    private boolean hasFaces(BlockState pState) {
        return this.countFaces(pState) > 0;
    }

    private int countFaces(BlockState pState) {
        int i = 0;
        for (BooleanProperty booleanproperty : PROPERTY_BY_DIRECTION.values()) {
            if (pState.getValue(booleanproperty)) {
                ++i;
            }
        }
        return i;
    }

    private static boolean canSupportAtFace(BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
        BlockPos blockpos = pPos.relative(pDirection);
        if (isAcceptableNeighbour(pLevel, blockpos, pDirection)) {
            return true;
        } else if (pDirection.getAxis() == Direction.Axis.Y) {
            return false;
        } else {
            BooleanProperty booleanproperty = PROPERTY_BY_DIRECTION.get(pDirection);
            BlockState blockstate = pLevel.getBlockState(pPos.above());
            return (blockstate.is(NorthstarBlocks.MARS_ROOTS.get()) || blockstate.is(NorthstarBlocks.GLOWING_MARS_ROOTS.get())) && blockstate.getValue(booleanproperty);
        }

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    public static BooleanProperty getPropertyForFace(Direction pFace) {
        return PROPERTY_BY_DIRECTION.get(pFace);
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return this.spreader;
    }
}
