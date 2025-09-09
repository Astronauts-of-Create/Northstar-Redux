package com.lightning.northstar.accessor;

import com.lightning.northstar.world.NorthstarOxygen;
import com.lightning.northstar.world.NorthstarTemperature;

public interface NorthstarLevel {

    NorthstarTemperature northstar$temperature();

    NorthstarOxygen northstar$oxygen();

}
