package com.lightning.northstar.accessor;

// Not a mixin directly to prevent issues with default methods and having to manually cast stuff.
public interface NorthstarGroundPathNavigation {

    default boolean northstar$isAvoidSun() {
        throw new MissingMixinException();
    }

}
