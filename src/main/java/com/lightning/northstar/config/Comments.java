package com.lightning.northstar.config;

/** Comments commonly reused across the configuration files. */
public interface Comments {

    String LARGE_FAN_FORMULA = "A constant used to calculate the volume added by large fans using the following formula: (|speed| / 256 * 64) * size^sizeExp * C * (1 + log2(blades))";

}
