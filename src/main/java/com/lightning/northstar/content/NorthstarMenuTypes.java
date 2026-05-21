package com.lightning.northstar.content;

import com.lightning.northstar.block.tech.astronomy_table.AstronomyTableMenu;
import com.lightning.northstar.block.tech.astronomy_table.AstronomyTableScreen;
import com.lightning.northstar.block.tech.rocket_station.RocketStationMenu;
import com.lightning.northstar.block.tech.rocket_station.RocketStationScreen;
import com.lightning.northstar.item.atlas.SpaceAtlasMenu;
import com.lightning.northstar.item.atlas.SpaceAtlasScreen;
import com.tterrag.registrate.util.entry.MenuEntry;

import static com.lightning.northstar.Northstar.REGISTRATE;

public class NorthstarMenuTypes {

    public static final MenuEntry<AstronomyTableMenu> ASTRONOMY_TABLE_MENU = REGISTRATE
            .menu("astronomy_table_menu", AstronomyTableMenu::new, () -> AstronomyTableScreen::new)
            .register();

    public static final MenuEntry<RocketStationMenu> ROCKET_STATION = REGISTRATE
            .menu("rocket_station", RocketStationMenu::new, () -> RocketStationScreen::new)
            .register();

    public static final MenuEntry<SpaceAtlasMenu> SPACE_ATLAS = REGISTRATE
            .menu("space_atlas", SpaceAtlasMenu::new, () -> SpaceAtlasScreen::new)
            .register();

    public static void register() {
    }

}
