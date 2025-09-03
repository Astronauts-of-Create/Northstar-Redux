package com.lightning.northstar.block.tech.solar_panel;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SolarPanelBlock extends HorizontalKineticBlock implements IBE<SolarPanelBlockEntity> {

    protected static final VoxelShape SHAPE_EAST_WEST = Block.box(1.0D, 0.0D, 0.0D, 15.0D, 12.0D, 16.0D);
    protected static final VoxelShape SHAPE_NORTH_SOUTH = Block.box(0.0D, 0.0D, 1.0D, 16.0D, 12.0D, 15.0D);

    public SolarPanelBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(HORIZONTAL_FACING, Direction.NORTH));
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
        return (state.getValue(HORIZONTAL_FACING) == Direction.EAST) || (state.getValue(HORIZONTAL_FACING) == Direction.WEST) ? SHAPE_EAST_WEST : SHAPE_NORTH_SOUTH;
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

    public static Couple<Integer> getSpeedRange() {
        return Couple.create(8, 8);
    }

}
