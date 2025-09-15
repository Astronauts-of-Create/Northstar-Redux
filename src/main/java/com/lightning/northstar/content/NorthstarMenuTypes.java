package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.astronomy_table.AstronomyTableMenu;
import com.lightning.northstar.block.tech.astronomy_table.AstronomyTableScreen;
import com.lightning.northstar.block.tech.rocket_station.RocketStationMenu;
import com.lightning.northstar.block.tech.rocket_station.RocketStationScreen;
import com.lightning.northstar.block.tech.telescope.TelescopeMenu;
import com.lightning.northstar.block.tech.telescope.TelescopeScreen;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class NorthstarMenuTypes {

    public static final MenuEntry<TelescopeMenu> TELESCOPE_MENU = register("telescope", TelescopeMenu::new, () -> TelescopeScreen::new);
    public static final MenuEntry<AstronomyTableMenu> ASTRONOMY_TABLE_MENU = register("astronomy_table_menu", AstronomyTableMenu::new, () -> AstronomyTableScreen::new);
    public static final MenuEntry<RocketStationMenu> ROCKET_STATION = register("rocket_station", RocketStationMenu::new, () -> RocketStationScreen::new);

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
            String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return Northstar.REGISTRATE
                .menu(name, factory, screenFactory)
                .register();
    }

    public static void register() {
    }

}
