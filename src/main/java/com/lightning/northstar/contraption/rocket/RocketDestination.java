package com.lightning.northstar.contraption.rocket;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

/**
 * @param dim the target dimension, never null
 * @param pos the waypoint position, if null the rocket stays at the same position
 * @param dir the waypoint direction
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record RocketDestination(
        ResourceLocation dim,
        @Nullable BlockPos pos,
        @Nullable Direction dir
) {

    public RocketDestination(FriendlyByteBuf buffer) {
        this(
                buffer.readResourceLocation(),
                buffer.readOptional(FriendlyByteBuf::readBlockPos).orElse(null),
                buffer.readOptional(buf -> buf.readEnum(Direction.class)).orElse(null)
        );
    }

    public ResourceKey<Level> dimKey() {
        return ResourceKey.create(Registries.DIMENSION, dim);
    }

    public void writeBuffer(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(dim);
        buffer.writeOptional(Optional.ofNullable(pos), FriendlyByteBuf::writeBlockPos);
        buffer.writeOptional(Optional.ofNullable(dir), FriendlyByteBuf::writeEnum);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Dimension", dim.toString());
        if (pos != null) tag.put("Position", NbtUtils.writeBlockPos(pos));
        if (dir != null) tag.putString("Direction", dir.getSerializedName());
        return tag;
    }

    @Nullable
    public static RocketDestination fromTag(CompoundTag tag) {
        ResourceLocation dim = tag.contains("Dimension", Tag.TAG_STRING) ? ResourceLocation.tryParse(tag.getString("Dimension")) : null;
        BlockPos pos = tag.contains("Position", Tag.TAG_COMPOUND) ? NbtUtils.readBlockPos(tag.getCompound("Position")) : null;
        Direction dir = Direction.byName(tag.getString("Direction"));
        return dim == null ? null : new RocketDestination(dim, pos, dir);
    }

}
