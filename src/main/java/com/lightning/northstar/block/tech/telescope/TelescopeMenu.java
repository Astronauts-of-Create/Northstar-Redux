package com.lightning.northstar.block.tech.telescope;

import com.lightning.northstar.content.NorthstarMenuTypes;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class TelescopeMenu extends MenuBase<TelescopeBlockEntity> {

    public TelescopeMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public TelescopeMenu(MenuType<?> type, int id, Inventory inv, TelescopeBlockEntity contentHolder) {
        super(type, id, inv, contentHolder);
    }

    public static TelescopeMenu create(int id, Inventory inventory, TelescopeBlockEntity data) {
        return new TelescopeMenu(NorthstarMenuTypes.TELESCOPE_MENU.get(), id, inventory, data);
    }

    @Override
    protected TelescopeBlockEntity createOnClient(FriendlyByteBuf extraData) {
        if (Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos()) instanceof TelescopeBlockEntity be) {
            return be;
        }
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

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

}
