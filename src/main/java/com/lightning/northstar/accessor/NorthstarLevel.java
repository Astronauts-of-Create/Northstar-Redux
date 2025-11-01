package com.lightning.northstar.accessor;

import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import it.unimi.dsi.fastutil.longs.LongCollection;

public interface NorthstarLevel {

    default NorthstarTemperature northstar$temperature() {
        throw new RuntimeException("This should be implemented by a mixin!");
    }

    default NorthstarOxygen northstar$oxygen() {
        throw new RuntimeException("This should be implemented by a mixin!");
    }

    // TODO: this currently causes both oxygen and temperature to schedule block updates
    //  despite likely sharing a common volume
    default void northstar$queueBlockUpdates(LongCollection positions) {
        throw new RuntimeException("This should be implemented by a mixin!");
    }

}
