package com.lightning.northstar.block.tech.solar_panel;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SolarPanelBlock extends HorizontalKineticBlock implements IBE<SolarPanelBlockEntity> {

    public static final VoxelShape SHAPE_X = Block.box(1, 0, 0, 15, 14, 16);
    public static final VoxelShape SHAPE_Z = Block.box(0, 0, 1, 16, 14, 15);

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");

    public SolarPanelBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState()
                .setValue(HORIZONTAL_FACING, Direction.NORTH)
                .setValue(NORTH, false)
                .setValue(SOUTH, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(NORTH, SOUTH));
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moved) {
        super.onPlace(state, world, pos, oldState, moved);

        if (!(oldState.getBlock() instanceof SolarPanelBlock))
            updateConnectivity(world, pos, state, true);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, world, pos, newState, isMoving);

        if (!(newState.getBlock() instanceof SolarPanelBlock))
            updateConnectivity(world, pos, state, false);
    }

    private void updateConnectivity(Level world, BlockPos pos, BlockState state, boolean added) {
        state = state
                .setValue(NORTH, updateConnectivity(world, pos.north(), added, SOUTH))
                .setValue(SOUTH, updateConnectivity(world, pos.south(), added, NORTH));

        if (added)
            world.setBlock(pos, state, Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
    }

    private boolean updateConnectivity(Level world, BlockPos pos, boolean added, BooleanProperty prop) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof SolarPanelBlock) {
            world.setBlock(pos, state.setValue(prop, added), Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
            return true;
        }
        return false;
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);

        return state.setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getCounterClockWise());
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        withBlockEntityDo(level, pos, SolarPanelBlockEntity::determineAndApplySunlightScore);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HORIZONTAL_FACING).getAxis() == Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return state.getValue(HORIZONTAL_FACING).getAxis() == face.getAxis();
    }

    @Override
    public float getParticleTargetRadius() {
        return 1.125f;
    }

    @Override
    public float getParticleInitialRadius() {
        return 1f;
    }

    @Override
    public boolean hideStressImpact() {
        return true;
    }

    @Override
    public Class<SolarPanelBlockEntity> getBlockEntityClass() {
        return SolarPanelBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SolarPanelBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.SOLAR_PANEL.get();
    }

}
