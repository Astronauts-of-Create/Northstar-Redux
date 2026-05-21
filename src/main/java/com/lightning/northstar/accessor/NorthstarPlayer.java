package com.lightning.northstar.accessor;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface NorthstarPlayer {

    default void northstar$showTitle(Component title, Component subtitle, int fadeInTime, int displayTime, int fadeOutTime) {
        throw new MissingMixinException();
    }

}
