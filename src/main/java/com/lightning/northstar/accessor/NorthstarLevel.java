package com.lightning.northstar.accessor;

import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.NorthstarTemperature;

public interface NorthstarLevel {

    default NorthstarTemperature northstar$temperature() {
        throw new RuntimeException("This should be implemented by a mixin!");
    }

    default NorthstarOxygen northstar$oxygen() {
        throw new RuntimeException("This should be implemented by a mixin!");
    }

}
