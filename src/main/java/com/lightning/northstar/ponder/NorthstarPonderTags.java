package com.lightning.northstar.ponder;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarBlocks;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class NorthstarPonderTags {

    public static final ResourceLocation
            SPACE_EXPLORATION = Northstar.asResource("space_exploration");

    public static void register(@NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        helper.registerTag(SPACE_EXPLORATION)
                .addToIndex()
                .item(NorthstarItems.IRON_SPACE_SUIT_HELMET, true, false)
                .title("Space exploration")
                .description("Components used for space exploration.")
                .register();

        HELPER.addToTag(SPACE_EXPLORATION)
                // see comment in NorthstarPonderPlugin
                //.add(NorthstarBlocks.IRON_COGWHEEL)
                //.add(NorthstarBlocks.IRON_LARGE_COGWHEEL)
                .add(NorthstarBlocks.ROCKET_CONTROLS);
    }

}
