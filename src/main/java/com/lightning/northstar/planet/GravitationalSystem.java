package com.lightning.northstar.planet;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public record GravitationalSystem(
        Planet root,
        List<Planet> planets
) implements Iterable<Planet> {

    @Override
    public @NotNull Iterator<Planet> iterator() {
        return planets.iterator();
    }

}
