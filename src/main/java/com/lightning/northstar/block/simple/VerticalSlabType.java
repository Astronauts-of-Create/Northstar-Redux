package com.lightning.northstar.block.simple;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum VerticalSlabType implements StringRepresentable {

    NORTH("north"),
    WEST("west"),
    EAST("east"),
    SOUTH("south"),
    DOUBLE("double");

    public static final VerticalSlabType[] ALL = values();

    private final String name;

    VerticalSlabType(String name) {
        this.name = name;
    }

    public static Direction toDir(VerticalSlabType type) {
        return switch (type) {
            case SOUTH -> Direction.SOUTH;
            case EAST -> Direction.EAST;
            case WEST -> Direction.WEST;
            default -> Direction.NORTH;
        };
    }

    public static VerticalSlabType fromDir(Direction dir) {
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