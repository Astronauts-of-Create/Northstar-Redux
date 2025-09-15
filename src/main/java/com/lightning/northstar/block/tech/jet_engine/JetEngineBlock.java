package com.lightning.northstar.block.tech.jet_engine;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class JetEngineBlock extends Block implements IWrenchable {

    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");

    public JetEngineBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState()
                .setValue(TOP, false)
                .setValue(BOTTOM, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOP, BOTTOM);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moved) {
        if (!(oldState.getBlock() instanceof JetEngineBlock))
            updateConnectivity(world, pos, true);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!(newState.getBlock() instanceof JetEngineBlock))
            updateConnectivity(world, pos, false);
    }

    private void updateConnectivity(Level world, BlockPos pos, boolean added) {
        BlockState state = defaultBlockState()
                .setValue(TOP, updateConnectivity(world, pos.above(), added, BOTTOM))
                .setValue(BOTTOM, updateConnectivity(world, pos.below(), added, TOP));

        if (added)
            world.setBlock(pos, state, Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
    }

    private boolean updateConnectivity(Level world, BlockPos pos, boolean added, BooleanProperty prop) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof JetEngineBlock) {
            world.setBlock(pos, state.setValue(prop, added), Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
            return true;
        }
        return false;
    }

}
