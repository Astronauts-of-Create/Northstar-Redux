package com.lightning.northstar.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BetterSimpleContainer extends net.minecraft.world.SimpleContainer {

    public BetterSimpleContainer(int size) {
        super(size);
    }

    public BetterSimpleContainer(ItemStack... items) {
        super(items);
    }

    @Override
    public void fromTag(ListTag containerNbt, HolderLookup.Provider levelRegistry) {
        clearContent();

        for (int i = 0; i < containerNbt.size(); i++) {
            CompoundTag tag = containerNbt.getCompound(i);
            int slot = tag.getByte("Slot");
            if (slot >= 0 && slot < getContainerSize()) {
                setItem(slot, ItemStack.parseOptional(levelRegistry, tag));
            }
        }
    }

    @Override
    public ListTag createTag(HolderLookup.Provider levelRegistry) {
        ListTag list = new ListTag();

        for (int i = 0; i < getContainerSize(); i++) {
            ItemStack stack = getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            CompoundTag compound = (CompoundTag) stack.save(levelRegistry, new CompoundTag());
            compound.putByte("Slot", (byte) i);
            list.add(compound);
        }

        return list;
    }

}
