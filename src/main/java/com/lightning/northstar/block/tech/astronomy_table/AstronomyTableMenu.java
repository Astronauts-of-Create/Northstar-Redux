package com.lightning.northstar.block.tech.astronomy_table;

import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarMenuTypes;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class AstronomyTableMenu extends MenuBase<AstronomyTableBlockEntity> {

    public Component errorMessage;

    protected SimpleContainer inputSlots;
    protected ResultContainer resultSlots;

    public AstronomyTableMenu(MenuType<AstronomyTableMenu> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public AstronomyTableMenu(MenuType<?> type, int id, Inventory inv, AstronomyTableBlockEntity contentHolder) {
        super(type, id, inv, contentHolder);
    }

    public static AstronomyTableMenu create(int id, Inventory inv, AstronomyTableBlockEntity be) {
        return new AstronomyTableMenu(NorthstarMenuTypes.ASTRONOMY_TABLE_MENU.get(), id, inv, be);
    }

    @Override
    protected AstronomyTableBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        if (Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos()) instanceof AstronomyTableBlockEntity be) {
            return be;
        }
        return null;
    }

    @Override
    protected void initAndReadInventory(AstronomyTableBlockEntity contentHolder) {
        inputSlots = new SimpleContainer(3);
        resultSlots = new ResultContainer();

        inputSlots.addListener(container -> updateResult());
    }

    @Override
    protected void addSlots() {
        class InputSlot extends Slot {
            public InputSlot(Container container, int slot, int x, int y) {
                super(container, slot, x, y);
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(NorthstarItems.ASTRONOMICAL_READING.get());
            }
        }

        addSlot(new InputSlot(inputSlots, 0, 24, 47));
        addSlot(new InputSlot(inputSlots, 1, 80, 47));
        addSlot(new InputSlot(inputSlots, 2, 52, 27));
        addSlot(new Slot(resultSlots, 3, 134, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                for (int i = 0; i < 3; i++) {
                    inputSlots.getItem(i).setCount(inputSlots.getItem(i).getCount() - 1);
                }
                player.level().playSound(player, contentHolder.getBlockPos(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        });

        addPlayerSlots(8, 84);
    }

    @Override
    protected void saveData(AstronomyTableBlockEntity contentHolder) {
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        clearContainer(player, inputSlots);
        resultSlots.clearContent();
    }

    public void updateResult() {
        errorMessage = null;
        resultSlots.clearContent();

        ItemStack item1 = inputSlots.getItem(0);
        ItemStack item2 = inputSlots.getItem(1);
        ItemStack item3 = inputSlots.getItem(2);

        String planet1 = item1.get(NorthstarDataComponents.PLANET);
        String planet2 = item2.get(NorthstarDataComponents.PLANET);
        String planet3 = item3.get(NorthstarDataComponents.PLANET);
        if (planet1 == null || planet2 == null || planet3 == null)
            return;

        if (!planet1.equals(planet2) || !planet1.equals(planet3)) {
            errorMessage = Component.translatable("northstar.gui.astronomy_table.different_planets");
            return;
        }

        if (!arePlanetsFarEnough(item1, item2, item3)) {
            errorMessage = Component.translatable("northstar.gui.astronomy_table.close_data");
            return;
        }

        ItemStack result = new ItemStack(NorthstarItems.STAR_MAP.get());
        result.set(DataComponents.CUSTOM_NAME, Component.translatable("item.northstar.star_map" + "_" + planet1).setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA).withItalic(false)));
        result.set(NorthstarDataComponents.PLANET, planet1);
        resultSlots.setItem(0, result);
    }

    private boolean arePlanetsFarEnough(ItemStack item1, ItemStack item2, ItemStack item3) {
        int x1 = item1.get(NorthstarDataComponents.PLANET_X);
        int y1 = item1.get(NorthstarDataComponents.PLANET_Y);
        int x2 = item2.get(NorthstarDataComponents.PLANET_X);
        int y2 = item2.get(NorthstarDataComponents.PLANET_Y);
        int x3 = item3.get(NorthstarDataComponents.PLANET_X);
        int y3 = item3.get(NorthstarDataComponents.PLANET_Y);
        double r1 = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)); // 1 - 2
        double r2 = Math.sqrt(Math.pow(x3 - x2, 2) + Math.pow(y3 - y2, 2)); // 2 - 3
        double r3 = Math.sqrt(Math.pow(x1 - x3, 2) + Math.pow(y1 - y3, 2)); // 1 - 3
        double averageDistance = (r1 + r2 + r3) / 3;
        return Math.abs(averageDistance) > 30;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 3) {
                if (!this.moveItemStackTo(itemstack1, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index != 0 && index != 1 && index != 2) {
                if (index >= 4 && index < 40) {
                    int i = -1;
                    if (this.slots.get(0).getItem() == ItemStack.EMPTY && i == -1) {
                        i = 0;
                    }
                    if (this.slots.get(2).getItem() == ItemStack.EMPTY && i == -1) {
                        i = 2;
                    }
                    if (this.slots.get(1).getItem() == ItemStack.EMPTY && i == -1) {
                        i = 1;
                    }
                    if (i != -1) {
                        if (!this.moveItemStackTo(itemstack1, i, 3, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 4, 40, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

}
