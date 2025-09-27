package com.lightning.northstar.config;

import com.simibubi.create.foundation.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class ServerConfig extends ConfigBase {

    public final ConfigGroup sealer = group(1, "Oxygen sealer & Temperature regulator");

    public final ConfigInt sealerMaxBlocksPerTick = i(100, 1, "maxSealedBlocksPerTick");
    public final ConfigInt sealerCheckDelay = i(20, 1, "checkDelay");

    public final ConfigInt oxygenSealerBlocksPerRpm = i(20, 1, "oxygenSealerBlocksPerRpm");
    // 0.001 mB per block per tick is 1 mB per tick for 100 blocks or 20 mB per second for 1000 blocks
    public final ConfigFloat oxygenSealerOxygenPerBlockPerTick = f(0.001f, 0, "oxygenPerBlockPerTick");
    public final ConfigInt oxygenSealerMaxContraptionSealed = i(2_500, "oxygenSealerMaxContraptionSealed");

    public final ConfigInt temperatureRegulatorBlocksPerRpm = i(20, 1, "temperatureRegulatorBlocksPerRpm");
    public final ConfigInt temperatureRegulatorMaxContraptionSealed = i(2_500, "temperatureRegulatorMaxContraptionSealed");

    @Override
    public @NotNull String getName() {
        return "server";
    }

}
