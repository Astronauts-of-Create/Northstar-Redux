package com.lightning.northstar.item.atlas;

import com.lightning.northstar.content.NorthstarMenuTypes;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SpaceAtlasMenu extends MenuBase<ItemStack> {

    public static void open(ServerPlayer player, ItemStack item) {
        MenuProvider provider = new SimpleMenuProvider((id, inventory, p) -> new SpaceAtlasMenu(NorthstarMenuTypes.SPACE_ATLAS.get(), id, inventory, item), Component.empty());
        NetworkHooks.openScreen(player, provider, buffer -> buffer.writeItem(item));
    }

    public SpaceAtlasMenu(MenuType<?> type, int id, Inventory inv, @Nullable FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public SpaceAtlasMenu(MenuType<?> type, int id, Inventory inv, ItemStack stack) {
        super(type, id, inv, stack);
    }

    @Override
    protected ItemStack createOnClient(FriendlyByteBuf buffer) {
        return buffer.readItem();
    }

    @Override
    protected void initAndReadInventory(ItemStack stack) {
    }

    @Override
    protected void addSlots() {
    }

    @Override
    protected void saveData(ItemStack stack) {
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

}
