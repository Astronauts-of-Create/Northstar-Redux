package com.lightning.northstar.config;

import net.createmod.catnip.config.ConfigBase;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ServerConfig extends ConfigBase {

    public final ConfigInt spaceAtlasMaxWaypoints = i(256, 0, "spaceAtlasMaxWaypoints", "The maximum waypoints the space atlas can hold");

    public final ConfigGroup atmosphere = group(1, "atmosphere");

    // For default worlds (max Y = 320), fade at 800 until 1000 and change dimension at 2000
    public final ConfigInt atmosphereBaseHeight = i(480, "atmosphereBaseHeight", "The height after which the atmosphere starts to fade out. Relative to the maximum build height.");
    public final ConfigInt atmosphereThickness = i(200, 1, "atmosphereThickness", "The thickness of the atmosphere");
    public final ConfigInt atmosphereTeleportHeight = i(1000, 0, "atmosphereTeleportHeight", "The height at which the rocket changes dimension, relative to the atmosphere base height + thickness");
    public final ConfigBool allowDimensionTraversal = b(true, "allowDimensionTraversal");

    public final ConfigGroup rocket = group(1, "rocket");

    public final ConfigBool enableReturnTicketCreation = b(true, "enableReturnTicketCreation", "Wether return tickets should be created after the rocket lands.");
    public final ConfigFloat targetingComputerEfficiency = f(0.4f, 0f, 1f, "targetingComputerEfficiency", "The total fuel efficiency applied for the maximum amount of targeting computers");
    public final ConfigInt targetingComputersNeeded = i(48, 0, "targetingComputersNeeded", "The amount of targeting computers required to reach maximum efficiency");
    public final ConfigInt launchCountdownRadius = i(20, 0, "launchCountdownRadius", "From how far away should the launch countdown messages/sound be announced to players");
    public final ConfigFloat thrusterPower = f(500, 1, "thrusterPower", "The engine's force in Newtons (kg/m/s^2) assuming rocket weight is in kg");
    public final ConfigFloat landingMaxSafeSpeed = f(5, 0, "landingMaxSafeSpeed", "The maximum safe speed in blocks/second the rocket can move at before exploding on landing");
    public final ConfigFloat rocketExplosionFraction = f(0.1f, 0, 1, "rocketExplosionFraction", "The fraction of blocks to explode when a rocket crash lands. Set to zero to disable.");
    public final ConfigInt rocketExplosionSize = i(10, 1, "rocketExplosionSize", "The size of individual explosions when a rocket crash lands.");

    public final ConfigGroup rocketFuel = group(2, "fuel", "Fuel properties for rockets");

    public final ConfigFloat takeoffFuelScale = f(30, 0, "takeoffFuelScale");
    public final ConfigFloat landingFuelScale = f(10, 0, "landingFuelScale");
    public final ConfigFloat travelFuelScale = f(10, 0, "travelFuelScale");

    public final ConfigGroup gameplay = group(1, "gameplay");

    public final ConfigBool doDripstoneDripWithoutGravity = b(false, "doDripstoneDripWithoutGravity", "Wether to allow dripstones to drip in places with no gravity");
    public final ConfigBool doDripstoneGrowWithoutGravity = b(false, "doDripstoneGrowWithoutGravity", "Wether to allow dripstones to grow in places with no gravity");
    public final ConfigFloat zeroGravityJumpStrength = f(0.2f, 0.0f, "zeroGravityJumpStrength", "The jump strength scale applied in zero gravity worlds");
    public final ConfigBool removeStalledProjectiles = b(true, "removeStalledProjectiles", "If enabled, triggers a collision with stopped projectiles in zero gravity dimensions. Causes arrows to despawn, potions / experience bottles to explode, loyalty tridents to return, etc...");

    public final ConfigGroup sealer = group(1, "sealer");

    public final ConfigInt sealerBaseCheckBlocksPerTick = i(128, 1, "baseCheckBlocksPerTick", "Base maximum blocks tested per tick, higher values increase the speed at which seals updates but decreases performance, value increases dynamically with additional blocks that might add volume.");
    public final ConfigInt sealerMaxCheckedBlocksPerTick = i(4096, 1, "maxCheckedBlocksPerTick", "Absolute maximum blocks tested per tick");
    public final ConfigInt sealerCheckDelay = i(20, 1, "checkDelay", "Delay in ticks before checking again after a seal completed");

    public final ConfigGroup oxygen = group(2, "oxygen");

    public final ConfigInt oxygenSealerBlocksPerRpm = i(16, 1, "blocksPerRpm");
    public final ConfigInt oxygenSealerMaxContraptionSealed = i(2_500, 0, "maxContraptionSealed");
    public final ConfigFloat oxygenSealerPassiveDrain = f(0.0001f, 0, "passiveDrain", "Passive oxygen drain in mB per block per tick");
    public final ConfigFloat oxygenSealerBlockActiveDrain = f(0.01f, 0, "blockActiveDrain", "Active drain scaling in mB per block per tick. Applies to block with dynamic consumption like fire, torches, leaves, etc...");
    public final ConfigFloat oxygenSealerEntityActiveDrain = f(0.05f, 0, "entityActiveDrain", "Active drain for entity in mB per entity per tick.");

    public final ConfigGroup temperature = group(2, "temperature");

    public final ConfigInt temperatureRegulatorBlocksPerRpm = i(16, 1, "blocksPerRpm");
    public final ConfigInt temperatureRegulatorMaxContraptionSealed = i(2_500, 0, "maxContraptionSealed");

    public final ConfigGroup largeFan = group(1, "largeFan");

    public final ConfigInt largeFanMaxSize = i(5, 1, "maxSize", "Maximum size of large fans.");
    public final ConfigFloat largeFanSizeExponent = f(2.2f, 0, "sizeExp", Comments.LARGE_FAN_FORMULA);
    public final ConfigFloat largeFanMultiplier = f(12, 0, "multiplier", Comments.LARGE_FAN_FORMULA);

    @Override
    public @NotNull String getName() {
        return "server";
    }

    public int getCombinedTeleportHeight() {
        return atmosphereBaseHeight.get() + atmosphereThickness.get() + atmosphereTeleportHeight.get();
    }

    public float calculateAtmosphereBlend(Level level, double y) {
        return calculateAtmosphereBlend(level.getMaxBuildHeight(), y);
    }

    public float calculateAtmosphereBlend(int worldHeight, double y) {
        int atmosphereHeight = worldHeight + atmosphereBaseHeight.get();
        int thickness = atmosphereThickness.get();
        return (float) Mth.clamp(Mth.inverseLerp(y, atmosphereHeight, atmosphereHeight + thickness), 0, 1);
    }

}
