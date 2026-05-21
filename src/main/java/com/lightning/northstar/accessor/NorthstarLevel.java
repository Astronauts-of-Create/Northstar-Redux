package com.lightning.northstar.accessor;

import com.lightning.northstar.planet.Planet;
import com.lightning.northstar.planet.PlanetTracker;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import it.unimi.dsi.fastutil.longs.LongCollection;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface NorthstarLevel {

    // Two trackers are needed to prevent concurrency issues with the integrated server in singleplayer worlds.
    PlanetTracker SERVER_TRACKER = new PlanetTracker();
    PlanetTracker CLIENT_TRACKER = new PlanetTracker();

    default PlanetTracker northstar$getPlanetTracker() {
        return ((Level) this).isClientSide() ? CLIENT_TRACKER : SERVER_TRACKER;
    }

    default NorthstarTemperature northstar$temperature() {
        throw new MissingMixinException();
    }

    default NorthstarOxygen northstar$oxygen() {
        throw new MissingMixinException();
    }

    default void northstar$queueBlockUpdates(LongCollection positions) {
        throw new MissingMixinException();
    }

    @ApiStatus.Internal
    default void northstar$onResourceReload() {
        throw new MissingMixinException();
    }

    @Nullable
    default Planet northstar$planet() {
        throw new MissingMixinException();
    }

    default PlanetDimension northstar$dimension() {
        throw new MissingMixinException();
    }

    default float northstar$gravity() {
        return northstar$dimension().gravity();
    }

    default float northstar$gravityScale() {
        throw new MissingMixinException();
    }

    default boolean northstar$isZeroGravity() {
        throw new MissingMixinException();
    }

}
