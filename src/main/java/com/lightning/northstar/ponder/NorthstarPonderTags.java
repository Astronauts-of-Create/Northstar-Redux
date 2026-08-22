package com.lightning.northstar.ponder;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarItems;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;

public class NorthstarPonderTags {

    public static final PonderTag
            SPACE_EXPLORATION = new PonderTag(Northstar.asResource("space_exploration"));

    public static void register() {
        SPACE_EXPLORATION
                .addToIndex()
                .item(NorthstarItems.IRON_SPACE_SUIT_HELMET, true, false)
                .defaultLang("Space exploration", "Components used for space exploration.");

        PonderRegistry.TAGS.forTag(SPACE_EXPLORATION)
                // see comment in NorthstarPonderPlugin
                //.add(NorthstarBlocks.IRON_COGWHEEL)
                //.add(NorthstarBlocks.IRON_LARGE_COGWHEEL)
                .add(NorthstarBlocks.OXYGEN_FILLER)
                .add(NorthstarBlocks.ROCKET_THRUSTER)
                .add(NorthstarBlocks.ROCKET_CONTROLS)
                .add(NorthstarBlocks.ROCKET_STATION);
    }

}
