package com.lightning.northstar.block.tech.oxygen_filler;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

public class OxygenFillerBlock extends HorizontalKineticBlock implements IBE<OxygenFillerBlockEntity> {

    private static final Map<Direction, VoxelShape> SHAPES;

    static {
        VoxelShape base = Shapes.or(
                box(0, 0, 0, 16, 2, 16),
                box(1, 1, 1, 15, 15, 15));

        SHAPES = new EnumMap<>(Direction.class);
        SHAPES.put(Direction.NORTH, Shapes.or(base, box(0, 0, 14, 16, 16, 16)));
        SHAPES.put(Direction.SOUTH, Shapes.or(base, box(0, 0, 0, 16, 16, 2)));
        SHAPES.put(Direction.EAST, Shapes.or(base, box(0, 0, 0, 2, 16, 16)));
        SHAPES.put(Direction.WEST, Shapes.or(base, box(14, 0, 0, 16, 16, 16)));
        SHAPES.put(Direction.UP, Shapes.block());
        SHAPES.put(Direction.DOWN, Shapes.block());
    }

    public OxygenFillerBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(HORIZONTAL_FACING));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return onBlockEntityUseItemOn(level, pos, be -> {
            ItemStack mainItemStack = be.container.getItem(0);
            player.getInventory().placeItemBackInInventory(mainItemStack);
            player.getInventory().removeItem(stack);
            be.container.setItem(0, stack);
            if (!mainItemStack.isEmpty())
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

            be.notifyUpdate();
            return ItemInteractionResult.SUCCESS;
        });
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean pIsMoving) {
        super.onRemove(state, world, pos, newState, pIsMoving);
        if (!state.is(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof OxygenFillerBlockEntity be) {
                Block.popResource(world, pos, be.container.getItem(0));
                be.container.clearContent();
            }
        }
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    }

    @Override
    public Class<OxygenFillerBlockEntity> getBlockEntityClass() {
        return OxygenFillerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends OxygenFillerBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.OXYGEN_FILLER.get();
    }

}