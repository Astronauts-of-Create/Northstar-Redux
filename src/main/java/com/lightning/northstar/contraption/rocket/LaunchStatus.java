package com.lightning.northstar.contraption.rocket;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum LaunchStatus implements StringRepresentable {

    WAITING,
    COUNTDOWN,
    ASCENDING,
    DESCENDING;

    public static final StringRepresentable.EnumCodec<LaunchStatus> CODEC = StringRepresentable.fromEnum(LaunchStatus::values);

    @Override
    @NotNull
    public String getSerializedName() {
        return name().toLowerCase();
    }

    public static LaunchStatus byName(String name) {
        return CODEC.byName(name);
    }

}
