package com.lightning.northstar.accessor;

public interface NorthstarMinecraft {

    default boolean northstar$shouldRenderLevel() {
        throw new MissingMixinException();
    }

}
