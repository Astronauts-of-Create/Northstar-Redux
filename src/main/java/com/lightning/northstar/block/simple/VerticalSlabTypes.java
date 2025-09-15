package com.lightning.northstar.block.simple;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum VerticalSlabTypes implements StringRepresentable {

    NORTH("north"),
    WEST("west"),
    EAST("east"),
    SOUTH("south"),
    DOUBLE("double");

    private final String name;

    VerticalSlabTypes(String name) {
        this.name = name;
    }

    public static Direction toDir(VerticalSlabTypes type) {
        return switch (type) {
            case SOUTH -> Direction.SOUTH;
            case EAST -> Direction.EAST;
            case WEST -> Direction.WEST;
            default -> Direction.NORTH;
        };
    }

    public static VerticalSlabTypes fromDir(Direction dir) {
        return switch (dir) {
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            default -> NORTH;
        };
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

}