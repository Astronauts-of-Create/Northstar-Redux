package com.lightning.northstar.block.tech.rocket_station;

import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarMenuTypes;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RocketStationMenu extends MenuBase<RocketStationBlockEntity> {

    public int fuelCost;
    public ResourceKey<Level> target;

    public RocketStationMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public RocketStationMenu(MenuType<?> type, int id, Inventory inv, RocketStationBlockEntity contentHolder) {
        super(type, id, inv, contentHolder);
    }

    public static AbstractContainerMenu create(int id, Inventory inv, RocketStationBlockEntity be) {
        return new RocketStationMenu(NorthstarMenuTypes.ROCKET_STATION.get(), id, inv, be);
    }

    @Override
    protected RocketStationBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        if (Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos()) instanceof RocketStationBlockEntity be) {
            return be;
        }
        return null;
    }

    @Override
    protected void initAndReadInventory(RocketStationBlockEntity contentHolder) {
    }

    @Override
    protected void addSlots() {
        addSlot(new Slot(contentHolder.container, 0, 24, 47));
        addPlayerSlots(8, 84);
    }

    @Override
    protected void saveData(RocketStationBlockEntity contentHolder) {

    }

    public int fuelCalc() {
        String home = NorthstarPlanets.getPlanetName(contentHolder.getLevel().dimension());
        String targ = NorthstarPlanets.getPlanetName(target);

        int home_x = (int) NorthstarPlanets.getPlanetX(home);
        int home_y = (int) NorthstarPlanets.getPlanetY(home);

        int targ_x = (int) NorthstarPlanets.getPlanetX(targ);
        int targ_y = (int) NorthstarPlanets.getPlanetY(targ);

        int dif = (int) (Math.pow(home_x - targ_x, 2) + Math.pow(home_y - targ_y, 2));
        dif = Mth.roundToward(dif, 100) / 20;
        int cost = dif + NorthstarPlanets.getPlanetAtmosphereCost(contentHolder.getLevel().dimension()) + 1000;
        return cost * 8;
    }

    @Override
    public void slotsChanged(Container inventory) {
        ItemStack item = contentHolder.container.getItem(0);
        if (contentHolder.container.getItem(0).getItem() == NorthstarItems.STAR_MAP.get() || contentHolder.container.getItem(0).getItem() == NorthstarItems.RETURN_TICKET.get()) {
            if (item.has(NorthstarDataComponents.PLANET))
                target = NorthstarPlanets.getPlanetDimension(item.get(NorthstarDataComponents.PLANET));
        }
        fuelCost = fuelCalc();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

}
