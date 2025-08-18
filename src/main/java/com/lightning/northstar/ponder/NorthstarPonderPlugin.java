package com.lightning.northstar.ponder;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarTechBlocks;
import com.lightning.northstar.ponder.scene.RocketStationPonder;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class NorthstarPonderPlugin implements PonderPlugin {

    @Override
    public @NotNull String getModId() {
        return Northstar.MOD_ID;
    }

    @Override
    public void registerScenes(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        // FIXME: those register under northstar and are missing the translation keys
        /*HELPER.forComponents(NorthstarTechBlocks.IRON_COGWHEEL)
                .addStoryBoard(ResourceLocation.parse("create:cog/small"), KineticsScenes::cogAsRelay, AllCreatePonderTags.KINETIC_RELAYS)
                .addStoryBoard(ResourceLocation.parse("create:cog/speedup"), KineticsScenes::cogsSpeedUp)
                .addStoryBoard(ResourceLocation.parse("create:cog/encasing"), KineticsScenes::cogwheelsCanBeEncased);
        HELPER.forComponents(NorthstarTechBlocks.IRON_LARGE_COGWHEEL)
                .addStoryBoard(ResourceLocation.parse("create:cog/large"), KineticsScenes::largeCogAsRelay, AllCreatePonderTags.KINETIC_RELAYS)
                .addStoryBoard(ResourceLocation.parse("create:cog/speedup"), KineticsScenes::cogsSpeedUp)
                .addStoryBoard(ResourceLocation.parse("create:cog/encasing"), KineticsScenes::cogwheelsCanBeEncased);*/

        HELPER.forComponents(NorthstarTechBlocks.ROCKET_STATION, NorthstarTechBlocks.ROCKET_CONTROLS)
                .addStoryBoard("rocket", RocketStationPonder::program, AllCreatePonderTags.MOVEMENT_ANCHOR);
    }

    @Override
    public void registerTags(@NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
        NorthstarPonderTags.register(helper);
    }

}
