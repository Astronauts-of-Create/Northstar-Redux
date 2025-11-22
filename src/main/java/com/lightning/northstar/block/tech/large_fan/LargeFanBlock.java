package com.lightning.northstar.block.tech.large_fan;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.world.sealer.SealableBlock;
import com.lightning.northstar.world.sealer.SealerExtensionSource;
import com.lightning.northstar.world.sealer.SealingMode;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LargeFanBlock extends KineticBlock implements IBE<LargeFanBlockEntity>, IWrenchable, SealerExtensionSource, SealableBlock {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final EnumProperty<TenPatch> PATCH = TenPatch.PROPERTY;

    public LargeFanBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState()
                .setValue(AXIS, Direction.Axis.Z)
                .setValue(PATCH, TenPatch.SINGLE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(AXIS, PATCH));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return (state == null ? defaultBlockState() : state).setValue(AXIS, context.getNearestLookingDirection().getAxis());
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        LargeFanBlockEntity be = getBlockEntity(level, pos);
        if (be != null && !be.isController())
            be = be.getControllerBE();
        if (be == null)
            return InteractionResult.PASS;

        if (!heldItem.isEmpty() && heldItem.getItem() == NorthstarItems.FAN_BLADE.asItem() && hit.getDirection().getAxis() == state.getValue(AXIS)) {
            if (be.blades < LargeFanBlockEntity.MAXIMUM_BLADES) {
                be.blades++;
                be.sendData();
                if (!player.isCreative())
                    heldItem.setCount(heldItem.getCount() - 1);
                player.setItemInHand(hand, heldItem);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);

        if (oldState.getBlock() == state.getBlock())
            return;
        if (isMoving)
            return;
        withBlockEntityDo(world, pos, LargeFanBlockEntity::updateConnectivity);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.is(newState.getBlock()) && newState.hasBlockEntity())
            return;
        if (level.getBlockEntity(pos) instanceof LargeFanBlockEntity be) {
            be.destroy();
            level.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(be);
        }
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);

        Direction modifiedDirection = Direction.fromDelta(neighbor.getX() - pos.getX(), neighbor.getY() - pos.getY(), neighbor.getZ() - pos.getZ());
        if (modifiedDirection == null || modifiedDirection.getAxis() == state.getValue(AXIS))
            return;

        withBlockEntityDo(level, pos, be -> {
            LargeFanBlockEntity controller = be.getControllerBE();
            if (controller == null)
                return;
            controller.onNeighborChange(neighbor, false);
        });
    }

    @Override
    public int getMaximumSealedBlocks(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof LargeFanBlockEntity be)
            return be.isController() ? be.getExtraSealedVolume() : 0;
        return 0;
    }

    @Override
    public boolean northstar$isFaceSealed(BlockGetter level, BlockPos pos, BlockState state, Direction direction, boolean source, SealingMode mode) {
        Direction.Axis axis = state.getValue(AXIS);

        if (axis == direction.getAxis())
            return false;

        TenPatch patch = state.getValue(PATCH);
        return switch (patch.type) {
            case CENTER -> false;
            case SIDE -> direction == patch.getDirection(axis);
            case CORNER -> {
                Direction dir = patch.getDirection(axis);
                yield direction != dir && direction != dir.getCounterClockWise(axis);
            }
            default -> true;
        };
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return onBlockEntityUse(context.getLevel(), context.getClickedPos(), be -> {
            if (!be.isController())
                be = be.getControllerBE();
            if (be == null)
                return InteractionResult.PASS;
            be.flipChain = !be.flipChain;
            be.sendData();
            return InteractionResult.SUCCESS;
        });
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        return onBlockEntityUse(context.getLevel(), context.getClickedPos(), be -> {
            if (!be.isController())
                be = be.getControllerBE();
            if (be == null || be.blades == 0)
                return super.onSneakWrenched(state, context);

            super.playRemoveSound(world, pos);

            be.blades--;
            be.sendData();
            if (player == null) // is that even possible? technically nullable through UseOnContext
                popResource(world, pos, NorthstarItems.FAN_BLADE.asStack());
            else
                player.getInventory().placeItemBackInInventory(NorthstarItems.FAN_BLADE.asStack());

            return InteractionResult.SUCCESS;
        });
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public Class<LargeFanBlockEntity> getBlockEntityClass() {
        return LargeFanBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends LargeFanBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.LARGE_FAN.get();
    }

}
