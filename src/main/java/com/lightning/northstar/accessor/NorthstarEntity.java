package com.lightning.northstar.accessor;

public interface NorthstarEntity {

    default void northstar$onResourceReload() {
        throw new MissingMixinException();
    }

}
