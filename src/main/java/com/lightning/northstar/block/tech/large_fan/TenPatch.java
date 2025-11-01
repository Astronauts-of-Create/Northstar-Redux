package com.lightning.northstar.block.tech.large_fan;

import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/** nine-patch but with an extra value for single block structures */
public enum TenPatch implements StringRepresentable {

    SINGLE(PatchType.SINGLE, 0),
    CENTER(PatchType.CENTER, 0),
    SIDE_T(PatchType.SIDE, 0),
    SIDE_R(PatchType.SIDE, 270),
    SIDE_B(PatchType.SIDE, 180),
    SIDE_L(PatchType.SIDE, 90),
    CORNER_TR(PatchType.CORNER, 90),
    CORNER_BR(PatchType.CORNER, 0),
    CORNER_BL(PatchType.CORNER, 270),
    CORNER_TL(PatchType.CORNER, 180);

    public static final EnumProperty<TenPatch> PROPERTY = EnumProperty.create("patch", TenPatch.class);
    private static final TenPatch[] ALL = values();

    public final PatchType type;
    public final int rotation;
    public final Direction[] directions;

    TenPatch(PatchType type, int rotation) {
        this.type = type;
        this.rotation = rotation;
        this.directions = new Direction[3];

        for (Direction.Axis axis : Direction.Axis.values()) {
            Direction dir = axis.isHorizontal() ? Direction.UP : Direction.NORTH;
            for (int i = 0; i < rotation; i += 90) {
                dir = dir.getCounterClockWise(axis);
            }
            directions[axis.ordinal()] = dir;
        }
    }

    public Direction getDirection(Direction.Axis axis) {
        return directions[axis.ordinal()];
    }

    public static TenPatch pickPatch(Direction.Axis axis, Predicate<Direction> predicate) {
        Direction top = axis.isHorizontal() ? Direction.UP : Direction.NORTH;

        return switch (getConnectionCount(top, axis, predicate)) {
            case 4 -> CENTER;
            case 3 -> ALL[SIDE_T.ordinal() + getFirstMissingConnection(top, axis, predicate)];
            case 2 -> ALL[CORNER_TR.ordinal() + getCorner(top, axis, predicate)];
            default -> SINGLE;
        };
    }

    private static int getCorner(Direction direction, Direction.Axis axis, Predicate<Direction> predicate) {
        for (int i = 0; i < 4; i++) {
            Direction next = direction.getClockWise(axis);
            if (!predicate.test(direction) && !predicate.test(next))
                return i;
            direction = next;
        }
        return -1;
    }

    private static int getFirstMissingConnection(Direction direction, Direction.Axis axis, Predicate<Direction> predicate) {
        for (int i = 0; i < 4; i++) {
            if (!predicate.test(direction))
                return i;
            direction = direction.getClockWise(axis);
        }
        return -1;
    }

    private static int getConnectionCount(Direction direction, Direction.Axis axis, Predicate<Direction> predicate) {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            if (predicate.test(direction))
                count++;
            direction = direction.getClockWise(axis);
        }
        return count;
    }

    @Override
    public @NotNull String getSerializedName() {
        return Lang.asId(name());
    }

    public enum PatchType {
        CENTER,
        SINGLE,
        SIDE,
        CORNER
    }

}
