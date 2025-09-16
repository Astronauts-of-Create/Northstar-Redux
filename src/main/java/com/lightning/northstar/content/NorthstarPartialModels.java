package com.lightning.northstar.content;

import com.jozufozu.flywheel.core.PartialModel;
import com.lightning.northstar.Northstar;

public class NorthstarPartialModels {

    public static final PartialModel
            IRON_SPACE_SUIT_HELMET = armor("iron_space_suit_helmet"),
            BROKEN_IRON_SPACE_SUIT_HELMET = armor("broken_iron_space_suit_helmet"),
            MARTIAN_STEEL_SPACE_SUIT_HELMET = armor("martian_steel_space_suit_helmet"),
            CIRCUIT_ENGRAVER_HEAD = block("circuit_engraver/head"),
            CIRCUIT_ENGRAVER_LASER = block("circuit_engraver/laser"),
            OXYGEN_CONCENTATOR_FAN = block("oxygen_concentrator/fan"),
            WARM_SPINNY = block("temperature_regulator/warm_spinny"),
            COLD_SPINNY = block("temperature_regulator/cold_spinny"),
            OXYGEN_SEALER_FAN = block("oxygen_sealer/fan"),
            PISTON1 = block("combustion_engine/pistons/piston1"),
            PISTON2 = block("combustion_engine/pistons/piston2"),
            PISTON3 = block("combustion_engine/pistons/piston3"),
            PISTON4 = block("combustion_engine/pistons/piston4"),
            PISTON5 = block("combustion_engine/pistons/piston5"),
            PISTON6 = block("combustion_engine/pistons/piston6"),
            CPU1 = block("computer_rack/tier1/cpu1"),
            CPU2 = block("computer_rack/tier1/cpu2"),
            CPU3 = block("computer_rack/tier1/cpu3"),
            CPU4 = block("computer_rack/tier1/cpu4"),
            CPU5 = block("computer_rack/tier1/cpu5"),
            CPU6 = block("computer_rack/tier1/cpu6"),
            CONTROL_LEVER = block("rocket_controls/stick"),
            CONTROL_LEVER_BLOCK = block("rocket_controls/stick_2"),
            IRON_COGWHEEL = block("iron_cogwheel"),
            IRON_LARGE_COGWHEEL = block("iron_large_cogwheel"),
            SOLAR_PANEL_FULL = block("solar_panel/solar_panel_full"),
            SOLAR_PANEL_SLIM = block("solar_panel/solar_panel_slim"),
            SOLAR_PANEL_NORTH = block("solar_panel/solar_panel_north"),
            SOLAR_PANEL_SOUTH = block("solar_panel/solar_panel_south");

    private static PartialModel block(String path) {
        return new PartialModel(Northstar.asResource("block/" + path));
    }

    private static PartialModel armor(String path) {
        return new PartialModel(Northstar.asResource("entities/armor/" + path));
    }

    public static void register() {
    }

}
