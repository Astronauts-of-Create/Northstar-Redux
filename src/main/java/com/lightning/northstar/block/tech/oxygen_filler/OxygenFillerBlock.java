package com.lightning.northstar.block.tech.oxygen_filler;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
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
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return onBlockEntityUse(world, pos, be -> {
            if (hand != InteractionHand.MAIN_HAND)
                return InteractionResult.PASS;

            ItemStack held = player.getItemInHand(hand);
            ItemStack item = be.container.getItem(0);

            if (held.getCount() == 1) {
                // player holds a single item, swap them
                be.container.setItem(0, held);
                player.setItemInHand(hand, item);
            } else {
                // place one item from the hand in the filler and give back the item that was stored
                be.container.setItem(0, held.copyWithCount(1));
                player.setItemInHand(hand, held.copyWithCount(held.getCount() - 1));
                if (!item.isEmpty())
                    player.getInventory().placeItemBackInInventory(item);
            }

            if (!item.isEmpty()) {
                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + world.random.nextFloat());
            }

            be.notifyUpdate();
            return InteractionResult.SUCCESS;
        });
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