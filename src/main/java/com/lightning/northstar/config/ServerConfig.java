package com.lightning.northstar.config;

import com.simibubi.create.foundation.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class ServerConfig extends ConfigBase {

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

    private interface Comments {
        String LARGE_FAN_FORMULA = "A constant used to calculate the volume added by large fans using the following formula: (|speed| / 256 * 64) * size^sizeExp * C * (1 + log2(blades))";
    }

}
