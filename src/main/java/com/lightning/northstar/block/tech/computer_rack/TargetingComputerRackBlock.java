package com.lightning.northstar.block.tech.computer_rack;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.content.NorthstarItems;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class TargetingComputerRackBlock extends HorizontalKineticBlock implements IBE<TargetingComputerRackBlockEntity> {

    public TargetingComputerRackBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(HORIZONTAL_FACING, Direction.NORTH));
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

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean pIsMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof TargetingComputerRackBlockEntity rackBE))
                return;
            for (int brh = 0; brh < rackBE.container.getContainerSize(); brh++) {
                Block.popResource(world, pos, rackBE.container.getItem(brh));
            }
            world.removeBlockEntity(pos);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Direction dir = state.getValue(HORIZONTAL_FACING);
        Vec3 newhit = hit.getLocation().subtract(new Vec3(pos.getX(), pos.getY(), pos.getZ()));
        ItemStack heldItem = player.getItemInHand(hand);
        if (!(heldItem.getItem() == NorthstarItems.TARGETING_COMPUTER.get() || heldItem.isEmpty()))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        switch (dir) {
            //bruh this is so bad but like i dont know how else to do this in an elegant manner
            case NORTH:
                if (newhit.y < 0.5) {
                    if (newhit.x < 0.35)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(0);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(0, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else if (newhit.x < 0.65)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(1);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(1, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(2);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(2, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                } else {
                    if (newhit.x < 0.35)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(3);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(3, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else if (newhit.x < 0.65)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(4);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(4, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(5);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(5, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                }
            case SOUTH:
                if (newhit.y < 0.5) {
                    if (newhit.x < 0.35)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(2);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(2, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else if (newhit.x < 0.65)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(1);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(1, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(0);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(0, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                } else {
                    if (newhit.x < 0.35)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(5);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(5, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else if (newhit.x < 0.65)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(4);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(4, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(3);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(3, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                }
            case EAST:
                if (newhit.y < 0.5) {
                    if (newhit.z < 0.35)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(0);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(0, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else if (newhit.z < 0.65)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(1);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(1, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(2);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(2, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                } else {
                    if (newhit.z < 0.35)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(3);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(3, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else if (newhit.z < 0.65)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(4);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(4, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(5);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(5, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                }
            case WEST:
                if (newhit.y < 0.5) {
                    if (newhit.z < 0.35)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(2);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(2, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else if (newhit.z < 0.65)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(1);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(1, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(0);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(0, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                } else {
                    if (newhit.z < 0.35)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(5);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(5, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else if (newhit.z < 0.65)
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(4);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(4, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                    else
                        return onBlockEntityUseItemOn(world, pos, be -> {
                            ItemStack mainItemStack = be.container.getItem(3);
                            player.getInventory().placeItemBackInInventory(mainItemStack);
                            player.getInventory().removeItem(heldItem);
                            be.container.setItem(3, heldItem);
                            if (!mainItemStack.isEmpty())
                                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());

                            be.notifyUpdate();
                            return ItemInteractionResult.SUCCESS;
                        });
                }
            default:
                break;
        }
        hit.getLocation();
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

}
