package com.lightning.northstar;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class ModConfig {

    // 1. Create the ConfigValue to hold the boolean
    private static final ForgeConfigSpec.BooleanValue DISMOUNT_RIDEABLE_ENTITIES_IN_ROCKETS;

    // We use a Builder to construct the configuration
    public static final ForgeConfigSpec SPEC;

    // This static block runs once when the class is loaded to define the config structure
    static {
        // Create a new builder
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        // Use 'push' to create a section in the TOML file (e.g., [boat_settings])
        COMMON_BUILDER.push("northstar_common");

        // 2. Define the boolean config option
        DISMOUNT_RIDEABLE_ENTITIES_IN_ROCKETS = COMMON_BUILDER
            .comment("Set to 'true' to dismount players when inside a rocket (default: Shift).")
            .define("dismount_rideable_entities_in_rockets", false);

        // Pop the section to return to the root of the file structure
        COMMON_BUILDER.pop();

        // 3. Build the final specification
        SPEC = COMMON_BUILDER.build();
    }

    public static boolean dismountRideableEntitiesInRockets;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == ModConfig.SPEC) {
            dismountRideableEntitiesInRockets = DISMOUNT_RIDEABLE_ENTITIES_IN_ROCKETS.get();
        }
    }
}