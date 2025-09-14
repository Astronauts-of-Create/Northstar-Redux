package com.lightning.northstar.client;

public enum TilingAnchor {

    BOTTOM_LEFT(true, true),
    BOTTOM_RIGHT(false, true),
    TOP_LEFT(true, false),
    TOP_RIGHT(false, false),
    ;

    public final boolean left;
    public final boolean bottom;

    TilingAnchor(boolean left, boolean bottom) {
        this.left = left;
        this.bottom = bottom;
    }

}
