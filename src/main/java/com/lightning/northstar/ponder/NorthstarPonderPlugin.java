package com.lightning.northstar.ponder;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.ponder.scene.CombustionEnginePonder;
import com.lightning.northstar.ponder.scene.OxygenFillerPonder;
import com.lightning.northstar.ponder.scene.RocketStationPonder;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;

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

        HELPER.forComponents(NorthstarBlocks.COMBUSTION_ENGINE)
                .addStoryBoard("combustion_engine", CombustionEnginePonder::program, AllPonderTags.KINETIC_SOURCES);

        HELPER.forComponents(NorthstarBlocks.OXYGEN_FILLER)
                .addStoryBoard("oxygen_filler", OxygenFillerPonder::program, AllPonderTags.KINETIC_SOURCES);

        HELPER.forComponents(NorthstarBlocks.ROCKET_STATION, NorthstarBlocks.ROCKET_CONTROLS, NorthstarBlocks.ROCKET_THRUSTER, NorthstarBlocks.INTERPLANETARY_NAVIGATOR, NorthstarBlocks.AUTO_LANDER)
                .addStoryBoard("rocket", RocketStationPonder::program);
    }

}
