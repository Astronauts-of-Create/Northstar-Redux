package com.lightning.northstar.block.tech.oxygen_sealer;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OxygenSealerBlock extends HorizontalKineticBlock implements IBE<OxygenSealerBlockEntity> {

    protected static final VoxelShape SHAPE = Shapes.or(
            box(0, 0, 0, 16, 12, 16),
            box(1, 12, 1, 15, 16, 15)
    );

    public OxygenSealerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public Class<OxygenSealerBlockEntity> getBlockEntityClass() {
        return OxygenSealerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends OxygenSealerBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.OXYGEN_GENERATOR.get();
    }

}
