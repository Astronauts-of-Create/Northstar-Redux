package com.lightning.northstar.network.packet;

import com.lightning.northstar.contraption.rocket.RocketDestination;
import com.lightning.northstar.item.atlas.SpaceAtlasContent;
import com.lightning.northstar.item.atlas.SpaceAtlasMenu;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Map;

public class SpaceAtlasEditPacket extends SimplePacketBase {

    public static final int LABEL_MAX_LENGTH = 128;

    public final Map<RocketDestination, String> renamed;
    public final List<RocketDestination> removed;

    public SpaceAtlasEditPacket(Map<RocketDestination, String> renamed, List<RocketDestination> removed) {
        this.renamed = renamed;
        this.removed = removed;
    }

    public SpaceAtlasEditPacket(FriendlyByteBuf buffer) {
        this(buffer.readMap(RocketDestination::new, buf -> buf.readUtf(LABEL_MAX_LENGTH)), buffer.readList(RocketDestination::new));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeMap(renamed, (buf, dest) -> dest.writeBuffer(buf), (buf, label) -> buf.writeUtf(label, LABEL_MAX_LENGTH));
        buffer.writeCollection(removed, (buf, dest) -> dest.writeBuffer(buf));
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof SpaceAtlasMenu menu) {
                CompoundTag tag = menu.contentHolder.getOrCreateTag();
                SpaceAtlasContent content = SpaceAtlasContent.fromTag(tag);

                for (Map.Entry<RocketDestination, String> entry : renamed.entrySet()) {
                    content.destinations.computeIfPresent(entry.getKey(), (k, v) -> Component.literal(entry.getValue()));
                }

                content.destinations.keySet().removeIf(removed::contains);

                content.toTag(tag);
            }
        });
        return false;
    }

}
