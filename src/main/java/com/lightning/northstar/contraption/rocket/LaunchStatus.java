package com.lightning.northstar.contraption.rocket;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum LaunchStatus implements StringRepresentable {

    WAITING,
    COUNTDOWN,
    ASCENDING,
    DESCENDING;

    public static final StringRepresentable.EnumCodec<LaunchStatus> CODEC = StringRepresentable.fromEnum(LaunchStatus::values);
    public static final IntFunction<LaunchStatus> BY_ID = ByIdMap.continuous(LaunchStatus::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, LaunchStatus> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, LaunchStatus::ordinal);

    @Override
    @NotNull
    public String getSerializedName() {
        return name().toLowerCase();
    }

    public static LaunchStatus byName(String name) {
        return CODEC.byName(name);
    }

}
