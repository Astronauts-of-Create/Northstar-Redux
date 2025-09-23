package com.lightning.northstar.ponder;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.ponder.scene.RocketStationPonder;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;

public class NorthstarPonderPlugin {

    private static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(Northstar.MOD_ID);

    public static void register() {
        // FIXME: those register under northstar and are missing the translation keys
        /*HELPER.forComponents(NorthstarBlocks.IRON_COGWHEEL)
                .addStoryBoard(ResourceLocation.parse("create:cog/small"), KineticsScenes::cogAsRelay, AllCreatePonderTags.KINETIC_RELAYS)
                .addStoryBoard(ResourceLocation.parse("create:cog/speedup"), KineticsScenes::cogsSpeedUp)
                .addStoryBoard(ResourceLocation.parse("create:cog/encasing"), KineticsScenes::cogwheelsCanBeEncased);
        HELPER.forComponents(NorthstarBlocks.IRON_LARGE_COGWHEEL)
                .addStoryBoard(ResourceLocation.parse("create:cog/large"), KineticsScenes::largeCogAsRelay, AllCreatePonderTags.KINETIC_RELAYS)
                .addStoryBoard(ResourceLocation.parse("create:cog/speedup"), KineticsScenes::cogsSpeedUp)
                .addStoryBoard(ResourceLocation.parse("create:cog/encasing"), KineticsScenes::cogwheelsCanBeEncased);*/

        HELPER.forComponents(NorthstarBlocks.ROCKET_STATION, NorthstarBlocks.ROCKET_CONTROLS)
                .addStoryBoard("rocket", RocketStationPonder::program);
    }

}
