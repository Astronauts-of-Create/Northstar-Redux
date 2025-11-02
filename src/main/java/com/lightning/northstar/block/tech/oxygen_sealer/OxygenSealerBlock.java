package com.lightning.northstar.block.tech.oxygen_sealer;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class OxygenSealerBlock extends HorizontalKineticBlock implements IBE<OxygenSealerBlockEntity> {

    protected static final VoxelShape SHAPE = Shapes.or(
            box(0, 0, 0, 16, 12, 16),
            box(1, 12, 1, 15, 16, 15)
    );

    public static final BooleanProperty CAPPED = BooleanProperty.create("capped");

    public OxygenSealerBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState()
                .setValue(CAPPED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(CAPPED));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getPlayer() != null && context.getPlayer().isCrouching())
            return defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection());
        return defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (context.getClickedFace().getAxis().isHorizontal()) {
            context.getLevel().setBlock(context.getClickedPos(), state.cycle(CAPPED), Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
            IWrenchable.playRotateSound(context.getLevel(), context.getClickedPos());
            return InteractionResult.SUCCESS;
        }

        return super.onWrenched(state, context);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(CAPPED) ? Shapes.block() : SHAPE;
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
        return NorthstarBlockEntityTypes.OXYGEN_SEALER.get();
    }

}
