package com.lightning.northstar.contraption.rocket;

import com.lightning.northstar.util.NorthstarCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.StreamCodec;
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

    public static final Codec<RocketDestination> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("dimension").forGetter(RocketDestination::dim),
            BlockPos.CODEC.optionalFieldOf("position").forGetter(dest -> Optional.ofNullable(dest.pos())),
            Direction.CODEC.optionalFieldOf("direction").forGetter(dest -> Optional.ofNullable(dest.dir()))
    ).apply(i, (dim, pos, dir) -> new RocketDestination(dim, pos.orElse(null), dir.orElse(null))));

    public static final StreamCodec<ByteBuf, RocketDestination> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, RocketDestination::dim,
            NorthstarCodecs.nullableStream(BlockPos.STREAM_CODEC), RocketDestination::pos,
            NorthstarCodecs.nullableStream(Direction.STREAM_CODEC), RocketDestination::dir,
            RocketDestination::new
    );

    public ResourceKey<Level> dimKey() {
        return ResourceKey.create(Registries.DIMENSION, dim);
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
        BlockPos pos = NbtUtils.readBlockPos(tag, "Position").orElse(null);
        Direction dir = Direction.byName(tag.getString("Direction"));
        return dim == null ? null : new RocketDestination(dim, pos, dir);
    }

}
