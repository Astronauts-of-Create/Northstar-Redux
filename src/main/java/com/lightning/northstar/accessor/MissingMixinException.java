package com.lightning.northstar.accessor;

public class MissingMixinException extends RuntimeException {

    public MissingMixinException() {
        super("This method should've been implemented by a mixin but wasn't. This usually indicates broken mod loading or missing mixins (disabled by other mods?).");
    }

}
