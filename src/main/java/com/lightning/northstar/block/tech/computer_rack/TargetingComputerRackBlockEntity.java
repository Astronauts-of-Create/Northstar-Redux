package com.lightning.northstar.block.tech.computer_rack;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TargetingComputerRackBlockEntity extends SmartBlockEntity {

    public Container container = new SimpleContainer(6);

    public TargetingComputerRackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        if (tag.contains("slot1", Tag.TAG_COMPOUND)) container.setItem(0, ItemStack.parseOptional(registries, tag.getCompound("slot1")));
        if (tag.contains("slot2", Tag.TAG_COMPOUND)) container.setItem(1, ItemStack.parseOptional(registries, tag.getCompound("slot2")));
        if (tag.contains("slot3", Tag.TAG_COMPOUND)) container.setItem(2, ItemStack.parseOptional(registries, tag.getCompound("slot3")));
        if (tag.contains("slot4", Tag.TAG_COMPOUND)) container.setItem(3, ItemStack.parseOptional(registries, tag.getCompound("slot4")));
        if (tag.contains("slot5", Tag.TAG_COMPOUND)) container.setItem(4, ItemStack.parseOptional(registries, tag.getCompound("slot5")));
        if (tag.contains("slot6", Tag.TAG_COMPOUND)) container.setItem(5, ItemStack.parseOptional(registries, tag.getCompound("slot6")));
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        tag.put("slot1", container.getItem(0).save(registries));
        tag.put("slot2", container.getItem(1).save(registries));
        tag.put("slot3", container.getItem(2).save(registries));
        tag.put("slot4", container.getItem(3).save(registries));
        tag.put("slot5", container.getItem(4).save(registries));
        tag.put("slot6", container.getItem(5).save(registries));
    }

}
