package com.lightning.northstar.block.tech.telescope;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class TelescopeMenu extends MenuBase<TelescopeBlockEntity> {

    public TelescopeMenu(MenuType<TelescopeMenu> type, int id, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        super(type, id, inventory, extraData);
    }

    public TelescopeMenu(MenuType<?> type, int id, Inventory inv, TelescopeBlockEntity contentHolder) {
        super(type, id, inv, contentHolder);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    protected TelescopeBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        return null;
    }

    @Override
    protected void initAndReadInventory(TelescopeBlockEntity contentHolder) {

    }

    @Override
    protected void addSlots() {
    }

    @Override
    protected void saveData(TelescopeBlockEntity contentHolder) {
    }

}
