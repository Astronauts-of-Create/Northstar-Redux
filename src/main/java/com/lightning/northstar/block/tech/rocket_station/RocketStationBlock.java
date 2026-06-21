package com.lightning.northstar.block.tech.rocket_station;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.content.NorthstarStats;
import com.lightning.northstar.contraption.rocket.RocketContraption;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RocketStationBlock extends HorizontalDirectionalBlock implements IBE<RocketStationBlockEntity>, IWrenchable {

    public static final MapCodec<RocketStationBlock> CODEC = simpleCodec(RocketStationBlock::new);

    public static final VoxelShape SHAPE = Shapes.or(
            box(0, 0, 0, 16, 4, 16),
            box(1, 4, 1, 15, 16, 15)
    );

    public RocketStationBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<RocketStationBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return use(level, pos, player).result();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return use(level, pos, player);
    }

    private ItemInteractionResult use(Level level, BlockPos pos, Player player) {
        return onBlockEntityUseItemOn(level, pos, be -> {
            player.awardStat(NorthstarStats.INTERACT_WITH_ROCKET_STATION);
            if (player instanceof ServerPlayer serverPlayer) {
                ItemStack returnTicket = be.container.getItem(1);
                if (!returnTicket.isEmpty()) {
                    level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), returnTicket, 0, 0, 0));
                    be.container.setItem(1, ItemStack.EMPTY);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide());
                }

                RocketContraption contraption = be.assembleContraption();
                if (contraption != null) {
                    RocketStationMenu.open(serverPlayer, be.container, pos, contraption, be, null);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide());
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        });
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, worldIn, pos, newState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return null;
    }

    @Override
    public Class<RocketStationBlockEntity> getBlockEntityClass() {
        return RocketStationBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RocketStationBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.ROCKET_STATION.get();
    }

}
