package com.lightning.northstar.block.tech.computer_rack;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.content.NorthstarItems;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TargetingComputerRackBlock extends HorizontalKineticBlock implements IBE<TargetingComputerRackBlockEntity> {

    public TargetingComputerRackBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Direction dir = state.getValue(HORIZONTAL_FACING);
        if (hit.getDirection().getAxis() != dir.getAxis())
            return InteractionResult.PASS;

        Vec3 localPos = hit.getLocation().subtract(Vec3.atLowerCornerOf(pos));
        float x = (float) switch (dir) {
            case NORTH -> localPos.x;
            case SOUTH -> 1 - localPos.x;
            case EAST -> localPos.z;
            case WEST -> 1 - localPos.z;
            default -> 0;
        };
        float y = (float) localPos.y;

        int slot = (x < 1f / 3f ? 0 : x < 2f / 3f ? 1 : 2) + (y > 0.5f ? 3 : 0);

        return onBlockEntityUse(level, pos, be -> {
            ItemStack computer = be.container.getItem(slot);
            if (computer.isEmpty()) {
                ItemStack held = player.getItemInHand(hand);
                if (!level.isClientSide && held.is(NorthstarItems.TARGETING_COMPUTER.get())) {
                    be.container.setItem(slot, held.copyWithCount(1));
                    player.setItemInHand(hand, held.copyWithCount(held.getCount() - 1));
                    be.notifyUpdate();
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (!level.isClientSide) {
                if (!player.addItem(computer))
                    popResourceFromFace(level, pos, dir, computer);
                be.container.setItem(slot, ItemStack.EMPTY);
                be.notifyUpdate();
            }
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + level.random.nextFloat());

            return InteractionResult.sidedSuccess(level.isClientSide);
        });
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    }

    @Override
    public Class<TargetingComputerRackBlockEntity> getBlockEntityClass() {
        return TargetingComputerRackBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TargetingComputerRackBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.COMPUTER_RACK.get();
    }

}
