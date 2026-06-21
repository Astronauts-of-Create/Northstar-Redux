package com.lightning.northstar.network.packet;

import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraption.rocket.RocketDestination;
import com.lightning.northstar.item.atlas.SpaceAtlasContent;
import com.lightning.northstar.item.atlas.SpaceAtlasMenu;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SpaceAtlasEditPacket(
        Map<RocketDestination, String> renamed,
        List<RocketDestination> removed
) implements ServerboundPacketPayload {

    public static final int LABEL_MAX_LENGTH = 128;

    public static final StreamCodec<ByteBuf, SpaceAtlasEditPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, RocketDestination.STREAM_CODEC, ByteBufCodecs.stringUtf8(LABEL_MAX_LENGTH)), SpaceAtlasEditPacket::renamed,
            RocketDestination.STREAM_CODEC.apply(ByteBufCodecs.list()), SpaceAtlasEditPacket::removed,
            SpaceAtlasEditPacket::new
    );

    @Override
    public void handle(ServerPlayer player) {
        if (player.containerMenu instanceof SpaceAtlasMenu menu) {
            SpaceAtlasContent.Builder content = menu.contentHolder.getOrDefault(NorthstarDataComponents.SPACE_ATLAS_CONTENT, SpaceAtlasContent.EMPTY).asBuilder();

            for (Map.Entry<RocketDestination, String> entry : renamed.entrySet()) {
                content.getDestinations().computeIfPresent(entry.getKey(), (k, v) -> Component.literal(entry.getValue()));
            }

            content.getDestinations().keySet().removeIf(removed::contains);

            menu.contentHolder.set(NorthstarDataComponents.SPACE_ATLAS_CONTENT, content.build());
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.UPDATE_SPACE_ATLAS;
    }

}
